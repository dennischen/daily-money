package com.bottleworks.dailymoney.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bottleworks.dailymoney.util.Logger;
/**
 * a fake in memory data provider for development.
 * @author dennis
 *
 */
public class FakeDataProvider implements IDataProvider {

    List<Account> accountList;

    public FakeDataProvider() {
        reset();
    }

    public void reset() {
        accountList = new ArrayList<Account>();

        try {
            newAccount(new Account("default-income", AccountType.INCOME.getType(), 0D));
            newAccount(new Account("default-outcome", AccountType.OUTCOME.getType(), 0D));
            newAccount(new Account("default-asset", AccountType.ASSET.getType(), 0D));
            newAccount(new Account("default-debt", AccountType.DEBT.getType(), 0D));
        } catch (DuplicateKeyException e) {
            Logger.e(e.getMessage(), e);
        }

    }

    @Override
    public List<Account> listAccount(AccountType type) {
        List<Account> list = new ArrayList<Account>();
        for(Account a:accountList){
            if(type.getType().equals(a.getAccountType())){
                list.add(a);
            }
        }
        return list;
    }

    @Override
    public synchronized void newAccount(Account account) throws DuplicateKeyException {
        account.setId(account.getName());
        if (accountList.indexOf(account) != -1) {
            throw new DuplicateKeyException("account id" + account.getId());
        }
        accountList.add(account);
    }

    public Account findAccount(String id) {
        for(Account a:accountList){
            if(a.getId().equals(id)){
                return a;
            }
        }
        return null;
    }

    public Account findAccountByName(String name) {
        for(Account a:accountList){
            if(a.getName().equals(name)){
                return a;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean updateAccount(String id,Account account) {
        Account acc = findAccount(id);
        if (acc == null) {
            return false;
        }
        if (acc == account){
            return true;
        }
        //reset id, id is following the name;
        acc.setName(account.getName());
        acc.setAccountType(account.getAccountType());
        acc.setInitialValue(account.getInitialValue());

        // reset id;
        account.setId(account.getName());
        acc.setId(account.getId());
        return true;
    }

    @Override
    public synchronized boolean deleteAccount(String id) {
        for(Account a:accountList){
            if(a.getId().equals(id)){
                accountList.remove(a);
                return true;
            }
        }
        return false;
    }

}
