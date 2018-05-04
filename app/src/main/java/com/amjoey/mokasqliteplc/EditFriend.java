package com.amjoey.mokasqliteplc;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class EditFriend extends Activity {
    private EditText etTime;
    private EditText etAmount;
    private Button etButonOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend);

        final int recID = getIntent().getIntExtra("recID", 0);
        Log.i(TAG, "recid " + recID);

        final DatabaseHandler mydb = new DatabaseHandler(this);
        Cursor cursor = mydb.getEditRecord(recID);


        etTime = (EditText) findViewById(R.id.time);
        etAmount = (EditText) findViewById(R.id.amount);
        etButonOK = (Button)findViewById(R.id.button_submit);
        //TextView tv = (TextView)findViewById(R.id.textView1);
        //EditText etName =(EditText) findViewById(R.id.edit_name);
        //EditText etAge =(EditText) findViewById(R.id.edit_age);


        if(cursor.getCount()==1){

            cursor.moveToFirst();
            etTime.setText(cursor.getString(cursor.getColumnIndex("time")));
            etAmount.setText(cursor.getString(cursor.getColumnIndex("amount")));

            Log.i(TAG, "recid " + cursor.getString(cursor.getColumnIndex("time")));

        }
        etButonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isUpdated = mydb.updateTime(recID,Integer.parseInt(etTime.getText().toString()),Integer.parseInt(etAmount.getText().toString()));
                if(isUpdated == true) {
                    Toast.makeText(EditFriend.this, "Data Updated", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                    Toast.makeText(EditFriend.this,"Data not Updated",Toast.LENGTH_LONG).show();
            }
        });
    }

}
