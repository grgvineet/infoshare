package com.vineet.infoshare;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.util.Calendar;

public class ShowMessages extends Activity implements View.OnClickListener{

    Bundle extras;
//    TextView textView;
    ImageButton send;
    EditText message;
    String regid,category;
    ActionBar actionBar;
    ListView listView;
    CustomAdapter customAdapter;
    RelativeLayout messagesRootLayout;
//    ScrollView messagesScrollView;

    Context context;

    DatabaseManager mydb;

    MySharedPreferences mySharedPreferences;
    NetManager netManager;



    BroadcastReceiver gcmReciever;

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);

        context = this;

        mydb = new DatabaseManager(this);
        mySharedPreferences = new MySharedPreferences(this);
        netManager = new NetManager(this);

        extras = getIntent().getExtras();
        category = extras.getString("Key");
        regid = mySharedPreferences.getRegistrationId();
        actionBar = getActionBar();

        ForegroundTester.setForegroundTrue(category);

//        setContentView(R.layout.activity_show_messages);

        category = getIntent().getExtras().getString("Key");

        actionBar.setTitle(category);
        System.out.println("ShowMessagesTemp :" + category);

        listView = (ListView)findViewById(R.id.lvMessages);
        customAdapter = new CustomAdapter(this,category);

        listView.setAdapter(customAdapter);


//        textView = (TextView)findViewById(R.id.tvShowMessagesHello);
        send = (ImageButton)findViewById(R.id.bSendMessage);
        message = (EditText)findViewById(R.id.etSendMessage);
        messagesRootLayout = (RelativeLayout)findViewById(R.id.lMessagesRootLayout);
        messagesRootLayout.setBackground(WallpaperManager.getInstance(context).getDrawable());
//        messagesScrollView = (ScrollView)findViewById(R.id.svMessagesScrollView);

//        textView.setText(extras.getString("Key","Value not found"));
        send.setOnClickListener(this);
//        messagesScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
//                messagesScrollView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        messagesScrollView.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        });

//        mydb.open();
////        ArrayList arrayList = mydb.getAllMessages(category);
//        Cursor res = mydb.getAllMessages(category);
//        res.moveToFirst();
////        for (int i=0 ; i<arrayList.size() ; i++){
////            TextView tv = new TextView(this);
////            tv.setText(arrayList.get(i).toString());
////            tv.setGravity(Gravity.LEFT);
////            messagesLayout.addView(tv);
////        }
//        while (res.isAfterLast() == false){
//            TextView tv = new TextView(this);
//            tv.setText(res.getString(res.getColumnIndex("category")));
//            tv.setGravity(Gravity.LEFT);
//            messagesLayout.addView(tv);
//            res.moveToNext();
//        }
//        mydb.close();

//        messagesScrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                messagesScrollView.fullScroll(View.FOCUS_DOWN);
//            }
//        });


        RelativeLayout lBottomSend;
        lBottomSend = (RelativeLayout)findViewById(R.id.lBottomSend);


        gcmReciever = new BroadcastReceiver() {
            Bundle gcmBundle;
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("new reciver recieved data");
                gcmBundle = intent.getExtras();
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                // The getMessageType() intent parameter must be the intent you received
                // in your BroadcastReceiver.
                String messageType = gcm.getMessageType(intent);

                if (!gcmBundle.isEmpty()) {  // has effect of unparcelling Bundle

                    if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                        System.out.println("Recieved bundle :" + gcmBundle.toString());
                        // Post notification of received message.

                        try {
                            JSONObject messageRecieved = new JSONObject(gcmBundle.getString("Message").replace("\\",""));
                            if (messageRecieved.getString("category").equals(category)) {
                                String time = Calendar.getInstance().get(Calendar.HOUR) + ":" + ((Calendar.getInstance().get(Calendar.MINUTE) < 10) ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + " " + ((Calendar.getInstance().get(Calendar.AM_PM) == 1) ? "PM" : "AM");
                                mydb.open();
                                mydb.createEntry(messageRecieved.getString("category"), "text", messageRecieved.getString("message"), messageRecieved.getString("name"), 0, time);
                                mydb.close();
                                customAdapter.notifyDataSetChanged();
                                listView.setSelection(customAdapter.getCount()-1);

                                try {
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    r.play();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                sendNotification(gcmBundle.getString("Message"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
                // Release the wake lock provided by the WakefulBroadcastReceiver.



            }
        };


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
            mBuilder.setAutoCancel(true);

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bSendMessage:
                if (netManager.isNetworkAvailable()) {
                    netManager.sendMessage(message,listView,customAdapter,mySharedPreferences.getName(),category,message.getText().toString());
                }else{
                    String time = Calendar.getInstance().get(Calendar.HOUR)+ ":" + ((Calendar.getInstance().get(Calendar.MINUTE) < 10)? "0" + Calendar.getInstance().get(Calendar.MINUTE):Calendar.getInstance().get(Calendar.MINUTE)) + " " +((Calendar.getInstance().get(Calendar.AM_PM) == 1)? "PM": "AM");
                    mydb.open();
                    mydb.createEntry(category,"text",message.getText().toString(),"me",1, time);
                    mydb.close();
                    message.setText("");
                    customAdapter.notifyDataSetChanged();
                    listView.setSelection(listView.getCount() - 1);
                }
                break;

            default:
                Log.e("Show messages","Invalid view id in switch statement");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        ForegroundTester.setForegroundFalse();
        unregisterReceiver(gcmReciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gcmReciever,new IntentFilter("com.google.android.c2dm.intent.RECEIVE"));
        ForegroundTester.setForegroundTrue(category);
    }

    public void handleNewMessage(String messageJSON){

    }




//    public static void handleIncomingMessages(){
//        customAdapter.notifyDataSetChanged();
//        listView.setSelection(listView.getCount()-1);
//    }


}
