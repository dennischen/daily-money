/**
 * 
 */
package com.bottleworks.dailymoney.data;

import java.util.List;


/**
 * to provide all the data and operation also
 * @author dennis
 *
 */
public interface IDataProvider {

    
    public void reset();

    public Account findAccount(String id);
    
    public Account findAccountByNormalizedName(String name);
    
    List<Account> listAccount(AccountType type);

    void newAccount(Account account) throws DuplicateKeyException;
    
    boolean updateAccount(String id,Account account);
    
    boolean deleteAccount(String id);

    public void init();

    public void destroyed();

}
