package com.bottleworks.dailymoney.data;

import java.util.Date;

/**
 * 
 * @author dennis
 * 
 */
public class Detail {

    private int id;
    private String from;
    private String fromDisplay;
    private String to;
    private String toDisplay;
    private Date date;
    private Double money;
    private String note;
    
    private boolean archived;
    

    public Detail(String fromAccount, String fromDisplay, String toAccount, String toDisplay, Date date, Double money,
            String note) {
        this.from = fromAccount;
        this.fromDisplay = fromDisplay;
        this.to = toAccount;
        this.toDisplay = toDisplay;
        this.date = date;
        this.money = money;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }


    public String getFromDisplay() {
        return fromDisplay;
    }

    public void setFromDisplay(String fromDisplay) {
        this.fromDisplay = fromDisplay;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getToDisplay() {
        return toDisplay;
    }

    public void setToDisplay(String toDisplay) {
        this.toDisplay = toDisplay;
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
