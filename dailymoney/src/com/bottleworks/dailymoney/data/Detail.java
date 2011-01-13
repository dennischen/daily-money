package com.bottleworks.dailymoney.data;

import java.io.Serializable;
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
    private Double money;
    private String note;
    
    private boolean archived;

    Detail(){}

    public Detail(String from,String to,Date date, Double money,
            String note) {
        this.date = date;
        this.money = money;
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

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
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


}
