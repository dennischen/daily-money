package com.bottleworks.dailymoney.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bottleworks.dailymoney.context.Contexts;

public class BalanceHelper {
    
    static Contexts contexts(){
        return Contexts.instance();
    }

    
    public static List<Balance> adjustTotalBalance(AccountType type, String totalName, List<Balance> items) {
        if(items.size()==0){
            return items;
        }
        List<Balance> group = new ArrayList<Balance>(items);
        double total = 0;
        for (Balance b : items) {
            b.setIndent(1);
            b.setGroup(group);
            total += b.getMoney();
        }
        Balance bt = new Balance(totalName,type.getType(), total,type);
        bt.setIndent(0);
        bt.setGroup(group);
        bt.setDate(items.get(0).getDate());
        items.add(0, bt);
        return items;
    }

    public static List<Balance> calculateBalanceList(AccountType type,Date start,Date end) {
        boolean nat = type==AccountType.INCOME||type==AccountType.LIABILITY;
        IDataProvider idp = contexts().getDataProvider();
        boolean calInit = true;
        if(start!=null){
            calInit = false;
        }else{
            Detail first = idp.getFirstDetail();
            //don't calculate init val if the first record date in after end data
            if(first!=null && first.getDate().after(end)){
                calInit = false;
            }
        }
        List<Account> accs = idp.listAccount(type);
        List<Balance> blist = new ArrayList<Balance>();
        for (Account acc : accs) {
            double from = idp.sumFrom(acc, start, end);
            double to = idp.sumTo(acc, start, end);
            double init = calInit?acc.getInitialValue():0;
            double b = init + (nat?(from - to):(to - from));
            Balance balance = new Balance(acc.getName(),type.getType(), b,acc);
            balance.setDate(end);
            blist.add(balance);
        }
        return blist;
    }
    
    public static Balance calculateBalance(AccountType type,Date start,Date end) {
        boolean nat = type==AccountType.INCOME||type==AccountType.LIABILITY;
        IDataProvider idp = contexts().getDataProvider();
        boolean calInit = true;
        if(start!=null){
            calInit = false;
        }else{
            Detail first = idp.getFirstDetail();
            //don't calculate init val if the first record date in after end data
            if(first!=null && first.getDate().after(end)){
                calInit = false;
            }
        }
        
        double from = idp.sumFrom(type, start, end);
        double to = idp.sumTo(type, start, end);

        double init = calInit ? idp.sumInitialValue(type) : 0;

        double b = init + (nat ? (from - to) : (to - from));
        Balance balance = new Balance(type.getDisplay(contexts().getI18n()), type.getType(), b, type);
        balance.setDate(end);
            
        return balance;
    }
    
    public static Balance calculateBalance(Account acc, Date start, Date end) {
        AccountType type = AccountType.find(acc.getType());
        boolean nat = type == AccountType.INCOME || type == AccountType.LIABILITY;
        IDataProvider idp = contexts().getDataProvider();
        boolean calInit = true;
        if(start!=null){
            calInit = false;
        }else{
            Detail first = idp.getFirstDetail();
            //don't calculate init val if the first record date in after end data
            if(first!=null && first.getDate().after(end)){
                calInit = false;
            }
        }
        double from = idp.sumFrom(acc, start, end);
        double to = idp.sumTo(acc, start, end);
        double init = calInit ? acc.getInitialValue() : 0;
        double b = init + (nat ? (from - to) : (to - from));
        Balance balance = new Balance(acc.getName(), type.getType(), b, acc);
        balance.setDate(end);

        return balance;
    }
}
