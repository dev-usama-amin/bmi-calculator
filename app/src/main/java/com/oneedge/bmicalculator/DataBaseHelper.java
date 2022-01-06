package com.oneedge.bmicalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;



public class DataBaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "bmi_calculator";

    // Table Names
    private static final String TABLE_DATA = "data";

    // Common column names
    public static final String KEY_ID = "id";
    public static final String BMI = "bmi";
    public static final String BMR = "bmr";
    public static final String DATE = "date";;


    //  table create statement
    private static final String CREATE_TABLE_DATA = "CREATE TABLE " + TABLE_DATA
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ BMI + " TEXT,"+ BMR + " TEXT," + DATE + " TEXT" + ")";

    private Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        SQLiteDatabase database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        // create new tables
        onCreate(db);
    }

    public void addToDB(HistoryModel model){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BMI,model.getBmi());
        contentValues.put(BMR,model.getBmr());
        contentValues.put(DATE,model.getDate());
        try {
            database.insertOrThrow(TABLE_DATA,null,contentValues);
        }catch (Exception e){
            Log.d("Databasehelper", "addToRecords: "+e.getMessage());
        }
    }

    public Cursor getData() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+TABLE_DATA+" ORDER BY "+KEY_ID+" DESC",null);
        return cursor;
    }

    public void deleteData(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_DATA,KEY_ID+"=?",new String[]{id});
    }

    public void emptyData(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ TABLE_DATA);
        database.execSQL("vacuum");
    }

    public Cursor getDataInPeriod(String startDate, String endDate){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor mCursor = database.rawQuery("SELECT * FROM "+ TABLE_DATA +
                " WHERE "+ DATE +
                " BETWEEN ?  AND ? ORDER BY "+KEY_ID+" ASC", new String[]{startDate, endDate});
        return mCursor;
    }

    private void emptyAll(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ TABLE_DATA);
        database.execSQL("vacuum");
    }

    public Cursor getDataByOrder() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = null;
        cursor = database.rawQuery("SELECT * FROM "+TABLE_DATA + " ORDER BY "+DATE+" ASC",null);
        return cursor;
    }


}
