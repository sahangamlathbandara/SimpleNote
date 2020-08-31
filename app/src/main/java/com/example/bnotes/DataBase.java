package com.example.bnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DataBase extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Notes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_notes";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "input_title";
    private static final String COLUMN_LINK = "input_link";
    private static final String COLUMN_SUBJECT = "input_subject";


    DataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_LINK + " TEXT, " +
                COLUMN_SUBJECT + " TEXT);";
        db.execSQL(query);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    void addBook(String title, String link, String subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_LINK, link);
        cv.put(COLUMN_SUBJECT, subject);
        long result = db.insert(TABLE_NAME, null, cv);


        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();

        }

        db.close();

    }


    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }



        return cursor;


    }

    void updateData(String row_id, String title, String link, String subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_LINK, link);
        cv.put(COLUMN_SUBJECT, subject);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Updated", Toast.LENGTH_SHORT).show();

        }

        db.close();

    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        //db.execSQL("delete from "+ TABLE_NAME +" where _id = '" + row_id + "'");
        //db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE NAME = '"+TABLE_NAME+"'");

        if(result == -1){
            Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
        }

        db.close();

    }

    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        //db.delete(TABLE_NAME, null, null);
        //db.execSQL("UPDATE COLUMN_ID SET SEQ=0 WHERE _id=" + TABLE_NAME);


        db.close();


    }

    Cursor getOneRow(String rowId){
        //String query = "SELECT * FROM " + TABLE_NAME + " where ";
        SQLiteDatabase db = this.getReadableDatabase();
        //String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE _id="'id'";

        Log.d("Row id for share", rowId);

        Cursor c1 = null;
            if (db != null) {
                c1 = db.rawQuery("SELECT  * FROM " + TABLE_NAME + " WHERE _id ="+rowId+";", null);
            }


            return c1;

    }

    Cursor testGetData() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY _id DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }


        return cursor;


    }
}





