/**
 * 
 */
package com.bottleworks.dailymoney.data;

import com.bottleworks.commons.util.I18N;
import com.bottleworks.dailymoney.R;


/**
 * @author dennis
 *
 */
public enum AccountType {

    UNKONW("Z",R.drawable.na),
    INCOME("A",R.drawable.acc_tab_income),
    EXPENSE("B",R.drawable.acc_tab_expense),
    ASSET("C",R.drawable.acc_tab_asset),
    LIABILITY("D",R.drawable.acc_tab_liability),
    OTHER("E",R.drawable.acc_tab_other);
    
    String type;
    int drawable;
    AccountType(String type,int drawable){
        this.type = type;
        this.drawable = drawable;
    }
    public String getType() {
        return type;
    }
    
    
    public String getDisplay(I18N i18n){
        return getDisplay(i18n,type);
    }
    public int getDrawable() {
        return drawable;
    }

    static final AccountType[] supported = new  AccountType[]{INCOME,EXPENSE,ASSET,LIABILITY,OTHER};
    
    static final AccountType[] from = new  AccountType[]{ASSET,INCOME,LIABILITY,OTHER};
    
    static final AccountType[] fromIncome = new  AccountType[]{ASSET,EXPENSE,LIABILITY,OTHER};
    static final AccountType[] fromAsset = new  AccountType[]{EXPENSE,ASSET,LIABILITY,OTHER};
    static final AccountType[] fromUnknow = new  AccountType[]{};
    static final AccountType[] fromExpense = new  AccountType[]{};
    static final AccountType[] fromLiability = new  AccountType[]{EXPENSE,ASSET,LIABILITY,OTHER};
    static final AccountType[] fromOther = new  AccountType[]{EXPENSE,ASSET,LIABILITY,OTHER};
    
    static public AccountType[] getSupportedType(){
        return supported;
    }
    
    static public AccountType find(String type){
        if(INCOME.type.equals(type)){
            return INCOME;
        }else if(EXPENSE.type.equals(type)){
            return EXPENSE;
        }else if(ASSET.type.equals(type)){
            return ASSET;
        }else if(LIABILITY.type.equals(type)){
            return LIABILITY;
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
        case EXPENSE:
            return i18n.string(R.string.label_expense);
        case ASSET:
            return i18n.string(R.string.label_asset);
        case LIABILITY:
            return i18n.string(R.string.label_liability);
        case OTHER:
            return i18n.string(R.string.label_other);
        default:
            return i18n.string(R.string.clabel_unknow);
        }
    }

    public static AccountType[] getFromType() {
        return from;
    }
    public static AccountType[] getToType(String fromType) {
        AccountType at = find(fromType);
        switch (at) {
        case INCOME:
            return fromIncome;
        case EXPENSE:
            return fromExpense;
        case ASSET:
            return fromAsset;
        case LIABILITY:
            return fromLiability;
        case OTHER:
            return fromOther;
        default:
            return fromUnknow;
        }
    }
    
    
}
