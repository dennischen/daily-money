package com.bottleworks.dailymoney.data;

import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.R;

/**
 * 
 * @author dennis
 *
 */
public class DefaultDataCreator {

    I18N i18n;
    IDataProvider idp;
    public DefaultDataCreator(IDataProvider idp,I18N i18n){
        this.idp = idp;
        this.i18n = i18n;
    }
    
    public void createDefaultAccounts(){
        createAccountNoThrow(i18n.string(R.string.defacc_salary),AccountType.INCOME,0D);
        createAccountNoThrow(i18n.string(R.string.defacc_interest),AccountType.INCOME,0D);
        createAccountNoThrow(i18n.string(R.string.defacc_otherincome),AccountType.INCOME,0D);
        
        createAccountNoThrow(i18n.string(R.string.defacc_food),AccountType.EXPENSE,0D);
        createAccountNoThrow(i18n.string(R.string.defacc_entertainment),AccountType.EXPENSE,0D);
        createAccountNoThrow(i18n.string(R.string.defacc_otherexpense),AccountType.EXPENSE,0D);
        
        createAccountNoThrow(i18n.string(R.string.defacc_cash),AccountType.ASSET,0D);
        createAccountNoThrow(i18n.string(R.string.defacc_bank),AccountType.ASSET,0D);
        
    }
    
    private void createAccountNoThrow(String name,AccountType type,double initval){
        try {
            Logger.d("createDefaultAccount : "+name);
            idp.newAccount(new Account(name, type.getType(), initval));
        } catch (DuplicateKeyException e) {
            Logger.d(e.getMessage(),e);
        }
    }
}
