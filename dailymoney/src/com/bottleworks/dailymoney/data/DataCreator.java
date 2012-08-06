package com.bottleworks.dailymoney.data;

import java.math.BigDecimal;
import java.util.Date;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.core.R;

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
        createAccountNoThrow(i18n.string(R.string.defacc_salary), AccountType.INCOME, BigDecimal.ZERO, false);
        createAccountNoThrow(i18n.string(R.string.defacc_otherincome), AccountType.INCOME, BigDecimal.ZERO, false);

        createAccountNoThrow(i18n.string(R.string.defacc_food1), AccountType.EXPENSE, BigDecimal.ZERO, false);
        createAccountNoThrow(i18n.string(R.string.defacc_food2), AccountType.EXPENSE, BigDecimal.ZERO, false);
        createAccountNoThrow(i18n.string(R.string.defacc_entertainment), AccountType.EXPENSE, BigDecimal.ZERO, false);
        createAccountNoThrow(i18n.string(R.string.defacc_otherexpense), AccountType.EXPENSE, BigDecimal.ZERO, false);

        createAccountNoThrow(i18n.string(R.string.defacc_cash), AccountType.ASSET, BigDecimal.ZERO, true);
        createAccountNoThrow(i18n.string(R.string.defacc_bank1), AccountType.ASSET, BigDecimal.ZERO, false);
        createAccountNoThrow(i18n.string(R.string.defacc_bank2), AccountType.ASSET, BigDecimal.ZERO, false);

        createAccountNoThrow(i18n.string(R.string.defacc_creditcard), AccountType.LIABILITY, BigDecimal.ZERO, false);
    }

    public void createTestData(int loop) {
        //only for call from ui, so use uiInstance
        CalendarHelper cal = Contexts.instance().getCalendarHelper();
        
        Account income1 = createAccountNoThrow(i18n.string(R.string.defacc_salary), AccountType.INCOME, BigDecimal.ZERO, false);
        Account income2 = createAccountNoThrow(i18n.string(R.string.defacc_otherincome), AccountType.INCOME, BigDecimal.ZERO, false);

        Account expense1 = createAccountNoThrow(i18n.string(R.string.defacc_food1), AccountType.EXPENSE, BigDecimal.ZERO, false);
        Account expense2 = createAccountNoThrow(i18n.string(R.string.defacc_entertainment), AccountType.EXPENSE, BigDecimal.ZERO, false);
        Account expense3 = createAccountNoThrow(i18n.string(R.string.defacc_otherexpense), AccountType.EXPENSE, BigDecimal.ZERO, false);

        Account asset1 = createAccountNoThrow(i18n.string(R.string.defacc_cash), AccountType.ASSET, new BigDecimal("5000"), true);
        Account asset2 = createAccountNoThrow(i18n.string(R.string.defacc_bank1), AccountType.ASSET, new BigDecimal("100000"), false);

        Account liability1 = createAccountNoThrow(i18n.string(R.string.defacc_creditcard), AccountType.LIABILITY, BigDecimal.ZERO, false);

        Account other1 = createAccountNoThrow("Other", AccountType.OTHER, BigDecimal.ZERO, false);

        Date today = new Date();
        
        int base = 0;

        for(int i=0;i<loop;i++){
            createDetail(income1, asset1, cal.dateBefore(today, base + 3), new BigDecimal("5000"), "salary " + i);
            createDetail(income2, asset2, cal.dateBefore(today, base + 3), new BigDecimal("10"), "some where " + i);

            createDetail(asset1, expense1, cal.dateBefore(today, base + 2), new BigDecimal("100"), "date with Cica " + i);
            createDetail(asset1, expense1, cal.dateBefore(today, base + 2), new BigDecimal("30"), "taiwan food is great " + i);
            createDetail(asset1, expense2, cal.dateBefore(today, base + 1), new BigDecimal("11"), "buy DVD " + i);
            createDetail(asset1, expense3, cal.dateBefore(today, base + 1), new BigDecimal("300"), "it is a secrt  " + i);

            createDetail(asset1, asset2, cal.dateBefore(today, base + 0), new BigDecimal("4000"), "deposit  " + i);
            createDetail(asset2, asset1, cal.dateBefore(today, base + 0), new BigDecimal("1000"), "drawing  " + i);

            createDetail(liability1, expense2, cal.dateBefore(today, base + 2), new BigDecimal("20.9"), "buy Game " + i);
            createDetail(asset1, liability1, cal.dateBefore(today, base + 1), new BigDecimal("19.9"), "pay credit card " + i);
            createDetail(asset1, other1, cal.dateBefore(today, base + 1), new BigDecimal("1"), "salary to other " + i);
            createDetail(other1, liability1, cal.dateBefore(today, base + 1), new BigDecimal("1"), "other pay credit card " + i);
            
            base = base+5;
        }

    }

    private Detail createDetail(Account from, Account to, Date date, BigDecimal money, String note) {
        Detail det = new Detail(from.getId(),to.getId(), date, money, note);
        idp.newDetail(det);
        return det;
    }

    private Account createAccountNoThrow(String name, AccountType type, BigDecimal initval, boolean cashAccount) {
        try {
            Account account = null;
            if ((account = idp.findAccount(type.getType(), name)) == null) {
                if(Contexts.DEBUG){
                    Logger.d("createDefaultAccount : " + name);
                }
                account = new Account(type.getType(), name, initval);
                account.setCashAccount(cashAccount);
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
