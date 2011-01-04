package com.bottleworks.dailymoney.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeDataProvider implements IDataProvider {

    List<Account> incomeList;
    List<Account> outcomeList;
    List<Account> assetList;
    List<Account> debtList;

    public FakeDataProvider() {
        reset();
    }

    public void reset() {
        incomeList = new ArrayList<Account>();
        outcomeList = new ArrayList<Account>();
        assetList = new ArrayList<Account>();
        debtList = new ArrayList<Account>();
        
        incomeList.add(new Account(AccountType.INCOME.getType(),"default-income",0D));
        for(int i=0;i<20;i++){
            incomeList.add(new Account(AccountType.INCOME.getType(),"default-income "+i,0D));
        }
        outcomeList.add(new Account(AccountType.OUTCOME.getType(),"default-outcome",0D));
        assetList.add(new Account(AccountType.ASSET.getType(),"default-asset",0D));
        debtList.add(new Account(AccountType.DEBT.getType(),"default-debt",0D));
    }

    @Override
    public List<Account> listAccount(AccountType type) {
        if(AccountType.INCOME.equals(type)){
            return Collections.unmodifiableList(incomeList);
        }else if(AccountType.OUTCOME.equals(type)){
            return Collections.unmodifiableList(outcomeList);
        }else if(AccountType.ASSET.equals(type)){
            return Collections.unmodifiableList(assetList);
        }else if(AccountType.DEBT.equals(type)){
            return Collections.unmodifiableList(debtList);
        }
        return new ArrayList<Account>();
    }

}
