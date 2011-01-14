/**
 * 
 */
package com.bottleworks.dailymoney.data;

import java.util.Date;
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
    
    void deleteAllAccount();
    
    void deleteAllDetail();

    Account findAccount(String id);
    
    Account findAccount(String type,String name);
    
    void newAccount(Account account) throws DuplicateKeyException;
    void newAccount(String id,Account account) throws DuplicateKeyException;
    void newAccountNoCheck(String id,Account account);
    
    boolean updateAccount(String id,Account account);
    
    boolean deleteAccount(String id);

    /**
     * list account by account type, if type null then return all account
     */
    List<Account> listAccount(AccountType type);
    
    
    /** detail apis **/
    
    Detail findDetail(int id);
    
    void newDetail(Detail detail);
    void newDetail(int id,Detail detail)throws DuplicateKeyException;
    void newDetailNoCheck(int id,Detail detail);
    
    boolean updateDetail(int id,Detail detail);
    
    boolean deleteDetail(int id);

    List<Detail> listAllDetail();

    int countDetail(Date start, Date end);
    
    List<Detail> listDetail(Date start, Date end, int max);

    double sumFrom(AccountType type,Date start, Date end);
    double sumFrom(Account account,Date start, Date end);

    
    double sumTo(AccountType type,Date start, Date end);
    double sumTo(Account account,Date start, Date end);
    
    
}
