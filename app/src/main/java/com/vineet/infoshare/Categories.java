package com.vineet.infoshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Categories extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{

    String[] listItems = {"Notices","Events","Lost","Found"};
    ArrayAdapter<String> arrayAdapter;

    ListView listView;

    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = (ListView)findViewById(R.id.lvList);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItems);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(this);

        ((Button)findViewById(R.id.button)).setOnClickListener(this);
        ((Button)findViewById(R.id.button2)).setOnClickListener(this);

        flag = 0;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//        Intent intent = new Intent(getApplicationContext(),ShowMessagesTemp.class);
        try {
            Intent intent;
            if (flag == 0) {
                intent = new Intent(getApplicationContext(), ShowMessages.class);
            }else{
                intent = new Intent(getApplicationContext(), ShowMessages.class);
            }
            intent.putExtra("Key", adapterView.getItemAtPosition(i).toString());
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
//                className = "ShowMessages.class";
//                className.replace(className,"ShowMessages.class");
                flag = 0;
                break;

            case R.id.button2:
//                className.replace(className,"ShowMessagesTemp.class");
//                className = "ShowMessagesTemp.class";
                flag = 1;
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForegroundTester.setForegroundFalse();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ForegroundTester.setForegroundTrue("categories");
    }
}
