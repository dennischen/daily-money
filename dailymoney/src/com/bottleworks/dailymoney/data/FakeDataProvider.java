package com.bottleworks.dailymoney.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bottleworks.dailymoney.util.Logger;

public class FakeDataProvider implements IDataProvider {

    List<Account> incomeList;
    List<Account> outcomeList;
    List<Account> assetList;
    List<Account> debtList;
    List<Account> otherList;

    public FakeDataProvider() {
        reset();
    }

    public void reset() {
        incomeList = new ArrayList<Account>();
        outcomeList = new ArrayList<Account>();
        assetList = new ArrayList<Account>();
        debtList = new ArrayList<Account>();
        otherList = new ArrayList<Account>();

        try {
            newAccount(new Account(AccountType.INCOME.getType(),
                    "default-income", 0D));
            newAccount(new Account(AccountType.OUTCOME.getType(),
                    "default-outcome", 0D));
            newAccount(new Account(AccountType.ASSET.getType(),
                    "default-asset", 0D));
            newAccount(new Account(AccountType.DEBT.getType(), "default-debt",
                    0D));
        } catch (DuplicateKeyException e) {
            Logger.e(e.getMessage(), e);
        }

    }

    @Override
    public List<Account> listAccount(AccountType type) {

        switch (type) {
        case INCOME:
            return Collections.unmodifiableList(incomeList);
        case OUTCOME:
            return Collections.unmodifiableList(outcomeList);
        case ASSET:
            return Collections.unmodifiableList(assetList);
        case DEBT:
            return Collections.unmodifiableList(debtList);
        case OTHER:
            return Collections.unmodifiableList(debtList);            
        }
        return new ArrayList<Account>();
    }

    @Override
    public synchronized void newAccount(Account account) throws DuplicateKeyException {
        AccountType type = AccountType.find(account.getAccountType());

        switch (type) {
        case INCOME:
            if (incomeList.indexOf(account) != -1) {
                throw new DuplicateKeyException("account id" + account.getId());
            }
            incomeList.add(account);
            break;
        case OUTCOME:
            if (outcomeList.indexOf(account) != -1) {
                throw new DuplicateKeyException("account id" + account.getId());
            }
            outcomeList.add(account);
            break;
        case ASSET:
            if (assetList.indexOf(account) != -1) {
                throw new DuplicateKeyException("account id" + account.getId());
            }
            assetList.add(account);
            break;
        case DEBT:
            if (debtList.indexOf(account) != -1) {
                throw new DuplicateKeyException("account id" + account.getId());
            }
            debtList.add(account);
            break;
        case OTHER:
            if (otherList.indexOf(account) != -1) {
                throw new DuplicateKeyException("account id" + account.getId());
            }
            otherList.add(account);
            break;
        default:
            throw new IllegalStateException("unknow account type " + type);
        }
    }
    
    
    public Account findAccount(String id){
        List<Account>[] all = new List[]{incomeList,outcomeList,assetList,debtList,otherList};
        for(List<Account> l:all){
            for(Account a:l){
                if(a.getId().equals(id)){
                    return a;
                }
            }
        }
        return null;
    }
    
    public Account findAccountByName(String name){
        List<Account>[] all = new List[]{incomeList,outcomeList,assetList,debtList,otherList};
        for(List<Account> l:all){
            for(Account a:l){
                if(a.getName().equals(name)){
                    return a;
                }
            }
        }
        return null;
    }

    @Override
    public synchronized boolean updateAccount(Account account) {
        Account acc = findAccount(account.getId());
        if(acc==null){
            return false;
        }
        if(acc==account) return true;
        acc.setName(account.getName());
        acc.setAccountType(account.getAccountType());
        acc.setInitialValue(account.getInitialValue());
        
        //reset id;
        account.setId(account.getName());
        acc.setId(account.getId());
        return true;
    }

    @Override
    public synchronized boolean deleteAccount(Account account) {
        String id = account.getId();
        List<Account>[] all = new List[]{incomeList,outcomeList,assetList,debtList,otherList};
        for(List<Account> l:all){
            for(Account a:l){
                if(a.getId().equals(id)){
                    l.remove(a);
                    return true;
                }
            }
        }
        return false;
    }

}
