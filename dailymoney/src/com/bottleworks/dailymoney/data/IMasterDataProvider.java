package com.bottleworks.dailymoney.data;

import java.util.List;

/**
 * interface to get master data , such as Book etc.
 * @author dennis
 *
 */
public interface IMasterDataProvider {

    void init();

    void destroyed();
    
    void reset();

    Book findBook(int id);
    
    void newBook(Book book);
    void newBook(int id,Book book) throws DuplicateKeyException;
    void newBookNoCheck(int id,Book book);
    
    boolean updateBook(int id,Book book);
    
    boolean deleteBook(int id);

    List<Book> listAllBook();

}
