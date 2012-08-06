package com.bottleworks.dailymoney.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.context.ScheduleReceiver;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.BalanceHelper;
import com.bottleworks.dailymoney.data.Book;
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.IMasterDataProvider;
import com.bottleworks.dailymoney.data.ScheduleJob;
/**
 * 
 * @author dennis
 *
 */
public class DesktopActivity extends ContextsActivity implements OnTabChangeListener, OnItemClickListener {
    
    private String currTab = null;

    private GridView gridView;

    private DesktopItemAdapter gridViewAdapter;

    private List<Desktop> desktops = new ArrayList<Desktop>();
    
    private String appinfo;
    
    private DesktopItem lastClickedItem;
    
    private static boolean protectionPassed = false;
    private static boolean protectionInfront = false;
    
    
    private TextView infoBook;
    
    private TextView infoWeeklyExpense;
    private TextView infoMonthlyExpense;
    private TextView infoCumulativeCash;
    private TabHost tabs;
    private View dtLayout;
    
    private HashMap<Object, DesktopItem> dtHashMap = new HashMap<Object, DesktopItem>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desktop);
        dtLayout = findViewById(R.id.dt_layout);
        initialApplicationInfo();
        initialDesktopItem();
        initialTab();
        initialContent();
        initPasswordProtection();
        loadDesktop();
        loadInfo();
        loadWhatisNew();
        initSchedule();
    }
    
    private void initSchedule() {
        ScheduleJob backupJob = new ScheduleJob();
        backupJob.setRepeat(Long.valueOf(1000 * 60 * 60 * 24));
        Intent intent = new Intent(this, ScheduleReceiver.class);
        intent.setAction(Constants.BACKUP_JOB);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, backupJob.getInitDate().getTimeInMillis(), pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, backupJob.getInitDate().getTimeInMillis(), backupJob.getRepeat(), pi);
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        loadInfo();
        
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!desktops.get(0).getLabel().equals(currTab)){
                tabs.setCurrentTab(0);
                return true;
            }
            protectionPassed = false;
            protectionInfront = false;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }


    private void initPasswordProtection() { 
        dtLayout.setVisibility(View.INVISIBLE);
        final String password = getContexts().getPrefPassword();
        if("".equals(password)||protectionPassed){
            dtLayout.setVisibility(View.VISIBLE);
            return;
        }
        if(protectionInfront){
            return;
        }
        Intent intent = null;
        intent = new Intent(this,PasswordProtectionActivity.class);
        startActivityForResult(intent,Constants.REQUEST_PASSWORD_PROTECTION_CODE);
        protectionInfront = true;
    }

    private void initialApplicationInfo() {
        appinfo = i18n.string(R.string.app_name);
        String ver = getContexts().getApplicationVersionName();
        appinfo += " ver : "+ver;
        
        if(getContexts().isFirstTime()){
            IDataProvider idp = getContexts().getDataProvider();
            if(idp.listAccount(null).size()==0){
                //cause of this function is not ready in previous version, so i check the size for old user
                new DataCreator(idp,i18n).createDefaultAccount();
            }
            GUIs.longToast(this,R.string.msg_firsttime_use_hint);
        }
        
    }


    private void initialDesktopItem() {
        
        Desktop[] dts = new Desktop[]{new MainDesktop(this),new ReportsDesktop(this),new TestsDesktop(this)};
        
        for(Desktop dt:dts){
            if(dt.isAvailable()){
                desktops.add(dt);
            }
        }
    }

    private void initialTab() {
        tabs = (TabHost) findViewById(R.id.dt_tabs);
        tabs.setup();

        
        for(Desktop d:desktops){
            TabSpec tab = tabs.newTabSpec(d.getLabel());
            tab.setIndicator(d.getLabel(),getResources().getDrawable(d.getIcon()));
            tab.setContent(R.id.dt_grid);
            tabs.addTab(tab);
            if(currTab==null){
                currTab = tab.getTag();
            }
        }

        if(desktops.size()>1){
            // workaround, force refresh
            tabs.setCurrentTab(1);
            tabs.setCurrentTab(0);
        }

        tabs.setOnTabChangedListener(this);

    }

    private void initialContent() {
        
        infoBook = (TextView)findViewById(R.id.dt_info_book);
        
        infoWeeklyExpense = (TextView)findViewById(R.id.dt_info_weekly_expense);
        infoMonthlyExpense = (TextView)findViewById(R.id.dt_info_monthly_expense);
        infoCumulativeCash = (TextView)findViewById(R.id.dt_info_cumulative_cash);
        
        
        
        gridViewAdapter = new DesktopItemAdapter();
        gridView = (GridView) findViewById(R.id.dt_grid);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(this);
        // registerForContextMenu(gridView);

    }
    
    private void loadDesktop() {
        for(Desktop d:desktops){
            if(d.getLabel().equals(currTab)){
                d.refresh();
                break;
            }
        }
        gridViewAdapter.notifyDataSetChanged();
    }
    
    private void loadWhatisNew(){
        if(protectionInfront) return;
        if(getContexts().isFirstVersionTime()){
            Intent intent = new Intent(this, LocalWebViewDlg.class);
            intent.putExtra(LocalWebViewDlg.INTENT_URI_ID, R.string.path_what_is_new);
            startActivity(intent);
        }
    }
    
    private void loadInfo(){
        
        
        IMasterDataProvider imdp = Contexts.instance().getMasterDataProvider();
        Book book = imdp.findBook(Contexts.instance().getWorkingBookId());
        String symbol = book.getSymbol();
        if(symbol==null || "".equals(symbol)){
            infoBook.setText(book.getName());
        }else{
            infoBook.setText(book.getName()+" ( "+symbol+" )");
        }
        
        infoBook.setVisibility(imdp.listAllBook().size()<=1?TextView.GONE:TextView.VISIBLE);
        
        Date now = new Date();
        Date start = calHelper.weekStartDate(now);
        Date end = calHelper.weekEndDate(now);
        AccountType type = AccountType.EXPENSE;
        BigDecimal b = BalanceHelper.calculateBalance(type, start, end).getMoney();
        infoWeeklyExpense.setText(i18n.string(R.string.label_weekly_expense,getContexts().toFormattedMoneyString(b)));
        
        start = calHelper.monthStartDate(now);
        end = calHelper.monthEndDate(now);
        b = BalanceHelper.calculateBalance(type, start, end).getMoney();
        infoMonthlyExpense.setText(i18n.string(R.string.label_monthly_expense,getContexts().toFormattedMoneyString(b)));
        
        
        
        IDataProvider idp = Contexts.instance().getDataProvider();
        List<Account> acl =idp.listAccount(AccountType.ASSET);
        b = BigDecimal.ZERO;
        for(Account ac:acl){
            if(ac.isCashAccount()){
                b = b.add(BalanceHelper.calculateBalance(ac, null, calHelper.toDayEnd(now)).getMoney());
            }
        }
        infoCumulativeCash.setText(i18n.string(R.string.label_cumulative_cash,getContexts().toFormattedMoneyString(b)));
    }

    @Override
    public void onTabChanged(String tabId) {
        currTab = tabId;
        loadDesktop();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.accmgnt_optmenu, menu);
        super.onCreateOptionsMenu(menu);
        List<DesktopItem> importants = new ArrayList<DesktopItem>();
        for(Desktop d:desktops){
            for (DesktopItem item : d.getItems()) {
                if(item.getImportant()>=0){
                    importants.add(item);
                }
            }
        }
        //sort
        Collections.sort(importants, new Comparator<DesktopItem>() {
            public int compare(DesktopItem item1, DesktopItem item2) {
                return Integer.valueOf(item2.getImportant()).compareTo(Integer.valueOf(item1.getImportant()));
            }
        });
        for(DesktopItem item:importants){
            MenuItem mi = menu.add(item.getLabel());
            mi.setOnMenuItemClickListener(new DesktopItemClickListener(item));
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch (item.getItemId()) {
        // case R.id.accmgnt_menu_new:
        // doNewAccount();
        // return true;
        // }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        
        //item clicked in grid view
         if(parent == gridView){
             DesktopItem di = dtHashMap.get(view);
             if(di!=null){
                 lastClickedItem = di;
                 lastClickedItem.run();
             }
         }
    }
    
    
    @SuppressWarnings("unchecked")
    List<DesktopItem> getCurrentVisibleDesktopItems(){
        for(Desktop d:desktops){
            if(d.getLabel().equals(currTab)){
                return d.getVisibleItems();
            }
        }
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode,
            Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == Constants.REQUEST_PASSWORD_PROTECTION_CODE){
            protectionInfront = false;
            if(resultCode!=RESULT_OK){
                finish();
                protectionPassed = false;
            }else{
                protectionPassed = true;
                GUIs.delayPost(new Runnable(){
                    @Override
                    public void run() {
                        dtLayout.setVisibility(View.VISIBLE);
                        loadWhatisNew();
                    }});
               
            }
        }else{
            if(lastClickedItem!=null){
                lastClickedItem.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public class DesktopItemClickListener implements OnMenuItemClickListener{

        DesktopItem dtitem;
        
        public DesktopItemClickListener(DesktopItem dtitem){
            this.dtitem = dtitem;
        }
        
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            lastClickedItem = dtitem;
            lastClickedItem.run();
            return true;
        }
        
    }
    
    public class DesktopItemAdapter extends BaseAdapter {

        public int getCount() {
            return getCurrentVisibleDesktopItems().size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;
            TextView tv;
            LinearLayout view;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                
                view = new LinearLayout(DesktopActivity.this);
                view.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
                GUIs.inflateView(DesktopActivity.this, view, R.layout.desktop_item);
            } else {
                view = (LinearLayout)convertView;
            }

            iv = (ImageView)view.findViewById(R.id.dt_icon);
            tv = (TextView)view.findViewById(R.id.dt_label);
            
            DesktopItem item = getCurrentVisibleDesktopItems().get(position);
            iv.setImageResource(item.getIcon());
            tv.setText(item.getLabel());
            dtHashMap.put(view, item);
            return view;
        }

    }
    


}
