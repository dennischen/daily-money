package com.bottleworks.dailymoney.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * a fake in memory data provider for development.
 * @author dennis
 *
 */
public class InMemoryDataProvider implements IDataProvider {

    static List<Account> accountList = new ArrayList<Account>();
    static List<Detail> detailList = new ArrayList<Detail>();

    public InMemoryDataProvider() {
    }

    public void reset() {
        accountList.clear();
    }

    @Override
    public List<Account> listAccount(AccountType type) {
        List<Account> list = new ArrayList<Account>();
        for(Account a:accountList){
            if(type==null || type.getType().equals(a.getType())){
                list.add(a);
            }
        }
        return list;
    }

    @Override
    public synchronized void newAccount(Account account) throws DuplicateKeyException {
        String id = normalizeAccountId(account.getType(),account.getName());
        if (findAccount(id) != null) {
            throw new DuplicateKeyException("duplicate account id " + id);
        }
        account.setId(id);
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

    public Account findAccount(String type,String name) {
        String id = normalizeAccountId(type,name);
        return findAccount(id);
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
        id = normalizeAccountId(account.getType(),account.getName());
        account.setId(id);
        acc.setId(id);
        return true;
    }
    
    private String normalizeAccountId(String type,String name){
        name = name.trim().toLowerCase().replace(' ', '-');
        return type+"-"+name;
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
    
    
    static int detailId = 0;
    
    public synchronized int nextDetailId(){
        return ++detailId;
    }

    @Override
    public Detail findDetail(int id) {
        for(Detail d:detailList){
            if(d.getId() == id){
                return d;
            }
        }
        return null;
    }
    
    

    @Override
    public void newDetail(Detail detail){
        int id = nextDetailId();
        detail.setId(id);
        detailList.add(detail);
    }

    @Override
    public boolean updateDetail(int id, Detail detail) {
        Detail det = findDetail(id);
        if (det == null) {
            return false;
        }
        if (det == detail){
            return true;
        }
        //reset id, id is following the name;
        det.setFrom(detail.getFrom());
        det.setFromDisplay(detail.getFromDisplay());
        det.setTo(detail.getTo());
        det.setToDisplay(detail.getToDisplay());
        det.setDate(detail.getDate());
        det.setMoney(detail.getMoney());
        det.setNote(detail.getNote());
        det.setArchived(detail.isArchived());
        return true;
    }

    @Override
    public boolean deleteDetail(int id) {
        for(Detail d:detailList){
            if(d.getId()==id){
                detailList.remove(d);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Detail> listAllDetail() {
        return Collections.unmodifiableList(detailList);
    }
}
