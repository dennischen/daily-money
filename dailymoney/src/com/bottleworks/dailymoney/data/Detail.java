package com.bottleworks.dailymoney.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author dennis
 * 
 */
public class Detail implements Serializable{

    private static final long serialVersionUID = 1L;
    private int id;
    private String from;
    private String to;
    
    private String fromType;
    private String toType;
    
    private Date date;
    private double money;
    private BigDecimal moneyBD;
    private String note;
    private int period;
    private int periodUnit;
    private int periods;
    private int paymentType;

    private boolean archived;

    Detail(){}

    public Detail(String from,String to,Date date, double money,
            String note) {
        this.date = date;
        this.money = money;
        this.moneyBD = BigDecimal.valueOf(money);
        this.note = note;
        setFrom(from);
        setTo(to);
    }

    public Detail(String from, String to, Date date, BigDecimal moneyBD, String note) {
        this.date = date;
        this.money = moneyBD.doubleValue();
        this.moneyBD = moneyBD;
        this.note = note;
        setFrom(from);
        setTo(to);
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
        if(from!=null && from.length()>0){
            fromType = from.substring(0,1);
        }
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
        if(to!=null && to.length()>0){
            toType = to.substring(0,1);
        }
    }
    
    

    public String getFromType() {
        return fromType;
    }

    public String getToType() {
        return toType;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Detail other = (Detail) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPeriodUnit(int periodUnit) {
        this.periodUnit = periodUnit;
    }

    public int getPeriodUnit() {
        return periodUnit;
    }

    public void setMoneyBD(BigDecimal moneyBD) {
        this.moneyBD = moneyBD;
    }

    public BigDecimal getMoneyBD() {
        return moneyBD;
    }

}
