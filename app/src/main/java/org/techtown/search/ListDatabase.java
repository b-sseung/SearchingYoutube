package org.techtown.search;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import static java.sql.DriverManager.println;

public class ListDatabase {
    private static final String TAG = "ListDatabase";
    public static String DATABASE_NAME = "ListDatabase";

    private static ListDatabase database;
    public static String TABLE_LIST = "LIST";
    public static int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    private ListDatabase(Context context){
        this.context = context;
    }

    public static ListDatabase getInstance(Context context){
        if (database == null) {
            database = new ListDatabase(context);
        }
        return database;
    }

    public boolean open(){
        println("opening database [" + DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    public void close(){
        println("closing database [" + DATABASE_NAME + "].");
        db.close();

        database = null;
    }

    public Cursor rawQuery(String SQL){
        println("\nexecuteQuery called.\n");
        Log.d("1919", SQL);

        if (db == null){
            open();
        }

        Cursor cursor = null;
        try{
            cursor = db.rawQuery(SQL, null);
            println("cursor count : " + cursor.getCount());
        } catch (Exception ex){
            Log.e(TAG, "Exception in executeQuery", ex);
        }

        return cursor;
    }

    public boolean execSQL(String SQL){
        println("\nexecute called.\n");

        try{
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch (Exception ex){
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }
        return true;
    }

    private void println(String msg){
        Log.d(TAG, msg);
    }

    private class DatabaseHelper extends SQLiteOpenHelper{


        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + DATABASE_NAME + "].");
            println("creating table [" + TABLE_LIST + "].");

            String DROP_SQL = "drop table if exists " + TABLE_LIST;
            try{
                db.execSQL(DROP_SQL);
            } catch(Exception ex){
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }

            String CREATE_SQL = "create table " + TABLE_LIST + "("
                    + " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + " SEARCH_Q TEXT DEFAULT '', "
                    + " SEARCH_ORDER TEXT DEFAULT '', "
                    + " SEARCH_MAX TEXT DEFAULT '', "
                    + " SEARCH_Q_MINUS TEXT DEFAULT '', "
                    + " SEARCH_START TEXT DEFAULT '', "
                    + " SEARCH_END TEXT DEFAULT '', "
                    + " SEARCH_MAIN TEXT DEFAULT '' " + ")";
            try{
                db.execSQL(CREATE_SQL);
            } catch (Exception ex){
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            String CREATE_INDEX_SQL = "create index " + TABLE_LIST + "_IDX ON " + TABLE_LIST + "(" + "CRATE_DATE" + ")";
            try{
                db.execSQL(CREATE_INDEX_SQL);
            } catch (Exception ex){
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }
        }

        public void onOpen(SQLiteDatabase db){
            println("opened database [" + DATABASE_NAME + "]");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }

        private void println(String msg){
            Log.d(TAG, msg);
        }
    }


}
