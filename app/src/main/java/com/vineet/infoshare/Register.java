package com.vineet.infoshare;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Register extends Activity implements View.OnClickListener {

    EditText etName,etPhone,etEmail,etMessage,etOrganisation;
    String name,phone,email,message;
    Button register,goToShowMessage;

    ProgressDialog progressDialog;
    ActionBar actionBar;


    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ORGANISATION = "organisation" ;
    private static final String PROPERTY_NAME = "name" ;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String SHARED_PREFERENCES_FILE = "bulletin";


    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "216722683554";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;

    MySharedPreferences mySharedPreferences;
    NetManager netManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;

        mySharedPreferences = new MySharedPreferences(this);
        netManager = new NetManager(this);

        actionBar = getActionBar();
        actionBar.setTitle("Register");

        progressDialog = new ProgressDialog(context);

        etName = (EditText)findViewById(R.id.etName);
        etPhone = (EditText)findViewById(R.id.etPhoneNumber);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etMessage = (EditText)findViewById(R.id.etSendMessage);
        etOrganisation = (EditText)findViewById(R.id.etOrganisation);

        register = (Button)findViewById(R.id.bRegister);
//        goToShowMessage = (Button)findViewById(R.id.bSendMessage);

//        register.setEnabled(false);
        register.setOnClickListener(this);
//        goToShowMessage.setOnClickListener(this);
//        ((Button)findViewById(R.id.bGoToList)).setOnClickListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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
        int id = view.getId();

        switch (id){
            case R.id.bRegister:
                if (netManager.isNetworkAvailable()){
                    registerDevice();
                }else{
                    Toast.makeText(this,"Network not enabled, check your internet settings.",Toast.LENGTH_LONG).show();
                }
                break;

//            case R.id.bGoToShowMessage:
//                new SendMessage().execute();
//
//                break;

//            case R.id.bGoToList:
//                Intent intent = new Intent(getApplicationContext(),Categories.class);
//                startActivity(intent);
//                break;

            default:
                Log.e("Register.java","Invalid id passed to Register.java OnClickListener");
        }

    }

    private void registerDevice(){

        name = etEmail.getText().toString();
        phone = etPhone.getText().toString();
//        email = etEmail.getText().toString();
        email = getEmailId();
        if (name.isEmpty()){
            Toast.makeText(context,"Name feild is empty",Toast.LENGTH_LONG).show();
            return;
        }
        if (phone.isEmpty()){
            Toast.makeText(context,"Phone number is not valid",Toast.LENGTH_LONG).show();
            return;
        }
        if (email.isEmpty()){
            Toast.makeText(context,"Email account not set up on device",Toast.LENGTH_LONG).show();
            return;
        }

        if (etEmail.getText().toString().isEmpty()){
            Toast.makeText(context,"Email field is empty",Toast.LENGTH_LONG).show();
            return;
        }

        gcm = GoogleCloudMessaging.getInstance(this);
        regid = mySharedPreferences.getRegistrationId();

        if (regid.isEmpty()) {
//            registerInBackground();
//            new RegisterInBackground().execute();
            if (netManager.isNetworkAvailable()) {
                new OrganisationVerifier().execute();
            }else{
                Toast.makeText(context,"Internet connection not availaible",Toast.LENGTH_LONG).show();
            }
        }

    }




    private class RegisterInBackground extends AsyncTask<Void,Void,String>{

        String msg = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Registering device to server");

        }


        @Override
        protected String doInBackground(Void... voids) {

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                storeRegistrationId(getApplicationContext(),regid);
                msg = "Device registered, registration ID=" + regid;
                System.out.println(msg);

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.


                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the regID - no need to register again.
//                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            System.out.println("GCM register in background " + msg);
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //sendRegistrationIdToBackend();
            if (msg.startsWith("Error")){
                Toast.makeText(context,"Couldn't register to GCM Server. Please try again later",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }else{
                new SendRegistrationIdToBackend().execute();
            }

        }
    }


    private class SendRegistrationIdToBackend extends AsyncTask<Void,Void,Void>{

        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost("http://bulletin.comeze.com/register.php");
        @Override
        protected Void doInBackground(Void... voids) {

            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("name", etName.getText().toString()));
            nameValuePair.add(new BasicNameValuePair("email", getEmailId()));
            nameValuePair.add(new BasicNameValuePair("organisation",etOrganisation.getText().toString()));
            nameValuePair.add(new BasicNameValuePair("regId", regid));
            // add UUID to namevalue pair


            //Encoding POST data
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
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("SendRegistrationIdToBackend: Done");
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(),Categories.class);
            startActivity(intent);
        }
    }


    private class OrganisationVerifier extends AsyncTask<Void, Void, Boolean> {

        String organaisation;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            organaisation = etOrganisation.getText().toString();
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("http://bulletin.comeze.com/organisationVerify.php");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("organisation",organaisation));
            // Encrypt organisation to SHA ecryption


            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                e.printStackTrace();
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);
                String responsResult = EntityUtils.toString(response.getEntity());
                System.out.println("Organsiation verify result :" + responsResult);
                if (responsResult.equals("success")){
                    return true;
                }else{
                    return false;
                }
                // write response to log
//                Log.d("Http Post Response:", EntityUtils.toString(response.getEntity()));
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }

            return null ;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result == true){
                new RegisterInBackground().execute();
            }else if (false) {

            }else{
                progressDialog.dismiss();
                Toast.makeText(context,"Organisation key is not valid",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = mySharedPreferences.getGCMPreferences();
        int appVersion = mySharedPreferences.getAppVersion();
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.putString(PROPERTY_ORGANISATION, etOrganisation.getText().toString());
        editor.putString(PROPERTY_NAME,etName.getText().toString());
        editor.commit();
    }




    private String getEmailId() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                return possibleEmail;
            }
        }
        return null;
    }

}
