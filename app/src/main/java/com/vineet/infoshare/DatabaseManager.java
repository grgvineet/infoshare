package com.vineet.infoshare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vineet on 06-Jul-14.
 */
public class DatabaseManager {

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_TYPE = "type";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_TIME = "time";
    public static final String KEY_STATUS = "status" ;

    private static final String DATABASE_CATEGORY = "Bulletin";
    private static final String DATABASE_TABLE = "Bulletin";
    private static final int DATABASE_VESRION = 1;

    private final Context ourContext;
    private SQLiteDatabase ourDatabase;
    private DbHelper ourHelper;

    public DatabaseManager(Context c) {
        ourContext = c;
    }




    private static class DbHelper extends SQLiteOpenHelper{

        public DbHelper(Context context) {
            super(context, DATABASE_CATEGORY, null, DATABASE_VESRION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + "(" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_CATEGORY + " TEXT NOT NULL, " + KEY_TYPE + " TEXT NOT NULL, " + KEY_MESSAGE +
                    " TEXT NOT NULL, " + KEY_SENDER + " TEXT NOT NULL, " + KEY_STATUS + " INT NOT NULL, " + KEY_TIME + " TEXT NOT NULL); ");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE);
            onCreate(db);

        }

    }

    public DatabaseManager open(){
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void dropTable(){
        ourHelper.onUpgrade(ourDatabase,0,0);
    }

    public void close(){
        ourHelper.close();
    }

    public Cursor getAllMessages(String category)
    {
//        ArrayList array_list = new ArrayList();
        //hp = new HashMap();
        String[] coloumns = new String []{KEY_ROW_ID,KEY_CATEGORY,KEY_TYPE,KEY_MESSAGE,KEY_SENDER,KEY_STATUS,KEY_TIME} ;
        Cursor res =  ourDatabase.query(DATABASE_TABLE,coloumns,KEY_CATEGORY + " =? ",new String[]{category},null,null,null,null);
//        res.moveToFirst();
//        while(res.isAfterLast() == false){
//            array_list.add(res.getString(res.getColumnIndex(KEY_MESSAGE)));
//            res.moveToNext();
//        }
        return res;


    }

    public String[] getData(String id){
        String[] coloumns = new String []{KEY_ROW_ID,KEY_CATEGORY,KEY_TYPE,KEY_MESSAGE,KEY_SENDER,KEY_STATUS,KEY_TIME} ;
        String[] result = new String[5];
        Cursor res =  ourDatabase.query(DATABASE_TABLE,coloumns,KEY_CATEGORY + " =? ",new String[]{id},null,null,null);
        if (res != null){
            res.moveToFirst();
            for (int i=0 ; i<5 ; i++){
                result[i] = res.getString(i);
            }
//            String type = res.getString(2);
            return result;
        }
        return null;
    }

    public long createEntry(String categor, String typ,String messag,String sende,int status,String time) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CATEGORY,categor);
        cv.put(KEY_TYPE,typ);
        cv.put(KEY_MESSAGE,messag);
        cv.put(KEY_SENDER,sende);
        cv.put(KEY_STATUS,status);
        cv.put(KEY_TIME,time);

        return ourDatabase.insert(DATABASE_TABLE,null,cv);
    }

    public Cursor getNotSentMessages(){
        String[] coloumns = new String []{KEY_ROW_ID,KEY_CATEGORY,KEY_TYPE,KEY_MESSAGE,KEY_SENDER,KEY_STATUS,KEY_TIME} ;
        String[] result = new String[5];
//        Cursor res =  ourDatabase.query(DATABASE_TABLE,coloumns,KEY_SENDER + ',' + KEY_STATUS + " =? ",new String[]{"me",1},null,null,null);
        String query = "SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_SENDER + "='me' AND " + KEY_STATUS + "=1" ;
        Cursor res = ourDatabase.rawQuery(query,null);

//        if (res != null){
//            res.moveToFirst();
//            for (int i=0 ; i<5 ; i++){
//                result[i] = res.getString(i);
//            }
////            String type = res.getString(2);
//            return res;
//        }
//        return null;
        return  res;
    }

    public long updateEntry(String value, String newName, String newPhone ,String newExtNo, String newStaffNo) {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(KEY_CATEGORY,newName);
        cvUpdate.put(KEY_TYPE,newPhone);
        cvUpdate.put(KEY_MESSAGE,newExtNo);
        cvUpdate.put(KEY_SENDER,newStaffNo);
        return ourDatabase.update(DATABASE_TABLE,cvUpdate,KEY_CATEGORY + "=?",new String[]{value});
    }

    public boolean checkentry(String categor) {
        String[] coloumns = new String []{KEY_ROW_ID,KEY_CATEGORY,KEY_TYPE,KEY_MESSAGE,KEY_SENDER} ;
        Cursor res =  ourDatabase.query(DATABASE_TABLE,coloumns,KEY_CATEGORY + " =? ",new String[]{categor},null,null,null);
        if (res.getCount() != 0){
            return false;
        }else {
            return true;
        }

    }

    public void delete(String category){
        ourDatabase.delete(DATABASE_TABLE,KEY_CATEGORY + " =? ",new String[]{category} );
    }

    public ArrayList<HashMap<String, String>> getAllUsers() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;
        Cursor cursor = ourDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put("category", cursor.getString(1));
                map.put("type", cursor.getString(2));
                map.put("message", cursor.getString(3));
                map.put("sender", cursor.getString(4));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        return wordList;
    }

//    public String composeJSONfromSQLite(){
//        ArrayList<HashMap<String, String>> wordList;
//        wordList = new ArrayList<HashMap<String, String>>();
//        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;
//        Cursor cursor = ourDatabase.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                HashMap<String, String> map = new HashMap<String, String>();
////                map.put("id", cursor.getString(0));
//                map.put("category", cursor.getString(1));
//                map.put("type", cursor.getString(2));
//                map.put("message", cursor.getString(3));
//                map.put("sender", cursor.getString(4));
//                wordList.add(map);
//            } while (cursor.moveToNext());
//        }
//
//        Gson gson = new GsonBuilder().create();
//        //Use GSON to serialize Array List to JSON
//        return gson.toJson(wordList);
//    }

//    public String getJasonFromSqlite() {
//        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;
//        Cursor cursor = ourDatabase.rawQuery(selectQuery, null);
//
//        JSONArray valuesarray = new JSONArray();
//        JSONObject valueJson = new JSONObject();
//
//        if (cursor.moveToFirst()) {
//            do {
//
//            }while (cursor.moveToNext());
//        }
//
//    }


}
