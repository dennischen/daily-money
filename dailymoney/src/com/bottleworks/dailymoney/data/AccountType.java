/**
 * 
 */
package com.bottleworks.dailymoney.data;

import com.bottleworks.dailymoney.R;
import com.bottleworks.dailymoney.util.I18N;


/**
 * @author dennis
 *
 */
public enum AccountType {

    UNKONW("U"),
    INCOME("I"),
    OUTCOME("O"),
    ASSET("A"),
    DEBT("D"),
    OTHER("T");
    
    String type;
    String display;
    AccountType(String type){
        this.type = type;
    }
    public String getType() {
        return type;
    }

    
    
    static public AccountType find(String type){
        if(INCOME.type.equals(type)){
            return INCOME;
        }else if(OUTCOME.type.equals(type)){
            return OUTCOME;
        }else if(ASSET.type.equals(type)){
            return ASSET;
        }else if(DEBT.type.equals(type)){
            return DEBT;
        }else if(OTHER.type.equals(type)){
            return OTHER;
        }
        return UNKONW;
    }
    
    static public String getDisplay(I18N i18n,String type){
        AccountType at = find(type);
        switch (at) {
        case INCOME:
            return i18n.string(R.string.label_income);
        case OUTCOME:
            return i18n.string(R.string.label_outcome);
        case ASSET:
            return i18n.string(R.string.label_asset);
        case DEBT:
            return i18n.string(R.string.label_debt);
        case OTHER:
            return i18n.string(R.string.label_other);
        default:
            return i18n.string(R.string.clabel_unknow);
        }
    }
    
    
}
