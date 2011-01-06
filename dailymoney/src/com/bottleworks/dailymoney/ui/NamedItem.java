package com.bottleworks.dailymoney.ui;

/**
 * 
 * @author dennis
 *
 */
public class NamedItem {

    String name;
    Object value;
    String toString;
    public NamedItem(String name,Object value){
        this(name,value,null);
    }
    public NamedItem(String name,Object value, String toString){
        if(name==null || value==null){
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.value = value;
        this.toString = toString;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    
    public String getToString() {
        return toString;
    }
    public void setToString(String toString) {
        this.toString = toString;
    }
    @Override
    public String toString(){
        if(toString!=null){
            return toString;
        }
        return value.toString();
    }
    
}
