package com.bottleworks.dailymoney.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * this activity manages the account (of detail) with tab widgets of android,
 * there are 4 type of account, income, expense, asset and liability.
 *
 * @author dennis
 * @see {@link AccountType}
 */
public class AccountMgntActivity extends ContextsActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accmgnt);
        initialTab();
    }

    private void initialTab() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        AccountType[] ata = AccountType.getSupportedType();
        for (AccountType at : ata) {
            adapter.addAccountList(at);
//        Resources r = getResources();
//            tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()),r.getDrawable(at.getDrawable()));
        }

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constants.REQUEST_ACCOUNT_EDITOR_CODE && resultCode == Activity.RESULT_OK) {
//            GUIs.delayPost(new Runnable() {
//                @Override
//                public void run() {
//                    reloadData();
//                }
//            });
//
//        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.accmgnt_optmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.accmgnt_menu_new) {
//            doNewAccount();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void doDeleteAccount(int pos) {
//        Account acc = (Account) listViewData.get(pos);
//        String name = acc.getName();
//
//        getContexts().getDataProvider().deleteAccount(acc.getId());
//        reloadData();
//        GUIs.shortToast(this, i18n.string(R.string.msg_account_deleted, name));
//
//    }


//    private void doCopyAccount(int pos) {
//        Account acc = (Account) listViewData.get(pos);
//        Intent intent = null;
//        intent = new Intent(this, AccountEditorActivity.class);
//        intent.putExtra(AccountEditorActivity.INTENT_MODE_CREATE, true);
//        intent.putExtra(AccountEditorActivity.INTENT_ACCOUNT, acc);
//        startActivityForResult(intent, Constants.REQUEST_ACCOUNT_EDITOR_CODE);
//    }

//    private void doNewAccount() {
//        Account acc = new Account(currTab, "", 0D);
//        Intent intent = null;
//        intent = new Intent(this, AccountEditorActivity.class);
//        intent.putExtra(AccountEditorActivity.INTENT_MODE_CREATE, true);
//        intent.putExtra(AccountEditorActivity.INTENT_ACCOUNT, acc);
//        startActivityForResult(intent, Constants.REQUEST_ACCOUNT_EDITOR_CODE);
//    }

    private class ViewPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        List<AccountList> accounts = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return this.accounts.get(position);
        }

        @Override
        public int getCount() {
            return this.accounts.size();
        }

        public void addAccountList(AccountType at) {
            this.accounts.add(AccountList.newInstance(at));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.accounts.get(position).getLabel(AccountMgntActivity.this.i18n);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // do nothing intentionally
        }

        @Override
        public void onPageSelected(int position) {
            this.accounts.get(position).reloadData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // do nothing intentionally
        }
    }
}