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

    List<Account> listAccount(AccountType type);

}
