package com.bottleworks.dailymoney.reports;

import java.io.Serializable;

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
    Serializable target;

    public Balance(String name, String type, double money,Serializable target) {
        this.name = name;
        this.type = type;
        this.money = money;
        this.target = target;
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

    public Serializable getTarget() {
        return target;
    }

    public void setTarget(Serializable target) {
        this.target = target;
    }
    
    

}
