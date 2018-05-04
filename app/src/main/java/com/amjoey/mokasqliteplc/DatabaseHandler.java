package com.amjoey.mokasqliteplc;


/**
 * Created by Administrator on 7/4/2561.
 */
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myschedule.db";
    static final String TABLE_NAME = "mytime";

    private static final int DATABASE_VERSION = 2;
    static final String COLUMN_TIME = "time";
    static final String COLUMN_AMOUNT = "amount";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE_NAME
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TIME + " INTEGER, " + COLUMN_AMOUNT + " INTEGER);");
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TABLE_NAME, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean  addRecord(int time, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TIME, time);
        values.put(COLUMN_AMOUNT, amount);


        long row = db.insert(DatabaseHandler.TABLE_NAME, null, values);
        Log.d(TABLE_NAME,"inserted at row " + row + " " + time + amount);

        db.close();
        if(row == -1)
            return false;
        else
            return true;
    }

    public String getRecord(long id) {
        String data = null;

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { "_id", "time", "amount"};
        Cursor c = db.query(TABLE_NAME,
                columns,
                //null,
                "_id=?", new String[] { String.valueOf(id) },
                null, null, null, null);

        Log.d(TABLE_NAME,"recID "+ id + " count " + c.getCount() );

        if (c != null) {
            Log.d(TABLE_NAME,"recID "+ c);
            if (c.moveToFirst()) {
                int idCol= c.getColumnIndex("_id");
                int timeCol= c.getColumnIndex("time");
                int amountCol= c.getColumnIndex("amount");
                String strId = Integer.toString(c.getInt(idCol));
                String strTime = Integer.toString(c.getInt(timeCol));
                String strAmount = Integer.toString(c.getInt(amountCol));
                data = "id "+ strId + "\nTime "+ strTime + "\nAmount " + strAmount + "\n";
            }
        }
        c.close();
        return data;
    }


    public Cursor getAllRecord() {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { "_id", "time", "amount"};
        Cursor cur = db.query(TABLE_NAME,
                columns,
                null,
                null, null, null, null);

        Log.d(TABLE_NAME, " count " + cur.getCount() );
        return cur;
    }
    public Cursor getEditRecord(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { "_id", "time", "amount"};
        Cursor cur = db.query(TABLE_NAME,
                columns,
                //null,
                "_id=?", new String[] { String.valueOf(id) },
                null, null, null, null);

        Log.d(TABLE_NAME, " count " + cur.getCount() );

        return cur;
    }

    public Cursor getSearchedRecord(String search) {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { "_id", "time", "amount"};
        Cursor cur = db.query(TABLE_NAME,
                columns,
                "time LIKE ?", new String[]{"%" + search + "%"},
                null, null, null, null);

        Log.d(TABLE_NAME, " count " + cur.getCount() );

        return cur;
    }


    public int getRecordCount() {
        String countQuery = "SELECT _id FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(countQuery, null);
        return cur.getCount();
    }

    public boolean updateTime(long recID, int time, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_AMOUNT, amount);
        long row= db.update(TABLE_NAME, values, "_id = ?",
                new String[] { String.valueOf(recID) });
        db.close();
        if(row == -1)
            return false;
        else
            return true;
    }


    public boolean deleteRecord(long recID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long row = db.delete(TABLE_NAME, "_id = ?",
                new String[] { String.valueOf(recID) });
        db.close();


        if(row == -1)
            return false;
        else
            return true;
    }

}
