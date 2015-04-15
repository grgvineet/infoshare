package com.vineet.infoshare;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by vineet on 13-Sep-14.
 */
public class MySharedPreferences {

    private Context context;

    private static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ORGANISATION = "organisation" ;
    private static final String PROPERTY_NAME = "name" ;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String SHARED_PREFERENCES_FILE = "bulletin";

    public MySharedPreferences(Context context) {
        this.context = context;
    }


    public String getRegistrationId() {
        final android.content.SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("getRegistrationId", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i("getRegistrationId", "App version changed.");
            return "";
        }
        System.out.println("getRegistrationId :" + registrationId);
        return registrationId;
    }

    public String getOrganisation() {
        final android.content.SharedPreferences prefs = getGCMPreferences();
        String organisation = prefs.getString(PROPERTY_ORGANISATION, "");
        if (organisation.isEmpty()) {
            Log.i("getRegistrationId", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i("getRegistrationId", "App version changed.");
            return "";
        }
        System.out.println("getRegistrationId :" + organisation);
        return organisation;
    }

    public String getName() {
        final android.content.SharedPreferences prefs = getGCMPreferences();
        String name = prefs.getString(PROPERTY_NAME, "");
        if (name.isEmpty()) {
            Log.i("getRegistrationId", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i("getRegistrationId", "App version changed.");
            return "";
        }
        System.out.println("getRegistrationId :" + name);
        return name;
    }

    public android.content.SharedPreferences getGCMPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE,
                Context.MODE_PRIVATE);
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


}
