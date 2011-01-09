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
        createAccountNoThrow(i18n.string(R.string.defacc_interest), AccountType.INCOME, 0D);
        createAccountNoThrow(i18n.string(R.string.defacc_otherincome), AccountType.INCOME, 0D);

        createAccountNoThrow(i18n.string(R.string.defacc_food), AccountType.EXPENSE, 0D);
        createAccountNoThrow(i18n.string(R.string.defacc_entertainment), AccountType.EXPENSE, 0D);
        createAccountNoThrow(i18n.string(R.string.defacc_otherexpense), AccountType.EXPENSE, 0D);

        createAccountNoThrow(i18n.string(R.string.defacc_cash), AccountType.ASSET, 0D);
        createAccountNoThrow(i18n.string(R.string.defacc_bank), AccountType.ASSET, 0D);
    }

    public void createTestData() {
        CalendarHelper cal = Contexts.instance().getCalendarHelper();
        
        Account income1 = createAccountNoThrow(i18n.string(R.string.defacc_salary), AccountType.INCOME, 0D);
        Account income2 = createAccountNoThrow(i18n.string(R.string.defacc_interest), AccountType.INCOME, 0D);
        Account income3 = createAccountNoThrow(i18n.string(R.string.defacc_otherincome), AccountType.INCOME, 0D);

        Account expense1 = createAccountNoThrow(i18n.string(R.string.defacc_food), AccountType.EXPENSE, 0D);
        Account expense2 = createAccountNoThrow(i18n.string(R.string.defacc_entertainment), AccountType.EXPENSE, 0D);
        Account expense3 = createAccountNoThrow(i18n.string(R.string.defacc_otherexpense), AccountType.EXPENSE, 0D);

        Account asset1 = createAccountNoThrow(i18n.string(R.string.defacc_cash), AccountType.ASSET, 5000D);
        Account asset2 = createAccountNoThrow(i18n.string(R.string.defacc_bank), AccountType.ASSET, 100000D);

        Date today = new Date();

        createDetail(income1, asset1, cal.dateBefore(today, 10), 5000D, "Salary");
        createDetail(income2, asset2, cal.dateBefore(today, 5), 10D, "Interset");

        createDetail(asset1, expense1, cal.dateBefore(today, 2), 100D, "date with Cica");
        createDetail(asset1, expense1, cal.yesterday(today), 30D, "taiwan food is great");
        createDetail(asset1, expense2, cal.yesterday(today), 11D, "buy DVD");
        createDetail(asset1, expense3, today, 300D, "it is a secrt ");

        createDetail(asset1, asset2, cal.dateBefore(today, 9), 4000D, "deposit 1");
        createDetail(asset2, asset1, cal.dateBefore(today, 15), 1000D, "drawing 1");
        
        createDetail(asset1, asset2, cal.dateBefore(today, 39), 4111D, "deposit 2");
        createDetail(asset2, asset1, cal.dateBefore(today, 45), 1111D, "drawing 2");
        
        createDetail(asset1, asset2, cal.dateBefore(today, 69), 4222D, "deposit 3");
        createDetail(asset2, asset1, cal.dateBefore(today, 75), 1222D, "drawing 3");

    }

    private Detail createDetail(Account from, Account to, Date date, Double money, String note) {
        Detail det = new Detail(from.getId(),to.getId(), date, money, note);
        idp.newDetail(det);
        return det;
    }

    private Account createAccountNoThrow(String name, AccountType type, double initval) {
        try {
            Logger.d("createDefaultAccount : " + name);
            Account account = null;
            if ((account = idp.findAccount(type.getType(), name)) == null) {
                account = new Account(type.getType(), name, initval);
                idp.newAccount(account);
            }
            return account;
        } catch (DuplicateKeyException e) {
            Logger.d(e.getMessage(), e);
        }
        return null;
    }
}
