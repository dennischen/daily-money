package com.bottleworks.dailymoney.data;

import java.io.Serializable;

public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    int id = 0;
    String name = "Default";
    String symbol = "$";
    boolean symboInFront = true;
    String note = "";

    Book(){}

    public Book(String name,String symbol,boolean symboInFront,String note) {
        this.name = name;
        this.symbol = symbol;
        this.symboInFront = symboInFront;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isSymboInFront() {
        return symboInFront;
    }

    public void setSymboInFront(boolean symboInFront) {
        this.symboInFront = symboInFront;
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
        Book other = (Book) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
