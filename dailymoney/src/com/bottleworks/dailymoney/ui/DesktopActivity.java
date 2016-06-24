package com.bottleworks.dailymoney.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.TextView;

import com.bottleworks.commons.util.Files;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.BalanceHelper;
import com.bottleworks.dailymoney.data.Book;
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.IMasterDataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author dennis
 */
public class DesktopActivity extends ContextsActivity {

    private static boolean protectionPassed = false;

    private static boolean protectionInfront = false;
    private List<AbstractDesktop> desktops = new ArrayList<>();
    private TextView infoBook;

    private TextView infoWeeklyExpense;
    private TextView infoMonthlyExpense;
    private TextView infoCumulativeCash;
    private View dtLayout;

    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desktop);
        dtLayout = findViewById(R.id.dt_layout);
        initialDesktopItem();
        initialTab();
        initialContent();
        initPasswordProtection();
        loadInfo();
        loadWhatisNew();

        if (getContexts().isFirstTime()) {
            doTheFisrtTime();
        }
        //Dennis: need to confirm the use of AlarmManager in initSchedule for scheduled jobs
//        initSchedule();
    }

//    private void initSchedule() {
//        ScheduleJob backupJob = new ScheduleJob();
//        backupJob.setRepeat(Long.valueOf(1000 * 60 * 60 * 24));
//        Intent intent = new Intent(this, ScheduleReceiver.class);
//        intent.setAction(Constants.BACKUP_JOB);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        am.set(AlarmManager.RTC_WAKEUP, backupJob.getInitDate().getTimeInMillis(), pi);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, backupJob.getInitDate().getTimeInMillis(), backupJob.getRepeat(), pi);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInfo();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int pos = this.tabLayout.getSelectedTabPosition();
            if (pos != 0) {
                TabLayout.Tab tab = this.tabLayout.getTabAt(0);
                if (tab != null) {
                    tab.select();
                    return true;
                }
            }

            protectionPassed = false;
            protectionInfront = false;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }


    private void initPasswordProtection() {
        dtLayout.setVisibility(View.INVISIBLE);
        final String password = getContexts().getPrefPassword();
        if ("".equals(password) || protectionPassed) {
            dtLayout.setVisibility(View.VISIBLE);
            return;
        }
        if (protectionInfront) {
            return;
        }
        Intent intent = new Intent(this, PasswordProtectionActivity.class);
        startActivityForResult(intent, Constants.REQUEST_PASSWORD_PROTECTION_CODE);
        protectionInfront = true;
    }

    private void doTheFisrtTime() {
        if (Contexts.instance().hasSDBackup()) {
            restoreFromSD();
        } else {
            IDataProvider idp = getContexts().getDataProvider();
            if (idp.listAccount(null).size() == 0) {
                //cause of this function is not ready in previous version, so i check the size for old user
                new DataCreator(idp, i18n).createDefaultAccount();
            }
            GUIs.longToast(this, R.string.msg_firsttime_use_hint);
        }
    }

    private void restoreFromSD() {
        // restore db & pref
        final Contexts ctxs = Contexts.instance();
        final GUIs.IBusyRunnable restorejob = new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.longToast(DesktopActivity.this, i18n.string(R.string.msg_db_retored));

                //push a dummy to trigger resume/reload
                Intent intent = new Intent(DesktopActivity.this, DummyActivity.class);
                startActivity(intent);
            }

            @Override
            public void run() {
                try {
                    Files.copyDatabases(ctxs.getSdFolder(), ctxs.getDbFolder(), null);
                    Files.copyPrefFile(ctxs.getSdFolder(), ctxs.getPrefFolder(), null);
                    Contexts.instance().setPreferenceDirty();//since we reload it.
                } catch (IOException e) {
                    Logger.e(e.getMessage(), e);
                }
            }
        };
        GUIs.confirm(this, i18n.string(R.string.qmsg_retore_db), new GUIs.OnFinishListener() {
            @Override
            public boolean onFinish(Object data) {
                if ((Integer) data == GUIs.OK_BUTTON) {
                    GUIs.doBusy(DesktopActivity.this, restorejob);
                } else {
                    IDataProvider idp = getContexts().getDataProvider();
                    if (idp.listAccount(null).size() == 0) {
                        //cause of this function is not ready in previous version, so i check the size for old user
                        new DataCreator(idp, i18n).createDefaultAccount();
                    }
                    GUIs.longToast(DesktopActivity.this, R.string.msg_firsttime_use_hint);
                }
                return true;
            }
        });
    }

    private void initialDesktopItem() {

        AbstractDesktop[] dts = new AbstractDesktop[]{new MainDesktop(), new ReportsDesktop(), new TestsDesktop()};

        for (AbstractDesktop dt : dts) {
            if (dt.isAvailable()) {
                desktops.add(dt);
            }
        }
    }

    private void initialTab() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (Desktop d : desktops) {
            adapter.addDesktop(d);
        }

        viewPager.setAdapter(adapter);
        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(viewPager);

        int count = this.tabLayout.getTabCount();
        if (count <= this.desktops.size()) {
            for (int i = 0; i < count; i++) {
                TabLayout.Tab tab = this.tabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setIcon(this.desktops.get(i).getIcon());
                }
            }
        }
    }

    private void initialContent() {
        infoBook = (TextView) findViewById(R.id.dt_info_book);

        infoWeeklyExpense = (TextView) findViewById(R.id.dt_info_weekly_expense);
        infoMonthlyExpense = (TextView) findViewById(R.id.dt_info_monthly_expense);
        infoCumulativeCash = (TextView) findViewById(R.id.dt_info_cumulative_cash);
    }

    private void loadWhatisNew() {
        if (protectionInfront) return;
        if (getContexts().isFirstVersionTime()) {
            Intent intent = new Intent(this, LocalWebViewDlg.class);
            intent.putExtra(LocalWebViewDlg.INTENT_URI_ID, R.string.path_what_is_new);
            startActivity(intent);
        }
    }

    private void loadInfo() {
        IMasterDataProvider imdp = Contexts.instance().getMasterDataProvider();
        Book book = imdp.findBook(Contexts.instance().getWorkingBookId());
        String symbol = book.getSymbol();
        if (symbol == null || "".equals(symbol)) {
            infoBook.setText(book.getName());
        } else {
            infoBook.setText(String.format("%s ( %s )", book.getName(), symbol));
        }

        infoBook.setVisibility(imdp.listAllBook().size() <= 1 ? TextView.GONE : TextView.VISIBLE);

        Date now = new Date();
        Date start = calHelper.weekStartDate(now);
        Date end = calHelper.weekEndDate(now);
        AccountType type = AccountType.EXPENSE;
        double b = BalanceHelper.calculateBalance(type, start, end).getMoney();
        infoWeeklyExpense.setText(i18n.string(R.string.label_weekly_expense, getContexts().toFormattedMoneyString(b)));

        start = calHelper.monthStartDate(now);
        end = calHelper.monthEndDate(now);
        b = BalanceHelper.calculateBalance(type, start, end).getMoney();
        infoMonthlyExpense.setText(i18n.string(R.string.label_monthly_expense, getContexts().toFormattedMoneyString(b)));


        IDataProvider idp = Contexts.instance().getDataProvider();
        List<Account> acl = idp.listAccount(AccountType.ASSET);
        b = 0;
        for (Account ac : acl) {
            if (ac.isCashAccount()) {
                b += BalanceHelper.calculateBalance(ac, null, calHelper.toDayEnd(now)).getMoney();
            }
        }
        infoCumulativeCash.setText(i18n.string(R.string.label_cumulative_cash, getContexts().toFormattedMoneyString(b)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        List<Pair<AbstractDesktop, DesktopItem>> importants = new ArrayList<>();
        for (AbstractDesktop d : desktops) {
            for (DesktopItem item : d.getItems()) {
                if (item.getImportant() >= 0) {
                    importants.add(new Pair<>(d, item));
                }
            }
        }
        //sort
        Collections.sort(importants, new Comparator<Pair<AbstractDesktop, DesktopItem>>() {
            public int compare(Pair<AbstractDesktop, DesktopItem> item1, Pair<AbstractDesktop, DesktopItem> item2) {
                return Integer.valueOf(item2.second.getImportant()).compareTo(item1.second.getImportant());
            }
        });
        for (Pair<AbstractDesktop, DesktopItem> item : importants) {
            MenuItem mi = menu.add(item.second.getLabel());
            mi.setOnMenuItemClickListener(new DesktopItemClickListener(item.first, item.second));
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_PASSWORD_PROTECTION_CODE) {
            protectionInfront = false;
            if (resultCode != RESULT_OK) {
                finish();
                protectionPassed = false;
            } else {
                protectionPassed = true;
                GUIs.delayPost(new Runnable() {
                    @Override
                    public void run() {
                        dtLayout.setVisibility(View.VISIBLE);
                        loadWhatisNew();
                    }
                });

            }
        }
    }

    public class DesktopItemClickListener implements OnMenuItemClickListener {

        DesktopItem dtitem;
        private AbstractDesktop desktop;

        public DesktopItemClickListener(AbstractDesktop desktop, DesktopItem dtitem) {
            this.desktop = desktop;
            this.dtitem = dtitem;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            this.desktop.onMenuItemClick(this.dtitem);
            return true;
        }

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Desktop> desktopList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return desktopList.get(position);
        }

        @Override
        public int getCount() {
            return desktopList.size();
        }

        public void addDesktop(Desktop desktop) {
            desktopList.add(desktop);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return desktopList.get(position).getLabel();
        }
    }

}
