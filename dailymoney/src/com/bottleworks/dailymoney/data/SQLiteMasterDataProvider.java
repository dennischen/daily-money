package com.bottleworks.dailymoney.data;

import static com.bottleworks.dailymoney.data.MasterDataMeta.*;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.context.Contexts;

/**
 * 
 * @author dennis
 * 
 */
public class SQLiteMasterDataProvider implements IMasterDataProvider {

    SQLiteMasterDataHelper helper;
    CalendarHelper calHelper;

    public SQLiteMasterDataProvider(SQLiteMasterDataHelper helper,CalendarHelper calHelper) {
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

    @Override
    public void reset() {
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onUpgrade(db, -1, db.getVersion());
        bookId = 0;
        bookId_set = false;
    }

   

    /**
     * book impl.
     */

    private void applyCursor(Book book, Cursor c) {
        int i = 0;
        for (String n : c.getColumnNames()) {
            if (n.equals(COL_BOOK_ID)) {
                book.setId(c.getInt(i));
            } else if (n.equals(COL_BOOK_NAME)) {
                book.setName(c.getString(i));
            } else if (n.equals(COL_BOOK_SYMBOL)) {
                book.setSymbol(c.getString(i));
            } else if (n.equals(COL_BOOK_SYMBOL_POSITION)) {
                book.setSymbolPosition(SymbolPosition.find(c.getInt(i)));
            } else if (n.equals(COL_BOOK_NOTE)) {
                book.setNote(c.getString(i));
            }
            i++;
        }
    }

    private void applyContextValue(Book book, ContentValues values) {
        values.put(COL_BOOK_ID, book.getId());
        values.put(COL_BOOK_NAME, book.getName());
        values.put(COL_BOOK_SYMBOL, book.getSymbol());
        values.put(COL_BOOK_SYMBOL_POSITION, book.getSymbolPosition().getType());
        values.put(COL_BOOK_NOTE, book.getNote());
    }

    @Override
    public Book findBook(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TB_BOOK, COL_BOOK_ALL, COL_BOOK_ID + " = " + id, null, null, null, null, "1");
        Book book = null;
        if (c.moveToNext()) {
            book = new Book();
            applyCursor(book, c);
        }
        c.close();
        return book;
    }
    
    static int bookId = 0;
    static boolean bookId_set;
    
    public synchronized int nextBookId(){
        if(!bookId_set){
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT MAX("+MasterDataMeta.COL_BOOK_ID+") FROM "+MasterDataMeta.TB_BOOK,null);
            if(c.moveToNext()){
                bookId = c.getInt(0);
            }
            bookId_set = true;
            c.close();
        }
        return ++bookId;
    }

    @Override
    public void newBook(Book bookail) {
        int id = nextBookId();
        try {
            newBook(id,bookail);
        } catch (DuplicateKeyException e) {
            Logger.e(e.getMessage(),e);
        }
    }
    
    public void newBook(int id,Book book) throws DuplicateKeyException{
        if (findBook(id) != null) {
            throw new DuplicateKeyException("duplicate book id " + id);
        }
        newBookNoCheck(id,book);
    }
    
    @Override
    public void newBookNoCheck(int id,Book book){
        if(Contexts.DEBUG){
            Logger.d("new book "+id+","+book.getName());
        }
        book.setId(id);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        applyContextValue(book, cv);
        db.insertOrThrow(TB_BOOK, null, cv);
    }

    @Override
    public boolean updateBook(int id, Book book) {
        Book det = findBook(id);
        if (det == null) {
            return false;
        }
        //set id, book might have a dirty id from copy or zero
        book.setId(id);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        applyContextValue(book,cv);
        
        //use old id to update
        int r = db.update(TB_BOOK, cv, COL_BOOK_ID+" = "+id,null);
        return r>0;
    }

    @Override
    public boolean deleteBook(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int r = db.delete(TB_BOOK, COL_BOOK_ID+" = "+id, null);
        return r>0;
    }

    @Override
    public List<Book> listAllBook() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        c = db.query(TB_BOOK,COL_BOOK_ALL,null,null, null, null, BOOK_ORDERBY);
        List<Book> result = new ArrayList<Book>();
        Book det;
        while(c.moveToNext()){
            det = new Book();
            applyCursor(det,c);
            result.add(det);
        }
        c.close();
        return result;
    }

    static final String BOOK_ORDERBY = COL_BOOK_ID+" ASC";

}
