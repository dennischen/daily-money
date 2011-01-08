package com.bottleworks.dailymoney.data;

import com.bottleworks.commons.util.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.bottleworks.dailymoney.data.SQLiteMeta.*;

/**
 * 
 * @author dennis
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper{
    
    private static final int VERSION = 2;
    private static final String ACC_CREATE_SQL = "CREATE TABLE " + TB_ACC + " (" 
            + COL_ACC_ID + " TEXT PRIMARY KEY, "
            + COL_ACC_NAME +" TEXT NOT NULL, "
            + COL_ACC_TYPE+" TEXT NOT NULL, "
            + COL_ACC_INITVAL+" REAL NOT NULL)";
    private static final String ACC_DROP_SQL = "DROP TABLE IF EXISTS "+TB_ACC;
    
    
    private static final String DET_CREATE_SQL = "CREATE TABLE " + TB_DET + " (" 
    + COL_DET_ID + " INTEGER PRIMARY KEY, "
    + COL_DET_FROM +" TEXT NOT NULL, "
    + COL_DET_FROM_DISPLAY +" TEXT NOT NULL, "
    + COL_DET_TO +" TEXT NOT NULL, "
    + COL_DET_TO_DISPLAY +" TEXT NOT NULL, "
    + COL_DET_DATE+" INTEGER NOT NULL, "
    + COL_DET_MONEY+" REAL NOT NULL, "
    + COL_DET_ARCHIVED+" INTEGER NOT NULL, "
    + COL_DET_NOTE+" TEXT)";
    
    private static final String DET_DROP_SQL = "DROP TABLE IF EXISTS "+TB_DET;
    
    
    public SQLiteHelper(Context context,String dbname) {
        super(context, dbname, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d("create schema " +ACC_CREATE_SQL);
        db.execSQL(ACC_DROP_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.d("update db from "+oldVersion+" to "+newVersion);
        
        Logger.d("drop schema "+ACC_DROP_SQL);
        db.execSQL(ACC_DROP_SQL);
        Logger.d("drop schema "+DET_DROP_SQL);
        db.execSQL(DET_DROP_SQL);
        
        Logger.d("create schema " +ACC_CREATE_SQL);
        db.execSQL(ACC_CREATE_SQL);
        Logger.d("create schema " +DET_CREATE_SQL);
        db.execSQL(DET_CREATE_SQL);
    }

}
