package com.bottleworks.dailymoney.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.bottleworks.commons.util.Logger;
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
        detailList.clear();
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
        newAccount(normalizeAccountId(account.getType(),account.getName()),account);
    }
    
    public synchronized void newAccount(String id, Account account) throws DuplicateKeyException {
        if (findAccount(id) != null) {
            throw new DuplicateKeyException("duplicate account id " + id);
        }
        newAccountNoCheck(id,account);
    }
    public synchronized void newAccountNoCheck(String id, Account account) {
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

        //TODO change all detail's form and to id 
        
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
        try {
            newDetail(nextDetailId(),detail);
        } catch (DuplicateKeyException e) {
            Logger.e(e.getMessage(),e);
        }
    }
    
    public void newDetail(int id,Detail detail) throws DuplicateKeyException{
        if (findDetail(id) != null) {
            throw new DuplicateKeyException("duplicate detail id " + id);
        }
        newDetailNoCheck(id,detail);
    }
    
    public void newDetailNoCheck(int id,Detail detail){
        detail.setId(id);
        detailList.add(detail);
        Collections.sort(detailList,new DetailComparator());
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
        det.setTo(detail.getTo());
        det.setDate(detail.getDate());
        det.setMoney(detail.getMoney());
        det.setNote(detail.getNote());
        det.setArchived(detail.isArchived());
        Collections.sort(detailList,new DetailComparator());
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
    
    static class DetailComparator implements Comparator<Detail>{
        @Override
        public int compare(Detail d1, Detail d2) {
            return d2.getDate().compareTo(d1.getDate());
        }
    }

    @Override
    public List<Detail> listAllDetail() {
        return Collections.unmodifiableList(detailList);
    }

    @Override
    public List<Detail> listDetail(Date start, Date end, int max) {
        List<Detail> r = new ArrayList<Detail>();
        for(Detail d:detailList){
            long l = d.getDate().getTime();
            if( (start==null || l >= start.getTime()) &&  (end==null || l <= end.getTime())){
                r.add(d);
                if(max>0 && r.size()>=max){
                    break;
                }
            }
        }
        return Collections.unmodifiableList(r);
    }
    
    public int countDetail(Date start, Date end){
        return listDetail(start,end,-1).size();
    }

    @Override
    public double sumFrom(AccountType type,Date start, Date end) {
        double sum = 0D;
        for(Detail d:listDetail(start,end,-1)){
            if(d.getFrom().startsWith(type.type)){
                sum += d.getMoney();
            }
        }
        return sum;
    }
    
    @Override
    public double sumFrom(Account acc,Date start, Date end) {
        double sum = 0D;
        for(Detail d:listDetail(start,end,-1)){
            if(d.getFrom().equals(acc.getId())){
                sum += d.getMoney();
            }
        }
        return sum;
    }

    @Override
    public double sumTo(AccountType type,Date start, Date end) {
        double sum = 0D;
        for(Detail d:listDetail(start,end,-1)){
            if(d.getTo().startsWith(type.type)){
                sum += d.getMoney();
            }
        }
        return sum;
    }
    
    @Override
    public double sumTo(Account acc,Date start, Date end) {
        double sum = 0D;
        for(Detail d:listDetail(start,end,-1)){
            if(d.getTo().equals(acc.getId())){
                sum += d.getMoney();
            }
        }
        return sum;
    }

    @Override
    public void deleteAllAccount() {
        accountList.clear();
  
    }

    @Override
    public void deleteAllDetail() {
        detailList.clear();
    }
}
