package com.vineet.infoshare;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

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

/**
 * Created by vineet on 13-Sep-14.
 */
public class NetManager {


    private final String MESSAGE_SEND_URL = "";
    private final String ORGANISATION_VERIFY_URL = "";
    private final String REGISTER_URL = "";

//    private final String MESSAGE_SEND_URL = "";
//    private final String ORGANISATION_VERIFY_URL = "";
//    private final String REGISTER_URL = "";

    Context context;
    DatabaseManager mydb;
    MySharedPreferences mySharedPreferences;

    private String sender,category,message;
    private String organisation;


    NetManager(Context context){
        this.context = context;
        mydb = new DatabaseManager(context);
        mySharedPreferences = new MySharedPreferences(context);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean verifyOrganisation(String organisation){
        return false;
    }

    public boolean sendMessage(EditText editText,ListView listView,CustomAdapter customAdapter,String sender,String category,String message){
        this.sender = sender;
        this.category = category;
        this.message = message;
        new SendMessage(editText,listView,customAdapter).execute();
        return false;
    }

    public void sendUnsentMessages(){

    }

    public boolean register(){

        return false;
    }


    private class SendMessage extends AsyncTask<Void,Void,String> {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(MESSAGE_SEND_URL);

        String time;
        //        Bundle messageBundle = new Bundle();
        JSONObject messageJSON = new JSONObject();

        EditText editText;
        ListView listView;
        CustomAdapter customAdapter;

        SendMessage(EditText editText,ListView listView,CustomAdapter customAdapter){
            this.editText = editText;
            this.listView = listView;
            this.customAdapter = customAdapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            messageBundle.putString("name",getName(context));
//            messageBundle.putString("category",category);
//            messageBundle.putString("message",message.getText().toString());
            time = Calendar.getInstance().get(Calendar.HOUR)+ ":" + ((Calendar.getInstance().get(Calendar.MINUTE) < 10)? "0" + Calendar.getInstance().get(Calendar.MINUTE):Calendar.getInstance().get(Calendar.MINUTE)) + " " +((Calendar.getInstance().get(Calendar.AM_PM) == 1)? "PM": "AM");
            try {
                messageJSON.put("name", sender);
                messageJSON.put("category",category);
                messageJSON.put("message",message);
                messageJSON.put("time",time);
            }catch (Exception e){
                e.printStackTrace();
            }


//            msg = message.getText().toString();
//            msg = category+":"+msg;

            mydb.open();
            mydb.createEntry(category,"text",message,"me",0, time);
            mydb.close();

//            TextView tv = new TextView(context);
//            tv.setText(msg);
//            tv.setGravity(Gravity.RIGHT);
//            messagesLayout.addView(tv);


            editText.setText("");
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp.setLayoutDirection();
//            messagesScrollView.post(new Runnable() {
//                @Override
//                public void run() {
//                    messagesScrollView.fullScroll(View.FOCUS_DOWN);
//                }
//            });
            customAdapter.notifyDataSetChanged();
            listView.setSelection(listView.getCount() - 1);

        }

        @Override
        protected String doInBackground(Void... voids) {


            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("regId", mySharedPreferences.getRegistrationId()));
            nameValuePair.add(new BasicNameValuePair("message", messageJSON.toString()));
            nameValuePair.add(new BasicNameValuePair("organisation",mySharedPreferences.getOrganisation()));

//            try {
//                Bundle data = new Bundle();
//                data.putString("my_message", "echo message");
//                data.putString("my_action",
//                        "com.google.android.gcm.demo.app.ECHO_NOW");
//                String id = Integer.toString(msgId.incrementAndGet());
//                gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
//                msg = "Sent message";
//            } catch (Exception ex) {
//                msg = "Error :" + ex.getMessage();
//            }
//            System.out.println(msg);
//            return msg;

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
            }catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("Message sent");
            return "Message sent";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            messagesScrollView.fullScroll(View.FOCUS_DOWN);
        }
    }
}
