package com.bottleworks.dailymoney.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.context.Contexts;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static com.bottleworks.dailymoney.data.DataMeta.*;

/**
 * 
 * @author dennis
 * 
 */
public class SQLiteDataProvider implements IDataProvider {

    SQLiteDataHelper helper;
    CalendarHelper calHelper;

    public SQLiteDataProvider(SQLiteDataHelper helper,CalendarHelper calHelper) {
        this.helper = helper;
        this.calHelper = calHelper;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroyed() {
        helper.close();
    }

    private String normalizeAccountId(String type, String name) {
        name = name.trim().toLowerCase().replace(' ', '-');
        return type + "-" + name;
    }

    @Override
    public void reset() {
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onUpgrade(db, -1, db.getVersion());
        detId = 0;
        detId_set = false;
    }

    @Override
    public Account findAccount(String id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TB_ACC, COL_ACC_ALL, COL_ACC_ID + " = ?", new String[] { id }, null, null, null, "1");
        Account acc = null;
        if (c.moveToNext()) {
            acc = new Account();
            applyCursor(acc, c);
        }
        c.close();
        return acc;
    }

    private void applyCursor(Account acc, Cursor c) {
        int i = 0;
        for (String n : c.getColumnNames()) {
            if (n.equals(COL_ACC_ID)) {
                acc.setId(c.getString(i));
            } else if (n.equals(COL_ACC_NAME)) {
                acc.setName(c.getString(i));
            } else if (n.equals(COL_ACC_TYPE)) {
                acc.setType(c.getString(i));
            } else if (n.equals(COL_ACC_CASHACCOUNT)) {
                //nullable
                acc.setCashAccount(c.getInt(i) == 1);
            }else if (n.equals(COL_ACC_INITVAL)) {
                acc.setInitialValue(c.getDouble(i));
            }
            i++;
        }
    }

    private void applyContextValue(Account acc, ContentValues values) {
        values.put(COL_ACC_ID, acc.getId());
        values.put(COL_ACC_NAME, acc.getName());
        values.put(COL_ACC_TYPE, acc.getType());
        values.put(COL_ACC_CASHACCOUNT, acc.isCashAccount()?1:0);
        values.put(COL_ACC_INITVAL, acc.getInitialValue());
    }

    @Override
    public Account findAccount(String type, String name) {
        String id = normalizeAccountId(type, name);
        return findAccount(id);
    }

    @Override
    public List<Account> listAccount(AccountType type) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        if (type == null) {
            c = db.query(TB_ACC, COL_ACC_ALL, null, null, null, null, COL_ACC_ID);
        } else {
            c = db.query(TB_ACC, COL_ACC_ALL, COL_ACC_TYPE + " = ?", new String[] { type.getType() }, null, null,
                    COL_ACC_ID);
        }
        List<Account> result = new ArrayList<Account>();
        Account acc;
        while (c.moveToNext()) {
            acc = new Account();
            applyCursor(acc, c);
            result.add(acc);
        }
        c.close();
        return result;
    }

    @Override
    public void newAccount(Account account) throws DuplicateKeyException {
        String id = normalizeAccountId(account.getType(), account.getName());
        newAccount(id,account);
    }
    
    public  String toAccountId(Account account){
        String id = normalizeAccountId(account.getType(), account.getName());
        return id;
    }
    
    public synchronized void newAccount(String id, Account account) throws DuplicateKeyException {
        if (findAccount(id) != null) {
            throw new DuplicateKeyException("duplicate account id " + id);
        }
        newAccountNoCheck(id,account);
    }
    
    @Override
    public void newAccountNoCheck(String id,Account account){
        if(Contexts.DEBUG){
            Logger.d("new account "+id);
        }
        account.setId(id);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        applyContextValue(account, cv);
        db.insertOrThrow(TB_ACC, null, cv);
    }

    @Override
    public boolean updateAccount(String id, Account account) {
        Account acc = findAccount(id);
        if (acc == null) {
            return false;
        }

        // reset id, id is following the name;
        String newid = normalizeAccountId(account.getType(), account.getName());
        account.setId(newid);

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        applyContextValue(account, cv);

        // use old id to update
        int r = db.update(TB_ACC, cv, COL_ACC_ID + " = ?", new String[] { id });
        
        
        if(r > 0){
            //update the refereted detail id
            cv = new ContentValues();
            cv.put(COL_DET_FROM, newid);
            cv.put(COL_DET_FROM_TYPE,account.getType());
            db.update(TB_DET, cv, COL_DET_FROM + " = ?" ,new String[]{id});
            
            cv = new ContentValues();
            cv.put(COL_DET_TO, newid);
            cv.put(COL_DET_TO_TYPE,account.getType());
            db.update(TB_DET, cv, COL_DET_TO + " = ?" ,new String[]{id});
        }
        
        
        
        return r > 0;
    }

    @Override
    public boolean deleteAccount(String id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int r = db.delete(TB_ACC, COL_ACC_ID + " = ?", new String[] { id });
        return r > 0;
    }

    /**
     * detail impl.
     */

    private void applyCursor(Detail det, Cursor c) {
        int i = 0;
        for (String n : c.getColumnNames()) {
            if (n.equals(COL_DET_ID)) {
                det.setId(c.getInt(i));
            } else if (n.equals(COL_DET_FROM)) {
                det.setFrom(c.getString(i));
            } else if (n.equals(COL_DET_TO)) {
                det.setTo(c.getString(i));
            } else if (n.equals(COL_DET_DATE)) {
                det.setDate(new Date(c.getLong(i)));
            } else if (n.equals(COL_DET_MONEY)) {
                det.setMoney(c.getDouble(i));
            } else if (n.equals(COL_DET_ARCHIVED)) {
                det.setArchived((c.getInt(i) == 1));
            } else if (n.equals(COL_DET_NOTE)) {
                det.setNote(c.getString(i));
            }
            i++;
        }
    }

    private void applyContextValue(Detail det, ContentValues values) {
        values.put(COL_DET_ID, det.getId());
        values.put(COL_DET_FROM, det.getFrom());
        values.put(COL_DET_FROM_TYPE, det.getFromType());
        values.put(COL_DET_TO, det.getTo());
        values.put(COL_DET_TO_TYPE, det.getToType());
        values.put(COL_DET_DATE, calHelper.toDayMiddle(det.getDate()).getTime());
        values.put(COL_DET_MONEY, det.getMoney());
        values.put(COL_DET_ARCHIVED, det.isArchived() ? 1 : 0);
        values.put(COL_DET_NOTE, det.getNote());
    }

    @Override
    public Detail findDetail(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TB_DET, COL_DET_ALL, COL_DET_ID + " = " + id, null, null, null, null, "1");
        Detail det = null;
        if (c.moveToNext()) {
            det = new Detail();
            applyCursor(det, c);
        }
        c.close();
        return det;
    }
    
    static int detId = 0;
    static boolean detId_set;
    
    public synchronized int nextDetailId(){
        if(!detId_set){
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT MAX("+DataMeta.COL_DET_ID+") FROM "+DataMeta.TB_DET,null);
            if(c.moveToNext()){
                detId = c.getInt(0);
            }
            detId_set = true;
            c.close();
        }
        return ++detId;
    }

    @Override
    public void newDetail(Detail detail) {
        int id = nextDetailId();
        try {
            newDetail(id,detail);
        } catch (DuplicateKeyException e) {
            Logger.e(e.getMessage(),e);
        }
    }
    
    public void newDetail(int id,Detail detail) throws DuplicateKeyException{
        if (findDetail(id) != null) {
            throw new DuplicateKeyException("duplicate detail id " + id);
        }
        newDetailNoCheck(id,detail);
    }
    
    @Override
    public void newDetailNoCheck(int id,Detail detail){
        if(Contexts.DEBUG){
            Logger.d("new detail "+id+","+detail.getNote());
        }
        first = null;
        detail.setId(id);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        applyContextValue(detail, cv);
        db.insertOrThrow(TB_DET, null, cv);
    }

    @Override
    public boolean updateDetail(int id, Detail detail) {
        Detail det = findDetail(id);
        if (det == null) {
            return false;
        }
        first = null;
        //set id, detail might have a dirty id from copy or zero
        detail.setId(id);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        applyContextValue(detail,cv);
        
        //use old id to update
        int r = db.update(TB_DET, cv, COL_DET_ID+" = "+id,null);
        return r>0;
    }

    @Override
    public boolean deleteDetail(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        first = null;
        int r = db.delete(TB_DET, COL_DET_ID+" = "+id, null);
        return r>0;
    }

    @Override
    public List<Detail> listAllDetail() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        c = db.query(TB_DET,COL_DET_ALL,null,null, null, null, DET_ORDERBY);
        List<Detail> result = new ArrayList<Detail>();
        Detail det;
        while(c.moveToNext()){
            det = new Detail();
            applyCursor(det,c);
            result.add(det);
        }
        c.close();
        return result;
    }
    
    
    static final String DET_ORDERBY = COL_DET_DATE +" DESC,"+COL_DET_ID+" DESC";

    @Override
    public List<Detail> listDetail(Date start, Date end, int max) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        StringBuilder where = new StringBuilder();
        where.append(" 1=1 ");
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        
        c = db.query(TB_DET,COL_DET_ALL,where.length()==0?null:where.toString(),null, null, null, DET_ORDERBY,max>0?Integer.toString(max):null);
        List<Detail> result = new ArrayList<Detail>();
        Detail det;
        while(c.moveToNext()){
            det = new Detail();
            applyCursor(det,c);
            result.add(det);
        }
        c.close();
        return result;
    }
    @Override
    public List<Detail> listDetail(Account account, int mode, Date start, Date end,int max) {
        return listDetail(account.getId(),mode,start,end,max);
    }
    @Override
    public List<Detail> listDetail(String accountId, int mode, Date start, Date end,int max) {
        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuilder where = new StringBuilder();
        List<String> args = new ArrayList<String>();
        String nestedId = accountId+".%";
        where.append(" 1=1 ");
        if(mode ==LIST_DETAIL_MODE_FROM){
            where.append(" AND (");
            where.append(COL_DET_FROM + " = ? OR ");
            where.append(COL_DET_FROM + " LIKE ? ");
            where.append(")");
            args.add(accountId);
            args.add(nestedId);
        }else if(mode==LIST_DETAIL_MODE_TO){
            where.append(" AND (");
            where.append(COL_DET_TO + " = ? OR ");
            where.append(COL_DET_TO + " LIKE ? ");
            where.append(")");
            args.add(accountId);
            args.add(nestedId);
        }else if(mode==LIST_DETAIL_MODE_BOTH){
            where.append(" AND (");
            where.append(COL_DET_FROM + " = ? OR ");
            where.append(COL_DET_FROM + " LIKE ? OR ");
            where.append(COL_DET_TO + " = ? OR ");
            where.append(COL_DET_TO + " LIKE ? ");
            where.append(")");
            args.add(accountId);
            args.add(nestedId);
            args.add(accountId);
            args.add(nestedId);
        }
        
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        String[] wherearg = null;
        if(args.size()>0){
            wherearg = args.toArray(wherearg = new String[args.size()]);
        }
        Cursor c = null;
        c = db.query(TB_DET,COL_DET_ALL,where.length()==0?null:where.toString(),wherearg, null, null, DET_ORDERBY,max>0?Integer.toString(max):null);
        List<Detail> result = new ArrayList<Detail>();
        Detail det;
        while(c.moveToNext()){
            det = new Detail();
            applyCursor(det,c);
            result.add(det);
        }
        c.close();
        return result;
    }
    
    @Override
    public List<Detail> listDetail(AccountType type, int mode,Date start, Date end, int max) {
        SQLiteDatabase db = helper.getReadableDatabase();

        StringBuilder where = new StringBuilder();
        where.append(" 1=1 ");
        if(mode ==LIST_DETAIL_MODE_FROM){
            where.append(" AND ");
            where.append(COL_DET_FROM_TYPE + "= '" + type.getType()+"'");
        }else if(mode==LIST_DETAIL_MODE_TO){
            where.append(" AND ");
            where.append(COL_DET_TO_TYPE + "= '" + type.getType()+"'");
        }else if(mode==LIST_DETAIL_MODE_BOTH){
            where.append(" AND (");
            where.append(COL_DET_FROM_TYPE + "= '" + type.getType()+"' OR ");
            where.append(COL_DET_TO_TYPE + "= '" + type.getType()+"')");
        }
        
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        
        Cursor c = null;
        c = db.query(TB_DET,COL_DET_ALL,where.length()==0?null:where.toString(),null, null, null, DET_ORDERBY,max>0?Integer.toString(max):null);
        List<Detail> result = new ArrayList<Detail>();
        Detail det;
        while(c.moveToNext()){
            det = new Detail();
            applyCursor(det,c);
            result.add(det);
        }
        c.close();
        return result;
    }
    
    @Override
    public int countDetail(Date start, Date end){
        SQLiteDatabase db = helper.getReadableDatabase();

        StringBuilder query =  new StringBuilder();

        StringBuilder where = new StringBuilder();
        where.append(" 1=1 ");
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        
        query.append("SELECT COUNT(").append(COL_DET_ID).append(") FROM ").append(TB_DET);
        
        if(where.length()>0){
            query.append(" WHERE ").append(where);
        }
        
        
        Cursor c = db.rawQuery(query.toString(),null);
        
        int i = 0;
        if(c.moveToNext()){
            i = c.getInt(0);
        }
        
        c.close();
        return i;
    }
    @Override
    public int countDetail(Account account, int mode,Date start, Date end){
        return countDetail(account.getId(),mode,start,end);
    }
    @Override
    public int countDetail(String accountId, int mode,Date start, Date end){
        SQLiteDatabase db = helper.getReadableDatabase();
        String nestedId = accountId+".%";
        StringBuilder query =  new StringBuilder();
        List<String> args = new ArrayList<String>();
        StringBuilder where = new StringBuilder();
        where.append(" 1=1 ");
        if(mode ==LIST_DETAIL_MODE_FROM){
            where.append(" AND (");
            where.append(COL_DET_FROM + " = ? OR ");
            where.append(COL_DET_FROM + " LIKE ? ");
            where.append(")");
            args.add(accountId);
            args.add(nestedId);
        }else if(mode==LIST_DETAIL_MODE_TO){
            where.append(" AND (");
            where.append(COL_DET_TO + " = ? OR ");
            where.append(COL_DET_TO + " LIKE ? ");
            where.append(")");
            args.add(accountId);
            args.add(nestedId);
        }else if(mode==LIST_DETAIL_MODE_BOTH){
            where.append(" AND (");
            where.append(COL_DET_FROM + " = ? OR ");
            where.append(COL_DET_FROM + " LIKE ? OR ");
            where.append(COL_DET_TO + " = ? OR ");
            where.append(COL_DET_TO + " LIKE ? ");
            where.append(")");
            args.add(accountId);
            args.add(nestedId);
            args.add(accountId);
            args.add(nestedId);
        }
        
        
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        
        query.append("SELECT COUNT(").append(COL_DET_ID).append(") FROM ").append(TB_DET);
        
        if(where.length()>0){
            query.append(" WHERE ").append(where);
        }
        
        String[] wherearg = null;
        if(args.size()>0){
            wherearg = args.toArray(wherearg = new String[args.size()]);
        }
        
        Cursor c = db.rawQuery(query.toString(),wherearg);
        
        int i = 0;
        if(c.moveToNext()){
            i = c.getInt(0);
        }
        
        c.close();
        return i;
    }
    
    @Override
    public int countDetail(AccountType type, int mode,Date start, Date end){
        SQLiteDatabase db = helper.getReadableDatabase();

        StringBuilder query =  new StringBuilder();

        StringBuilder where = new StringBuilder();
        where.append(" 1=1 ");
        
        if(mode ==LIST_DETAIL_MODE_FROM){
            where.append(" AND ");
            where.append(COL_DET_FROM_TYPE + "= '" + type.getType()+"'");
        }else if(mode==LIST_DETAIL_MODE_TO){
            where.append(" AND ");
            where.append(COL_DET_TO_TYPE + "= '" + type.getType()+"'");
        }else if(mode==LIST_DETAIL_MODE_BOTH){
            where.append(" AND (");
            where.append(COL_DET_FROM_TYPE + "= '" + type.getType()+"' OR ");
            where.append(COL_DET_TO_TYPE + "= '" + type.getType()+"')");
        }
        
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        
        query.append("SELECT COUNT(").append(COL_DET_ID).append(") FROM ").append(TB_DET);
        
        if(where.length()>0){
            query.append(" WHERE ").append(where);
        }
        
        
        Cursor c = db.rawQuery(query.toString(),null);
        
        int i = 0;
        if(c.moveToNext()){
            i = c.getInt(0);
        }
        
        c.close();
        return i;
    }

    @Override
    public double sumFrom(AccountType type,Date start, Date end) {
        SQLiteDatabase db = helper.getReadableDatabase();

        StringBuilder query =  new StringBuilder();

        StringBuilder where = new StringBuilder();
        where.append(" WHERE ").append(COL_DET_FROM_TYPE).append(" = '").append(type.type).append("'");
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        
        query.append("SELECT SUM(").append(COL_DET_MONEY).append(") FROM ").append(TB_DET).append(where);

        Cursor c = db.rawQuery(query.toString(),null);
        
        double r = 0D;
        if(c.moveToNext()){
            r = c.getDouble(0);
        }
        
        c.close();
        return r;
    }
    
    @Override
    public double sumFrom(Account acc,Date start, Date end) {
        SQLiteDatabase db = helper.getReadableDatabase();

        StringBuilder query =  new StringBuilder();
        List<String> args = new ArrayList<String>();
        StringBuilder where = new StringBuilder();
        where.append(" WHERE ").append(COL_DET_FROM).append(" = ? ");
        args.add(acc.getId());
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        
        query.append("SELECT SUM(").append(COL_DET_MONEY).append(") FROM ").append(TB_DET).append(where);

        String[] wherearg = null;
        if(args.size()>0){
            wherearg = args.toArray(wherearg = new String[args.size()]);
        }
        
        Cursor c = db.rawQuery(query.toString(),wherearg);
        
        double r = 0D;
        if(c.moveToNext()){
            r = c.getDouble(0);
        }
        
        c.close();
        return r;
    }

    @Override
    public double sumTo(AccountType type,Date start, Date end) {
        SQLiteDatabase db = helper.getReadableDatabase();

        StringBuilder query =  new StringBuilder();

        StringBuilder where = new StringBuilder();
        where.append(" WHERE ").append(COL_DET_TO_TYPE).append(" = '").append(type.type).append("'");
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        
        query.append("SELECT SUM(").append(COL_DET_MONEY).append(") FROM ").append(TB_DET).append(where);

        Cursor c = db.rawQuery(query.toString(),null);
        
        double r = 0D;
        if(c.moveToNext()){
            r = c.getDouble(0);
        }
        
        c.close();
        return r;
    }
    
    @Override
    public double sumTo(Account acc,Date start, Date end) {
        SQLiteDatabase db = helper.getReadableDatabase();

        StringBuilder query =  new StringBuilder();
        List<String> args = new ArrayList<String>();
        StringBuilder where = new StringBuilder();
        where.append(" WHERE ").append(COL_DET_TO).append(" = ?");
        args.add(acc.getId());
        if(start!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + ">=" + start.getTime());
        }
        if(end!=null){
            where.append(" AND ");
            where.append(COL_DET_DATE + "<=" +end.getTime());
        }
        
        query.append("SELECT SUM(").append(COL_DET_MONEY).append(") FROM ").append(TB_DET).append(where);
        String[] wherearg = null;
        if(args.size()>0){
            wherearg = args.toArray(wherearg = new String[args.size()]);
        }

        Cursor c = db.rawQuery(query.toString(),wherearg);
        
        double r = 0D;
        if(c.moveToNext()){
            r = c.getDouble(0);
        }
        
        c.close();
        return r;
    }

    @Override
    public void deleteAllAccount() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TB_ACC, null, null);
        return ;
        
        
    }

    @Override
    public void deleteAllDetail() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TB_DET, null, null);
        detId = 0;
        detId_set = false;
        first = null;
        return ;
        
        
        
    }

    
    Detail first = null;
    
    @Override
    public Detail getFirstDetail() {
        if(first!=null) return first;
        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuilder where = new StringBuilder();
        where.append(" 1=1 ");
        Cursor c = null;
        c = db.query(TB_DET,COL_DET_ALL,where.length()==0?null:where.toString(),null, null, null, COL_DET_DATE,Integer.toString(1));
        first = null;
        if(c.moveToNext()){
            first = new Detail();
            applyCursor(first,c);
        }
        c.close();
        return first;
    }

    @Override
    public double sumInitialValue(AccountType type) {
        SQLiteDatabase db = helper.getReadableDatabase();

        StringBuilder query =  new StringBuilder();

        StringBuilder where = new StringBuilder();
        where.append(" WHERE ").append(COL_ACC_TYPE).append(" = '").append(type.type).append("'");
        
        query.append("SELECT SUM(").append(COL_ACC_INITVAL).append(") FROM ").append(TB_ACC).append(where);

        Cursor c = db.rawQuery(query.toString(),null);
        
        double r = 0D;
        if(c.moveToNext()){
            r = c.getDouble(0);
        }
        
        c.close();
        return r;
    }

}
