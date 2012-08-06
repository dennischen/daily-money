package com.bottleworks.dailymoney.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author dennis
 * 
 */
public class Balance {

    String name;
    String type;
    BigDecimal money;
    int indent;
    Serializable target;
    Date date;

    List<Balance> group;

    public Balance(String name, String type, BigDecimal money, Serializable target) {
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

    public BigDecimal getMoney() {
        return money == null ? BigDecimal.ZERO : money;
    }

    public void setMoney(BigDecimal money) {
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

    public List<Balance> getGroup() {
        return group;
    }

    public void setGroup(List<Balance> group) {
        this.group = group;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    
}
