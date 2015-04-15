package com.vineet.infoshare;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {

    ActionBar actionBar;

    MySharedPreferences mySharedPreferences;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ORGANISATION = "organisation" ;
    private static final String PROPERTY_NAME = "name" ;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String SHARED_PREFERENCES_FILE = "bulletin";

    private final String TAG = "Splash.java";
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        actionBar = getActionBar();

        actionBar.hide();

        mySharedPreferences = new MySharedPreferences(this);

        System.out.println("Splash.java: registration id is" + mySharedPreferences.getRegistrationId());

        if (mySharedPreferences.getRegistrationId().equals("")){
            Thread timer = new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        Intent intent = new Intent(getApplicationContext(),Register.class);
                        startActivity(intent);
                    }
                }
            };
            timer.start();
        }else{
            Intent intent = new Intent(getApplicationContext(),Categories.class);
            startActivity(intent);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


}
