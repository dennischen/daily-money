package com.bottleworks.dailymoney.data;

import java.util.ArrayList;
import java.util.List;
/**
 * a fake in memory data provider for development.
 * @author dennis
 *
 */
public class InMemoryDataProvider implements IDataProvider {

    static List<Account> accountList = new ArrayList<Account>();

    public InMemoryDataProvider() {
    }

    public void reset() {
        accountList.clear();
    }

    @Override
    public List<Account> listAccount(AccountType type) {
        List<Account> list = new ArrayList<Account>();
        for(Account a:accountList){
            if(type.getType().equals(a.getType())){
                list.add(a);
            }
        }
        return list;
    }

    @Override
    public synchronized void newAccount(Account account) throws DuplicateKeyException {
        String id = normalizeName(account.getName());
        if (findAccount(id) != null) {
            throw new DuplicateKeyException("duplicate account id " + id);
        }
        account.setId(normalizeName(id));
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

    public Account findAccountByNormalizedName(String name) {
        name = normalizeName(name);
        return findAccount(name);
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
        acc.setType(account.getType());
        acc.setInitialValue(account.getInitialValue());

        // reset id;
        id = normalizeName(account.getName());
        account.setId(id);
        acc.setId(id);
        return true;
    }
    
    private String normalizeName(String name){
        name = name.trim().toLowerCase().replace(' ', '-');
        return name;
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

    @Override
    public void init() {
    }

    @Override
    public void destroyed() {
    }

}
