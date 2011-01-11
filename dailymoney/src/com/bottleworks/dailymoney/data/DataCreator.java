package com.bottleworks.dailymoney.data;

import java.util.Date;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.R;
import com.bottleworks.dailymoney.ui.Contexts;

/**
 * 
 * @author dennis
 * 
 */
public class DataCreator {

    I18N i18n;
    IDataProvider idp;

    public DataCreator(IDataProvider idp, I18N i18n) {
        this.idp = idp;
        this.i18n = i18n;
    }

    public void createDefaultAccount() {
        createAccountNoThrow(i18n.string(R.string.defacc_salary), AccountType.INCOME, 0D);
        createAccountNoThrow(i18n.string(R.string.defacc_otherincome), AccountType.INCOME, 0D);

        createAccountNoThrow(i18n.string(R.string.defacc_food), AccountType.EXPENSE, 0D);
        createAccountNoThrow(i18n.string(R.string.defacc_entertainment), AccountType.EXPENSE, 0D);
        createAccountNoThrow(i18n.string(R.string.defacc_otherexpense), AccountType.EXPENSE, 0D);

        createAccountNoThrow(i18n.string(R.string.defacc_cash), AccountType.ASSET, 0D);
        createAccountNoThrow(i18n.string(R.string.defacc_bank), AccountType.ASSET, 0D);
        
        createAccountNoThrow(i18n.string(R.string.defacc_creditcard), AccountType.LIABILITY, 0D);
    }

    public void createTestData(int loop) {
        CalendarHelper cal = Contexts.instance().getCalendarHelper();
        
        Account income1 = createAccountNoThrow(i18n.string(R.string.defacc_salary), AccountType.INCOME, 0D);
        Account income2 = createAccountNoThrow(i18n.string(R.string.defacc_otherincome), AccountType.INCOME, 0D);

        Account expense1 = createAccountNoThrow(i18n.string(R.string.defacc_food), AccountType.EXPENSE, 0D);
        Account expense2 = createAccountNoThrow(i18n.string(R.string.defacc_entertainment), AccountType.EXPENSE, 0D);
        Account expense3 = createAccountNoThrow(i18n.string(R.string.defacc_otherexpense), AccountType.EXPENSE, 0D);

        Account asset1 = createAccountNoThrow(i18n.string(R.string.defacc_cash), AccountType.ASSET, 5000D);
        Account asset2 = createAccountNoThrow(i18n.string(R.string.defacc_bank), AccountType.ASSET, 100000D);
        
        Account liability1 = createAccountNoThrow(i18n.string(R.string.defacc_creditcard), AccountType.LIABILITY, 0D);
        
        Account other1 = createAccountNoThrow("Other", AccountType.OTHER, 0D);

        Date today = new Date();
        
        int base = 0;

        for(int i=0;i<loop;i++){
            createDetail(income1, asset1, cal.dateBefore(today, base + 3), 5000D, "salary "+i);
            createDetail(income2, asset2, cal.dateBefore(today, base + 3), 10D, "some where "+i);
    
            createDetail(asset1, expense1, cal.dateBefore(today, base + 2), 100D, "date with Cica "+i);
            createDetail(asset1, expense1, cal.dateBefore(today,base + 2), 30D, "taiwan food is great "+i);
            createDetail(asset1, expense2, cal.dateBefore(today,base + 1), 11D, "buy DVD "+i);
            createDetail(asset1, expense3, cal.dateBefore(today,base + 1), 300D, "it is a secrt  "+i);
    
            createDetail(asset1, asset2, cal.dateBefore(today, base+0), 4000D, "deposit  "+i);
            createDetail(asset2, asset1, cal.dateBefore(today, base+0), 1000D, "drawing  "+i);
            
            createDetail(liability1, expense2, cal.dateBefore(today, base+2), 20.9D, "buy Game "+i);
            createDetail(asset1, liability1, cal.dateBefore(today, base+1), 19.9D, "pay credit card "+i);
            createDetail(asset1, other1, cal.dateBefore(today, base+1), 1D, "salary to other "+i);
            createDetail(other1, liability1, cal.dateBefore(today, base+1), 1D, "other pay credit card "+i);
            
            base = base+5;
        }

    }

    private Detail createDetail(Account from, Account to, Date date, Double money, String note) {
        Detail det = new Detail(from.getId(),to.getId(), date, money, note);
        idp.newDetail(det);
        return det;
    }

    private Account createAccountNoThrow(String name, AccountType type, double initval) {
        try {
            Account account = null;
            if ((account = idp.findAccount(type.getType(), name)) == null) {
                if(Contexts.DEBUG){
                    Logger.d("createDefaultAccount : " + name);
                }
                account = new Account(type.getType(), name, initval);
                idp.newAccount(account);
            }
            return account;
        } catch (DuplicateKeyException e) {
            if(Contexts.DEBUG){
                Logger.d(e.getMessage(), e);
            }
        }
        return null;
    }
}
