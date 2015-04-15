package com.vineet.infoshare;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by vineet on 07-Sep-14.
 */
public class CustomAdapter extends BaseAdapter {

    Activity activity;
    DatabaseManager mydb;
    private LayoutInflater inflater;
    RelativeLayout wrapper;

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_TYPE = "type";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_TIME = "time";
    public static final String KEY_STATUS = "status";
    private String category;

    Cursor res;


    public CustomAdapter(Activity activity,String category){
        this.activity = activity;
        mydb = new DatabaseManager(activity);
        this.category = category;
        System.out.println("CustomAdapter :" + category);
    }
    @Override
    public int getCount() {
        int count = 0;
        mydb.open();
        Cursor res = mydb.getAllMessages(category);
        res.moveToFirst();
        while (res.isAfterLast() == false){
            count++;
            res.moveToNext();
        }
        mydb.close();
        System.out.println("Count :" + count);
        return count;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        System.out.println("In Custom Adapter View Class");

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.message_layout,null);

        wrapper = (RelativeLayout)view.findViewById(R.id.lWrapperLayout);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView name = (TextView) view.findViewById(R.id.tvName);
        TextView message = (TextView) view.findViewById(R.id.tvMessage);
        TextView time = (TextView) view.findViewById(R.id.tvTime);


        // getting movie data for the row
        System.out.println("CustomAdapter :" + category);
        mydb.open();
        res = mydb.getAllMessages(category);
//        res.moveToFirst();
        res.moveToPosition(i);
//        while (res.isAfterLast() == false){
            System.out.println( "CustomAdapter " + res.getString(res.getColumnIndex(KEY_SENDER)));
            if (res.getString(res.getColumnIndex(KEY_SENDER)).equals("me")){
                System.out.println(res.getString(res.getColumnIndex(KEY_SENDER)) + " " + res.getString(res.getColumnIndex(KEY_SENDER)).equals("me"));
                name.setVisibility(View.GONE);
                name.setText(res.getString(res.getColumnIndex(KEY_SENDER)));
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lp.setMargins(50, 0, 0, 0);
                time.setGravity(Gravity.RELATIVE_LAYOUT_DIRECTION);
                wrapper.setLayoutParams(lp);
                wrapper.setBackgroundResource(R.drawable.chat_bubble_me);
//                name.setLayoutParams(lp);
//                view.setLayoutParams(lp);
            }else{
                name.setVisibility(View.VISIBLE);
                name.setText(res.getString(res.getColumnIndex(KEY_SENDER)).toUpperCase());
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.setMargins(0, 0, 50, 0);
                wrapper.setLayoutParams(lp);
                wrapper.setBackgroundResource(R.drawable.chat_bubble_other);
                time.setGravity(Gravity.START);
//                name.setLayoutParams(lp);
//                view.setLayoutParams(lp);
            }
            message.setText(res.getString(res.getColumnIndex(KEY_MESSAGE)) + "  ");
            time.setText(res.getString(res.getColumnIndex(KEY_TIME)));
//            res.moveToNext();
//        }


        mydb.close();
        return view;
    }
}
