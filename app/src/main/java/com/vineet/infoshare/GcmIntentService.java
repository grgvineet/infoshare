package com.vineet.infoshare;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

/**
 * Created by vineet on 04-Sep-14.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    final String TAG = "tag" ;

    Bundle extras;
    DatabaseManager mydb;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        mydb = new DatabaseManager(this);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                System.out.println("Recieved bundle :" + extras.toString());
                // Post notification of received message.
                if (!ForegroundTester.isInForeground()) {
                    sendNotification("Received: " + extras.getString("Message").replace("\\", ""));

                    try {
                        String temp;
                        temp = extras.getString("Message");
                        temp = temp.replace("\\", "");
                        System.out.println("Temp :" + temp);
                        JSONObject messageRecieved = new JSONObject(temp);
//                        String time = Calendar.getInstance().get(Calendar.HOUR) + ":" + ((Calendar.getInstance().get(Calendar.MINUTE) < 10) ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + " " + ((Calendar.getInstance().get(Calendar.AM_PM) == 1) ? "PM" : "AM");
                        mydb.open();
                        mydb.createEntry(messageRecieved.getString("category"), "text", messageRecieved.getString("message"), messageRecieved.getString("name"), 0, messageRecieved.getString("time"));
                        mydb.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.i(TAG, "Received: " + extras.getString("Message"));
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String msg) {


        try {

                mNotificationManager = (NotificationManager)
                        this.getSystemService(Context.NOTIFICATION_SERVICE);

                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, Categories.class), 0);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("Bulletin")
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(msg))
                                .setContentText(msg);
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        }catch (Exception e){
            e.printStackTrace();
        }


//        mNotificationManager.cancel(NOTIFICATION_ID);

    }
}
