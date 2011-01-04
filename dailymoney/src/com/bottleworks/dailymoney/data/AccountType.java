/**
 * 
 */
package com.bottleworks.dailymoney.data;


/**
 * @author dennis
 *
 */
public enum AccountType {

    INCOME("I","income"),
    OUTCOME("O","outcome"),
    ASSET("A","asset"),
    DEBT("D","debt");
    
    String type;
    String display;
    AccountType(String type,String display){
        this.type = type;
        this.display = display;
    }
    public String getType() {
        return type;
    }
    public String getDisplay() {
        return display;
    }
    
    
    public AccountType find(String type){
        if(INCOME.type.equals(type)){
            return INCOME;
        }else if(OUTCOME.type.equals(type)){
            return OUTCOME;
        }else if(ASSET.type.equals(type)){
            return ASSET;
        }else if(DEBT.type.equals(type)){
            return DEBT;
        }
        throw new IllegalStateException("unkonw type "+type);
    }
    
    
}
