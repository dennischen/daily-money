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
    
    private static final int VERSION = 1;
    private static final String ACCOUNT_CREATE_SQL = "CREATE TABLE " + TB_ACC + " (" + COL_ACC_ID
            + " TEXT PRIMARY KEY, "+COL_ACC_NAME+" TEXT NOT NULL, "+COL_ACC_TYPE+" TEXT NOT NULL, "+COL_ACC_INITVAL+" REAL)";
    private static final String ACCOUNT_DROP_SQL = "DROP TABLE IF EXISTS "+TB_ACC;
    
    
    
    
    public SQLiteHelper(Context context,String dbname) {
        super(context, dbname, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d("create schema " +ACCOUNT_CREATE_SQL);
        db.execSQL(ACCOUNT_CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.d("update db from "+oldVersion+" to "+newVersion);
        
        Logger.d("drop schema "+ACCOUNT_DROP_SQL);
        db.execSQL(ACCOUNT_DROP_SQL);
        
        Logger.d("create schema " +ACCOUNT_CREATE_SQL);
        db.execSQL(ACCOUNT_CREATE_SQL);
    }

}
