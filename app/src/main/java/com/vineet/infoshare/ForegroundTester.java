package com.vineet.infoshare;

/**
 * Created by vineet on 07-Sep-14.
 */
public class ForegroundTester {

    private static boolean Foreground;
    private static String category;

    public static boolean isInForeground(){
        return Foreground;
    }

    public static void setForegroundFalse(){
        Foreground = false;
    }

    public static void setForegroundTrue(String categor){
        Foreground = true;
        category = categor;
    }

    public static String getCategory(){
        return category;
    }
}
