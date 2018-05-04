package com.amjoey.mokasqliteplc;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import Moka7.*;

public class MainActivity extends ListActivity {
    private EditText searchText;

    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    DatabaseHandler mydb ;

    private void updateData()
    {
        mydb = new DatabaseHandler(this);

        cursor = mydb.getAllRecord();
        adapter = new MyCursorAdapter(
                this,
                R.layout.row_layout,
                cursor,
                new String[] {"_id", "time", "amount"},
                new int[] {R.id.id, R.id.time, R.id.amount},
                0);
        setListAdapter(adapter);

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (getListView() != null)
        {
            updateData();
        }

        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 500; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            new PlcReader().execute("");
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DatabaseHandler(this);

        cursor = mydb.getAllRecord();
        adapter = new MyCursorAdapter(
                this,
                R.layout.row_layout,
                cursor,
                new String[] {"_id", "time", "amount"},
                new int[] {R.id.id, R.id.time, R.id.amount},
                0);
        setListAdapter(adapter);

        searchText = (EditText) findViewById (R.id.searchText);

        //register ListView for context menu in ListActivity class
        getListView().setAdapter(adapter);
        registerForContextMenu(getListView());
    }

    public void onListItemClick(ListView parent, View view, int position, long id) {

        Intent intent = new Intent(this, EditFriend.class);
        Cursor cursor = (Cursor) adapter.getItem(position);
        intent.putExtra("recID", cursor.getInt(cursor.getColumnIndex("_id")));
        startActivity(intent);

    }

    private class MyCursorAdapter extends SimpleCursorAdapter{

        private Cursor c;
        private Context context;
        private Bundle bundle;

        public MyCursorAdapter(Context context, int layout, Cursor c,
                               String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.c = c;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*
            if(convertView == null){
                convertView = View.inflate(context, R.layout.row_layout, null);
            }
            View row = convertView;
            */


            //get reference to the row
            View view = super.getView(position, convertView, parent);

            this.c.moveToPosition(position);
            String time = Integer.toString(this.c.getInt(this.c.getColumnIndex("time")));

            TextView timeTextView = (TextView)view.findViewById(R.id.time);
            timeTextView.setText(timeformat(Integer.parseInt(time)));

            //check for odd or even to set alternate colors to the row background
            if(position % 2 == 0){
                view.setBackgroundColor(Color.rgb(238, 233, 233));
            }
            else {
                view.setBackgroundColor(Color.rgb(255, 255, 204));
            }
            return view;
        }

    }

    public static String timeformat(int t){
        String intTime;
        intTime =String.valueOf(Integer.toHexString(t));
        String first = padding(Integer.parseInt(intTime.substring(0, intTime.length() / 2)));
        String second = padding(Integer.parseInt(intTime.substring(intTime.length() / 2)));
        return first+ ":"  +second;
    }

    public static int timetoint(String s){
        int setTime;
        String[] separated = s.split(":");

        String first = separated[0];
        String second =separated[1];
        setTime =Integer.parseInt(first+second,16);
        return setTime;
    }

    public static String padding(int c){
        if(c>=10)
            return String.valueOf(c);
        else
            return "0"+ String.valueOf(c);
    }

    S7Client client = new S7Client();


    //begin class PlcReader
    private class PlcReader extends AsyncTask<String, Void, String> {

        String ret = "";

        @Override
        protected String doInBackground(String... params){

            try{
                client.SetConnectionType(S7.S7_BASIC);
                int res=client.ConnectTo("192.168.1.12",0,0);

                if(res==0){//connection OK

                    //byte[] data = new byte[12];
                    //res = client.ReadArea(S7.S7AreaDB,1,1,12,data);

                    byte[] data = new byte[4];
                    res = client.ReadArea(S7.S7AreaDB,1,988,3,data);
                    //  ret = "value of DB1.DBD25: "+ S7.GetFloatAt(data,0);
                    //  ret = "value of DB1.DBD10: "+ S7.GetWordAt(data,0);
                    //ret = "Value of DB1.DBD1: "+ S7.GetWordAt(data,0)+"/"+ S7.GetWordAt(data,2)+"/"+ S7.GetWordAt(data,4)+"/"+ S7.GetWordAt(data,6)+"/"+ S7.GetWordAt(data,8)+"/"+ S7.GetWordAt(data,10);

                    ret = padding(S7.GetWordAt(data,0)/256) +":"+padding(S7.GetWordAt(data,1)/256) +":"+padding(S7.GetWordAt(data,2)/256) ;

                    /*
                    byte[] dataWrite = new byte[2];
                   // S7.SetBitAt(dataWrite, 0, 1, true);
                   // S7.SetDIntAt(dataWrite,0,5);
                    S7.SetWordAt(dataWrite,0,700);

                    client.WriteArea(S7.S7AreaDB, 1, 12, 2, dataWrite);
                    ret = "WriteArea of DB1.DBD12: OK ";
                    */


                }else{
                    ret = "ERR: "+ S7Client.ErrorText(res);
                }
                client.Disconnect();
            }catch (Exception e) {
                ret = "EXC: "+e.toString();
                Thread.interrupted();
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result){
            TextView txout = (TextView) findViewById(R.id.searchText);
            txout.setText(ret);
        }
    }
    //end class PlcReader

    public void upload(View view){

        new PlcWriter().execute("");


    }

    //begin class PlcWriter
    private class PlcWriter extends AsyncTask<String, Void, String> {

        String ret= "";
        DatabaseHandler mydb = new DatabaseHandler(getApplicationContext());

        @Override
        protected String doInBackground(String... params){

            try{
                client.SetConnectionType(S7.S7_BASIC);
                int res=client.ConnectTo("192.168.1.12",0,0);

                if(res==0){//connection OK

                    cursor = mydb.getAllRecord();


                    byte[] dataWrite = new byte[96];

                    int cAmount = 0;
                    int cTime = 2;
                    while (cursor.moveToNext()) {

                        int intTime =   cursor.getInt(cursor.getColumnIndex("time"));
                        int intAmount =    (cursor.getInt(cursor.getColumnIndex("amount"))*100);
                        S7.SetWordAt(dataWrite,cAmount,intAmount);
                        S7.SetWordAt(dataWrite,cTime,intTime);
                        cAmount = cAmount+4;
                        cTime = cTime+4;
                    }
                    cursor.close();
                    client.WriteArea(S7.S7AreaDB, 1, 0, 96, dataWrite);

                    ret = "Upload Completed";


                }else{
                    ret = "ERR: "+ S7Client.ErrorText(res);
                }
                client.Disconnect();
            }catch (Exception e) {
                ret = "EXC: "+e.toString();
                Thread.interrupted();
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result){

            Context context = getApplicationContext();
            Toast.makeText(context, ret, Toast.LENGTH_LONG).show();

        }

    }
    //end class PlcWriter
}
