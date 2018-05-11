package com.amjoey.mokasqliteplc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.amjoey.mokasqliteplc.MainActivity.padding;
import static com.amjoey.mokasqliteplc.MainActivity.timeformat;
import static com.amjoey.mokasqliteplc.MainActivity.timetoint;

public class EditFriend extends Activity {

    private EditText etTime;
    private EditText etAmount;
    private Button etButtonOK,etButtonCancel;

    ImageButton imgTimeON;
    private int chour,cminute;

    static final int TIME_ON_ID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend);


        final Calendar calendar=Calendar.getInstance();
        chour=calendar.get(Calendar.HOUR_OF_DAY);
        cminute=calendar.get(Calendar.MINUTE);

        final int recID = getIntent().getIntExtra("recID", 0);
        Log.i(TAG, "recid " + recID);

        final DatabaseHandler mydb = new DatabaseHandler(this);
        Cursor cursor = mydb.getEditRecord(recID);


        etTime = (EditText) findViewById(R.id.time);
        etAmount = (EditText) findViewById(R.id.amount);
        etAmount.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "59")});

        etButtonOK = (Button)findViewById(R.id.button_submit);

        etButtonCancel = (Button)findViewById(R.id.button_cancel);

        imgTimeON = (ImageButton) findViewById(R.id.imgTimeON);

        imgTimeON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_ON_ID);
            }
        });

        //TextView tv = (TextView)findViewById(R.id.textView1);
        //EditText etName =(EditText) findViewById(R.id.edit_name);
        //EditText etAge =(EditText) findViewById(R.id.edit_age);


        if(cursor.getCount()==1){

            cursor.moveToFirst();
            etTime.setText(timeformat(Integer.parseInt(cursor.getString(cursor.getColumnIndex("time")))));
            etAmount.setText(cursor.getString(cursor.getColumnIndex("amount")));

            Log.i(TAG, "recid " + cursor.getString(cursor.getColumnIndex("time")));

        }
        etButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isUpdated = mydb.updateTime(recID,timetoint(etTime.getText().toString()),Integer.parseInt(etAmount.getText().toString()));
                if(isUpdated == true) {
                    Toast.makeText(EditFriend.this, "Data Updated", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                    Toast.makeText(EditFriend.this,"Data not Updated",Toast.LENGTH_LONG).show();
            }
        });

        etButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private TimePickerDialog.OnTimeSetListener mStartTime=new TimePickerDialog.OnTimeSetListener()
    {
        public void onTimeSet(TimePicker view, int hourofday, int min)
        {
            etTime.setText(new StringBuilder().append(padding(hourofday))
                    .append(":").append(padding(min)));
        }
    };

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case TIME_ON_ID:
                return new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,mStartTime,chour,cminute,false);
        }
        return null;
    }

}
