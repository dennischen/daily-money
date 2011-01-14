package com.bottleworks.dailymoney.reports;

/**
 * 
 * @author dennis
 * 
 */
public class Balance {

    String name;
    
    String type;
    double money;
    
    int indent;

    public Balance(String name, String type, double money) {
        this.name = name;
        this.type = type;
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    

}
