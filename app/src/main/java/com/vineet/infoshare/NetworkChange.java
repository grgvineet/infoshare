package com.vineet.infoshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NetworkChange extends BroadcastReceiver {

    DatabaseManager mydb;
    Cursor res;
    Context context;

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_TYPE = "type";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_TIME = "time";
    public static final String KEY_STATUS = "status" ;


    MySharedPreferences mySharedPreferences;
    NetManager netManager;

    public NetworkChange() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        mySharedPreferences = new MySharedPreferences(context);
        netManager = new NetManager(context);
        try {
            this.context = context;
            if (netManager.isNetworkAvailable()) {
                mydb = new DatabaseManager(context);
                mydb.open();
                res = mydb.getNotSentMessages();
                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    new SendMessage().execute(res);
                    res.moveToNext();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        throw new UnsupportedOperationException("Not yet implemented");
    }



    private class SendMessage extends AsyncTask<Cursor,Void,String> {

        String regid = "";
        Cursor backGroundCursor;
        //        Bundle messageBundle = new Bundle();
        JSONObject messageJSON = new JSONObject();
        String time;

        @Override
        protected String doInBackground(Cursor... cursors) {

            HttpClient httpClient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost("http://192.168.1.23/bulletin/send_message.php");
            HttpPost httpPost = new HttpPost("http://http://bulletin.comeze.com/send_message.php");

            backGroundCursor = cursors[0];

            try {
                time = Calendar.getInstance().get(Calendar.HOUR)+ ":" + ((Calendar.getInstance().get(Calendar.MINUTE) < 10)? "0" + Calendar.getInstance().get(Calendar.MINUTE):Calendar.getInstance().get(Calendar.MINUTE)) + " " +((Calendar.getInstance().get(Calendar.AM_PM) == 1)? "PM": "AM");
                messageJSON.put("name", mySharedPreferences.getName());
                messageJSON.put("category",backGroundCursor.getString(backGroundCursor.getColumnIndex(KEY_CATEGORY)));
                messageJSON.put("message",backGroundCursor.getString(backGroundCursor.getColumnIndex(KEY_MESSAGE)));
                messageJSON.put("time",time);
            }catch (Exception e){
                e.printStackTrace();
            }

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("regId", regid));
            nameValuePair.add(new BasicNameValuePair("message", messageJSON.toString()));
            nameValuePair.add(new BasicNameValuePair("organisation",mySharedPreferences.getOrganisation()));


            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);
                // write response to log
                Log.d("Http Post Response:", EntityUtils.toString(response.getEntity()));
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
                Log.e("Send Message","Client protocol exception");
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }

            System.out.println("Message sent");
            return "Message sent";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            messagesScrollView.fullScroll(View.FOCUS_DOWN);

            if (backGroundCursor.isLast()){
                mydb.close();
            }
        }
    }


}
