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
public abstract class SQLiteDataProvider implements IDataProvider {

    SQLiteHelper helper;

    public SQLiteDataProvider(SQLiteHelper helper) {
        this.helper = helper;
    }

    
    private String normalizeAccountId(String type,String name){
        name = name.trim().toLowerCase().replace(' ', '-');
        return type+"-"+name;
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
    public Account findAccount(String type,String name) {
        String id = normalizeAccountId(type,name);
        return findAccount(id);
    }

    @Override
    public List<Account> listAccount(AccountType type) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        if(type==null){
            c = db.query(TB_ACC,COL_ACC_ALL,null,null, null, null, COL_ACC_ID);
        }else{
            c = db.query(TB_ACC,COL_ACC_ALL, COL_ACC_TYPE+" = ?", new String[]{type.getType()}, null, null, COL_ACC_ID);
        }
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
        String id = normalizeAccountId(account.getType(),account.getName());
        if (findAccount(id) != null) {
            throw new DuplicateKeyException("duplicate account id " + id);
        }
        account.setId(id);
        
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
        String newid = normalizeAccountId(account.getType(),account.getName());
        account.setId(newid);
        //TODO update all detail that has id to new id

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
