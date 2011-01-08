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

    void init();

    void destroyed();
    
    void reset();

    Account findAccount(String id);
    
    Account findAccount(String type,String name);
    
    void newAccount(Account account) throws DuplicateKeyException;
    
    boolean updateAccount(String id,Account account);
    
    boolean deleteAccount(String id);

    /**
     * list account by account type, if type null then return all account
     */
    List<Account> listAccount(AccountType type);
    
    
    /** detail apis **/
    
    Detail findDetail(int id);
    
    void newDetail(Detail detail);
    
    boolean updateDetail(int id,Detail detail);
    
    boolean deleteDetail(int id);

    List<Detail> listAllDetail();
    
}
