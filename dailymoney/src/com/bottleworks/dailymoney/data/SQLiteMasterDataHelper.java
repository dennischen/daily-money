package com.bottleworks.dailymoney.data;

import static com.bottleworks.dailymoney.data.MasterDataMeta.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.context.Contexts;

/**
 * 
 * @author dennis
 *
 */
public class SQLiteMasterDataHelper extends SQLiteOpenHelper{
    /** maintain this field carefully*/
    private static final int VERSION = 1;//0.9.6-
    
    private static final String BOOK_CREATE_SQL = "CREATE TABLE " + TB_BOOK + " (" 
            + COL_BOOK_ID + " TEXT PRIMARY KEY, "
            + COL_BOOK_NAME +" TEXT NOT NULL, "
            + COL_BOOK_SYMBOL+" TEXT NOT NULL, "
            + COL_BOOK_SYMBOL_INFRONT+" INTEGER NULL, "
            + COL_BOOK_NOTE+" TEXT)";
    private static final String BOOK_DROP_SQL = "DROP TABLE IF EXISTS "+TB_BOOK;
    
    
    public SQLiteMasterDataHelper(Context context,String dbname) {
        super(context, dbname, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(Contexts.DEBUG){
            Logger.d("create master schema " +BOOK_CREATE_SQL);
        }
        db.execSQL(BOOK_CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(Contexts.DEBUG){
            Logger.d("update master db from "+oldVersion+" to "+newVersion);
        }
        if(oldVersion<0){
            Logger.i("reset master schema");
            //drop and create.
            Logger.i("drop master schema "+BOOK_DROP_SQL);
            db.execSQL(BOOK_DROP_SQL);
            onCreate(db);
            return;
        }
    }

}
