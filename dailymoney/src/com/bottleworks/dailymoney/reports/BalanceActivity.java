package com.bottleworks.dailymoney.reports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.R;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.IDataProvider;

/**
 * 
 * @author dennis
 * 
 */
public class BalanceActivity extends ContextsActivity implements OnClickListener {

    public static final int MODE_DAY = 0;
    public static final int MODE_MONTH = 1;
    public static final int MODE_YEAR = 2;

    public static final String INTENT_BALANCE_DATE = "balanceDate";
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_TARGET_DATE = "target";

    TextView infoView;
    View toolbarView;

    private Date targetDate;
    private Date currentDate;
    private int mode = MODE_DAY;

    private DateFormat endDateFormat;

    ImageButton modeBtn;

    CalendarHelper calHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_balance);
        initialIntent();
        initialContent();
        GUIs.delayPost(new Runnable() {
            @Override
            public void run() {
                reloadData();
            }
        }, 25);
    }

    private void initialIntent() {
        Bundle b = getIntentExtras();
        mode = b.getInt(INTENT_MODE, MODE_DAY);
        Object o = b.get(INTENT_BALANCE_DATE);
        if (o instanceof Date) {
            targetDate = (Date) o;
        } else {
            targetDate = new Date();
        }
        currentDate = targetDate;
    }

    private void initialContent() {

        endDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        infoView = (TextView) findViewById(R.id.report_balance_infobar);
        toolbarView = findViewById(R.id.report_balance_toolbar);

        findViewById(R.id.report_balance_prev).setOnClickListener(this);
        findViewById(R.id.report_balance_next).setOnClickListener(this);
        findViewById(R.id.report_balance_today).setOnClickListener(this);
        modeBtn = (ImageButton) findViewById(R.id.report_balance_mode);
        modeBtn.setOnClickListener(this);
        reloadToolbar();
    }

    private void reloadToolbar() {
        switch (mode) {
        case MODE_MONTH:
            modeBtn.setImageResource(R.drawable.btn_year);
            break;
        case MODE_YEAR:
            modeBtn.setImageResource(R.drawable.btn_day);
            break;
        default:
            modeBtn.setImageResource(R.drawable.btn_month);
            break;
        }
    }

    private List<Balance> adjustTotalBalance(AccountType type, String totalName, List<Balance> items) {
        double total = 0;
        for (Balance b : items) {
            b.setIndent(1);
            total += b.money;
        }
        Balance bt = new Balance(totalName, total);
        items.add(0, bt);
        return items;
    }

    private List<Balance> calBalance(AccountType type, Date end, boolean nat) {
        IDataProvider idp = Contexts.uiInstance().getDataProvider();
        List<Account> accs = idp.listAccount(type);
        List<Balance> blist = new ArrayList<Balance>();
        for (Account acc : accs) {
            double from = idp.sumFrom(acc, null, end);
            double to = idp.sumTo(acc, null, end);
            double init = acc.getInitialValue();
            double b = init + to - from;
            Balance balance = new Balance(acc.getName(), ((nat & b != 0) ? -b : b));
            blist.add(balance);
        }
        return blist;
    }

    private void reloadData() {
        final CalendarHelper cal = Contexts.uiInstance().getCalendarHelper();
        final Date end;
        infoView.setText("");
        reloadToolbar();
        switch (mode) {
        case MODE_MONTH:
            end = cal.monthEndDate(currentDate);
            break;
        case MODE_YEAR:
            end = cal.yearEndDate(currentDate);
            break;
        default:
            end = cal.toDayEnd(currentDate);
            break;
        }
        GUIs.doBusy(this, new GUIs.BusyAdapter() {
            List<Balance> all = new ArrayList<Balance>();

            @Override
            public void run() {
                List<Balance> income = calBalance(AccountType.INCOME, end, true);
                income = adjustTotalBalance(AccountType.INCOME, i18n.string(R.string.label_balt_income), income);
                all.addAll(income);

                List<Balance> expense = calBalance(AccountType.EXPENSE, end, false);
                expense = adjustTotalBalance(AccountType.INCOME, i18n.string(R.string.label_balt_expense), expense);
                all.addAll(expense);

                List<Balance> asset = calBalance(AccountType.ASSET, end, false);
                asset = adjustTotalBalance(AccountType.INCOME, i18n.string(R.string.label_balt_asset), asset);
                all.addAll(asset);

                List<Balance> liability = calBalance(AccountType.LIABILITY, end, false);
                liability = adjustTotalBalance(AccountType.INCOME, i18n.string(R.string.label_balt_liability),
                        liability);
                all.addAll(liability);

                List<Balance> other = calBalance(AccountType.OTHER, end, false);
                other = adjustTotalBalance(AccountType.INCOME, i18n.string(R.string.label_balt_other), other);
                all.addAll(other);
            }

            @Override
            public void onBusyFinish() {

                StringBuilder sb = new StringBuilder();
                for (Balance b : all) {
                    for (int i = 0; i < b.indent; i++) {
                        sb.append("> ");
                    }
                    sb.append(b.name + " : " + Formats.double2String(b.money)).append("\n");
                }
                System.out.println(">>>>>>>>>>" +sb.toString());
                ((TextView) findViewById(R.id.text)).setText(sb.toString());

                // update info
                infoView.setText(i18n.string(R.string.label_balance_to_day, endDateFormat.format(end)));

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.report_balance_prev:
            onPrev();
            break;
        case R.id.report_balance_next:
            onNext();
            break;
        case R.id.report_balance_today:
            onToday();
            break;
        case R.id.report_balance_mode:
            onMode();
            break;
        }
    }

    private void onMode() {
        switch (mode) {
        case MODE_DAY:
            mode = MODE_MONTH;
            reloadData();
            break;
        case MODE_MONTH:
            mode = MODE_YEAR;
            reloadData();
            break;
        case MODE_YEAR:
            mode = MODE_DAY;
            reloadData();
            break;
        }
    }

    private void onNext() {
        CalendarHelper cal = Contexts.uiInstance().getCalendarHelper();
        switch (mode) {
        case MODE_DAY:
            currentDate = cal.dateAfter(currentDate, 1);
            reloadData();
            break;
        case MODE_MONTH:
            currentDate = cal.monthAfter(currentDate, 1);
            reloadData();
            break;
        case MODE_YEAR:
            currentDate = cal.yearAfter(currentDate, 1);
            reloadData();
            break;
        }
    }

    private void onPrev() {
        CalendarHelper cal = Contexts.uiInstance().getCalendarHelper();
        switch (mode) {
        case MODE_DAY:
            currentDate = cal.dateBefore(currentDate, 1);
            reloadData();
            break;
        case MODE_MONTH:
            currentDate = cal.monthBefore(currentDate, 1);
            reloadData();
            break;
        case MODE_YEAR:
            currentDate = cal.yearBefore(currentDate, 1);
            reloadData();
            break;
        }
    }

    private void onToday() {
        switch (mode) {
        case MODE_DAY:
        case MODE_MONTH:
        case MODE_YEAR:
            currentDate = targetDate;
            reloadData();
            break;
        }
    }

}
