package com.bottleworks.dailymoney.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static com.bottleworks.dailymoney.data.SQLiteMeta.*;

/**
 * 
 * @author dennis
 * 
 */
public class SQLiteDataProvider implements IDataProvider {

    SQLiteHelper helper;

    public SQLiteDataProvider(SQLiteHelper helper) {
        this.helper = helper;
    }

    
    private String normalizeName(String name){
        name = name.trim().toLowerCase().replace(' ', '-');
        return name;
    }
    
    @Override
    public void reset() {
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onUpgrade(db, 0, db.getVersion());
    }

    @Override
    public Account findAccount(String id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TB_ACC,COL_ACC_ALL, COL_ACC_ID+" = ?", new String[]{id}, null, null, null, "1");
        Account acc = null;
        if(c.moveToNext()){
            acc = new Account();
            applyAccount(acc,c);
        }
        return acc;
    }

    private void applyAccount(Account acc, Cursor c) {
        int i = 0;
        for(String n:c.getColumnNames()){
            if(n.equals(COL_ACC_ID)){
                acc.setId(c.getString(i));
            }else if(n.equals(COL_ACC_NAME)){
                acc.setName(c.getString(i));
            }else if(n.equals(COL_ACC_TYPE)){
                acc.setType(c.getString(i));
            }else if(n.equals(COL_ACC_INITVAL)){
                acc.setInitialValue(c.getDouble(i));
            }
            i++;
        }
    }
    
    private void applyContextValue(Account acc,ContentValues values){
        values.put(COL_ACC_ID, acc.getId());
        values.put(COL_ACC_NAME, acc.getName());
        values.put(COL_ACC_TYPE, acc.getType());
        values.put(COL_ACC_INITVAL, acc.getInitialValue());
    }

    @Override
    public Account findAccountByNormalizedName(String name) {
        name = normalizeName(name);
        return findAccount(name);
    }

    @Override
    public List<Account> listAccount(AccountType type) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TB_ACC,COL_ACC_ALL, COL_ACC_TYPE+" = ?", new String[]{type.getType()}, null, null, COL_ACC_ID);
        List<Account> result = new ArrayList<Account>();
        Account acc;
        while(c.moveToNext()){
            acc = new Account();
            applyAccount(acc,c);
            result.add(acc);
        }
        return result;
    }

    @Override
    public void newAccount(Account account) throws DuplicateKeyException {
        String id = normalizeName(account.getName());
        if (findAccount(id) != null) {
            throw new DuplicateKeyException("duplicate account id " + id);
        }
        account.setId(normalizeName(id));
        
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        applyContextValue(account,cv);
        db.insertOrThrow(TB_ACC, null, cv);
    }

    @Override
    public boolean updateAccount(String id, Account account) {
        Account acc = findAccount(id);
        if (acc == null) {
            return false;
        }
        
        //reset id, id is following the name;
        account.setId(normalizeName(account.getName()));

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        applyContextValue(account,cv);
        
        //use old id to update
        int r = db.update(TB_ACC, cv, COL_ACC_ID+" = ?", new String[]{id});
        return r>0;
    }

    @Override
    public boolean deleteAccount(String id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int r = db.delete(TB_ACC, COL_ACC_ID+" = ?", new String[]{id});
        return r>0;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroyed() {
        helper.close();
    }

}
