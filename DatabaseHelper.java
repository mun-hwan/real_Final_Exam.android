package com.example.user.final_exam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABAES_NAME = "Location.db";
    public static final String TABLE_NAME = "Location_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "LAT";
    public static final String COL_3 = "LAN";
    public static final String COL_4 = "DATE";
    public static final int DTATBASE_VERSION=4;

    public DatabaseHelper(Context context) {
        super(context, DATABAES_NAME, null, DTATBASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,LAT DOUBLE,LAN DOUBLE,DATE STRING)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(double lat,double lan,String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put(COL_2,lat);
        contentvalues.put(COL_3,lan);
        contentvalues.put(COL_4,date);
        long result = db.insert(TABLE_NAME,null,contentvalues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getAllDate(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME,null);
        return res;
    }
}
