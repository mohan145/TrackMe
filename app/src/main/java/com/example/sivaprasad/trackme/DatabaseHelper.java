package com.example.sivaprasad.trackme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by siva prasad on 23-03-2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Trackme.db";
    public static final String TABLE_NAME = "user_table";
    public static final String COL_1 = "Email";
    public static final String COL_2 = "Password";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
        SQLiteDatabase db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("create table "+TABLE_NAME+" (Email Text,Password Text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);

        onCreate(db);


    }
    public boolean clear(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete  from "+TABLE_NAME);

        return true;

    }
    public boolean insertData(String Email,String Password) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1,Email);
        cv.put(COL_2,Password);
        long result = db.insert(TABLE_NAME,null,cv);
        if (result==-1){
            return false;
        }
        else
            return true;
    }



    public Cursor getAllData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("SELECT  * FROM "+TABLE_NAME+";",null);
        return res;
    }

    public String searchPass(String mailid){

        SQLiteDatabase db = getReadableDatabase();
        String query ="select * from "+TABLE_NAME;
        Cursor csr =db.rawQuery(query,null);
        String a,b;
        b="not found";

        if (csr.moveToFirst()){
            do {
                a = csr.getString(0);

                if (a.equals(mailid)){
                    b = csr.getString(1);
                    break;
                }
            }while (csr.moveToNext());
        }
        return b;

    }


}
