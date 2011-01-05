/**
 * 
 */
package com.bottleworks.dailymoney.data;

import java.util.List;


/**
 * @author dennis
 *
 */
public interface IDataProvider {

    
    public void reset();

    public Account findAccount(String id);
    
    public Account findAccountByName(String name);
    
    List<Account> listAccount(AccountType type);

    void newAccount(Account account) throws DuplicateKeyException;
    
    boolean updateAccount(Account account);
    
    boolean deleteAccount(Account account);

}
