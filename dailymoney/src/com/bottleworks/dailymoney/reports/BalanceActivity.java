package com.bottleworks.dailymoney.reports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.AccountDetailListActivity;
import com.bottleworks.dailymoney.Constants;
import com.bottleworks.dailymoney.R;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.NamedItem;

/**
 * 
 * @author dennis
 * 
 */
public class BalanceActivity extends ContextsActivity implements OnClickListener, OnItemClickListener {

    public static final int MODE_MONTH = 0;
    public static final int MODE_YEAR = 1;

    public static final String INTENT_BALANCE_DATE = "balanceDate";
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_TARGET_DATE = "target";
    public static final String INTENT_TOTAL_MODE = "modeTotal";

    TextView infoView;
    View toolbarView;

    private Date targetDate;
    private Date currentDate;
    private int mode = MODE_MONTH;
    private boolean totalMode = false;

    private DateFormat monthDateFormat;
    private DateFormat yearDateFormat;
    
    private Date currentStartDate;
    private Date currentEndDate;

    ImageButton modeBtn;
    
    private static String[] bindingFrom = new String[] { "layout","name", "money"};
    
    private static int[] bindingTo = new int[] { R.id.report_balance_layout, R.id.report_balance_item_name, R.id.report_balance_item_money};
    
    private List<Balance> listViewData = new ArrayList<Balance>();
    
    private List<Map<String, Object>> listViewMapList = new ArrayList<Map<String, Object>>();

    private ListView listView;
    
    private SimpleAdapter listViewAdapter;

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
        mode = b.getInt(INTENT_MODE, MODE_MONTH);
        totalMode = b.getBoolean(INTENT_TOTAL_MODE, true);
        Object o = b.get(INTENT_BALANCE_DATE);
        if (o instanceof Date) {
            targetDate = (Date) o;
        } else {
            targetDate = new Date();
        }
        currentDate = targetDate;
    }

    private void initialContent() {

        monthDateFormat = new SimpleDateFormat("yyyy/MM");
        yearDateFormat = new SimpleDateFormat("yyyy");

        infoView = (TextView) findViewById(R.id.report_balance_infobar);
        toolbarView = findViewById(R.id.report_balance_toolbar);

        findViewById(R.id.report_balance_prev).setOnClickListener(this);
        findViewById(R.id.report_balance_next).setOnClickListener(this);
        findViewById(R.id.report_balance_today).setOnClickListener(this);
        modeBtn = (ImageButton) findViewById(R.id.report_balance_mode);
        modeBtn.setOnClickListener(this);
        reloadToolbar();
        
        
        
        
        listViewAdapter = new SimpleAdapter(this, listViewMapList, R.layout.report_balance_item, bindingFrom, bindingTo);
        listViewAdapter.setViewBinder(new SimpleAdapter.ViewBinder(){

            @Override
            public boolean setViewValue(View view, Object data, String text) {
                NamedItem item = (NamedItem)data;
                String name = item.getName();
                Balance b = (Balance)item.getValue();
                
                
                if("layout".equals(name)){
                    LinearLayout layout = (LinearLayout)view;
                    adjustLayout(layout,b);
                    return true;
                }
                
                //not textview, not initval
                if(!(view instanceof TextView)){
                    return false;
                }
                AccountType at = AccountType.find(b.getType());
                TextView tv = (TextView)view;
                
                if(at==AccountType.INCOME){
                    if(b.indent==0){
                        tv.setTextColor(getResources().getColor(R.color.income_fgl));
                    }else{
                        tv.setTextColor(getResources().getColor(R.color.income_fgd));
                    }
                }else if(at==AccountType.EXPENSE){
                    if(b.indent==0){
                        tv.setTextColor(getResources().getColor(R.color.expense_fgl));
                    }else{
                        tv.setTextColor(getResources().getColor(R.color.expense_fgd));
                    }
                }else if(at==AccountType.ASSET){
                    if(b.indent==0){
                        tv.setTextColor(getResources().getColor(R.color.asset_fgl));
                    }else{
                        tv.setTextColor(getResources().getColor(R.color.asset_fgd));
                    }
                }else if(at==AccountType.LIABILITY){
                    if(b.indent==0){
                        tv.setTextColor(getResources().getColor(R.color.liability_fgl));
                    }else{
                        tv.setTextColor(getResources().getColor(R.color.liability_fgd));
                    }
                }else if(at==AccountType.OTHER){
                    if(b.indent==0){
                        tv.setTextColor(getResources().getColor(R.color.other_fgl));
                    }else{
                        tv.setTextColor(getResources().getColor(R.color.other_fgd));
                    }
                }else{
                    if(b.indent==0){
                        tv.setTextColor(getResources().getColor(R.color.unknow_fgl));
                    }else{
                        tv.setTextColor(getResources().getColor(R.color.unknow_fgd));
                    }
                }
                adjustItem(tv,b,GUIs.getDPRatio(BalanceActivity.this));
                return false;
            }});
        
        listView = (ListView) findViewById(R.id.report_balance_list);
        listView.setAdapter(listViewAdapter);
        
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
    }

    protected void adjustLayout(LinearLayout layout, Balance b) {
        switch(b.indent){
        case 0:
            layout.setBackgroundDrawable((getResources().getDrawable(R.drawable.selector_balance_indent0)));
            break;
        case 1:
            layout.setBackgroundDrawable((getResources().getDrawable(R.drawable.selector_balance_indent)));
            break;
        default:
            layout.setBackgroundDrawable((getResources().getDrawable(R.drawable.selector_balance_indent)));
            break;    
        }
    }
    
    protected void adjustItem(TextView tv, Balance b,float dp) {
        float fontPixelSize = 18;
        float ratio = 0;
        int marginLeft = 0;
        int marginRight = 5;
        int paddingTB = 0;
        
        switch(b.indent){
        case 0:
            ratio = 1F;
            paddingTB = 5;
            marginLeft = 5;
            break;
        case 1:
            ratio = 0.85F;
            paddingTB = 3;
            marginLeft = 15;
            break;
        default:
            ratio = 0.75F;
            paddingTB = 1;
            marginLeft = 25;
            break;    
        }
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontPixelSize*ratio);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)tv.getLayoutParams();
        lp.setMargins((int)(marginLeft*dp), lp.topMargin, (int)(marginRight*dp), lp.bottomMargin);
        tv.setPadding(tv.getPaddingLeft(), (int)(paddingTB*dp), tv.getPaddingRight(),  (int)(paddingTB*dp));
    }


    private void reloadToolbar() {
        switch (mode) {
        case MODE_YEAR:
            modeBtn.setImageResource(R.drawable.btn_year);
            break;
        default:
            modeBtn.setImageResource(R.drawable.btn_month);
            break;
        }
    }

    private List<Balance> adjustTotalBalance(AccountType type, String totalName, List<Balance> items) {
        if(items.size()==0){
            return items;
        }
        List<Balance> group = new ArrayList<Balance>(items);
        double total = 0;
        for (Balance b : items) {
            b.setIndent(1);
            b.setGroup(group);
            total += b.money;
        }
        Balance bt = new Balance(totalName,type.getType(), total,type);
        bt.setIndent(0);
        bt.setGroup(group);
        bt.setDate(items.get(0).getDate());
        items.add(0, bt);
        return items;
    }

    private List<Balance> calBalance(AccountType type,Date start,Date end,boolean calInit) {
        boolean nat = type==AccountType.INCOME||type==AccountType.LIABILITY;
        IDataProvider idp = Contexts.uiInstance().getDataProvider();
        List<Account> accs = idp.listAccount(type);
        List<Balance> blist = new ArrayList<Balance>();
        for (Account acc : accs) {
            double from = idp.sumFrom(acc, start, end);
            double to = idp.sumTo(acc, start, end);
            double init = calInit?acc.getInitialValue():0;
            double b = init + (nat?(from - to):(to - from));
            Balance balance = new Balance(acc.getName(),type.getType(), b,acc);
            balance.setDate(end);
            blist.add(balance);
        }
        return blist;
    }
    
    private Balance calBalance(Account acc, Date start, Date end, boolean calInit) {
        AccountType type = AccountType.find(acc.getType());
        boolean nat = type == AccountType.INCOME || type == AccountType.LIABILITY;
        IDataProvider idp = Contexts.uiInstance().getDataProvider();

        double from = idp.sumFrom(acc, start, end);
        double to = idp.sumTo(acc, start, end);
        double init = calInit ? acc.getInitialValue() : 0;
        double b = init + (nat ? (from - to) : (to - from));
        Balance balance = new Balance(acc.getName(), type.getType(), b, acc);
        balance.setDate(end);

        return balance;
    }

    private void reloadData() {
        final CalendarHelper cal = Contexts.uiInstance().getCalendarHelper();
        currentEndDate = null;
        currentStartDate = null;
        infoView.setText("");
        reloadToolbar();
        switch (mode) {
        case MODE_YEAR:
            currentEndDate = cal.yearEndDate(currentDate);
            currentStartDate = totalMode?null:cal.yearStartDate(currentDate);
            break;
        default:
            currentEndDate = cal.monthEndDate(currentDate);
            currentStartDate = totalMode?null:cal.monthStartDate(currentDate);
            break;
        }
        GUIs.doBusy(this, new GUIs.BusyAdapter() {
            List<Balance> all = new ArrayList<Balance>();

            @Override
            public void run() {
                List<Balance> asset = calBalance(AccountType.ASSET, currentStartDate, currentEndDate,totalMode);
                asset = adjustTotalBalance(AccountType.ASSET, totalMode ? i18n.string(R.string.label_balance_tasset)
                        : i18n.string(R.string.label_asset), asset);

                List<Balance> income = calBalance(AccountType.INCOME, currentStartDate, currentEndDate,totalMode);
                income = adjustTotalBalance(AccountType.INCOME, totalMode ? i18n.string(R.string.label_balance_tincome)
                        : i18n.string(R.string.label_income), income);

                List<Balance> expense = calBalance(AccountType.EXPENSE, currentStartDate, currentEndDate,totalMode);
                expense = adjustTotalBalance(
                        AccountType.EXPENSE,
                        totalMode ? i18n.string(R.string.label_balance_texpense) : i18n
                                .string(R.string.label_expense), expense);

                List<Balance> liability = calBalance(AccountType.LIABILITY, currentStartDate, currentEndDate,totalMode);
                liability = adjustTotalBalance(
                        AccountType.LIABILITY,
                        totalMode ? i18n.string(R.string.label_balance_tliability) : i18n
                                .string(R.string.label_liability), liability);

                List<Balance> other = calBalance(AccountType.OTHER, currentStartDate, currentEndDate,totalMode);
                other = adjustTotalBalance(AccountType.OTHER, totalMode ? i18n.string(R.string.label_balance_tother)
                        : i18n.string(R.string.label_other), other);

                if(totalMode){
                    all.addAll(asset);
                    all.addAll(liability);
                    all.addAll(income);
                    all.addAll(expense);
                    all.addAll(other);
                }else{
                    all.addAll(income);
                    all.addAll(expense);
                    all.addAll(asset);
                    all.addAll(liability);
                    all.addAll(other);
                }
            }

            @Override
            public void onBusyFinish() {
                listViewData.clear();
                listViewData.addAll(all);
                listViewMapList.clear();

                for (Balance b : listViewData) {
                    Map<String, Object> row = new HashMap<String, Object>();
                    listViewMapList.add(row);
                    String money = i18n.string(R.string.label_item_money,Formats.money2String(b.getMoney()));
                    row.put(bindingFrom[0], new NamedItem(bindingFrom[0],b,""));//layout
                    row.put(bindingFrom[1], new NamedItem(bindingFrom[1],b,b.getName()));
                    row.put(bindingFrom[2], new NamedItem(bindingFrom[2],b,money));
                }

                listViewAdapter.notifyDataSetChanged();
                
                
                // update info
                if(totalMode){
                    if(mode==MODE_MONTH){
                        infoView.setText(i18n.string(R.string.label_balance_mode_month_total, monthDateFormat.format(currentDate)));
                    }else if(mode==MODE_YEAR){
                        infoView.setText(i18n.string(R.string.label_balance_mode_year_total, yearDateFormat.format(currentDate)));
                    }
                }else{
                    if(mode==MODE_MONTH){
                        infoView.setText(i18n.string(R.string.label_balance_mode_month,monthDateFormat.format(currentDate)));
                    }else if(mode==MODE_YEAR){
                        infoView.setText(i18n.string(R.string.label_balance_mode_year,yearDateFormat.format(currentDate)));
                    }
                }
                

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
        case MODE_MONTH:
            mode = MODE_YEAR;
            reloadData();
            break;
        case MODE_YEAR:
            mode = MODE_MONTH;
            reloadData();
            break;
        }
    }

    private void onNext() {
        CalendarHelper cal = Contexts.uiInstance().getCalendarHelper();
        switch (mode) {
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
        case MODE_MONTH:
        case MODE_YEAR:
            currentDate = targetDate;
            reloadData();
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent == listView){
            doBalancePieChart(position);
        }
    }
    
    private void doDetailList(int position) {
        Balance b = listViewData.get(position);
        
        Intent intent = null;
        intent = new Intent(this,AccountDetailListActivity.class);
        if(currentStartDate !=null){
            intent.putExtra(AccountDetailListActivity.INTENT_START,currentStartDate);
        }
        if(currentEndDate !=null){
            intent.putExtra(AccountDetailListActivity.INTENT_END,currentEndDate);
        }            
        intent.putExtra(AccountDetailListActivity.INTENT_TARGET,b.getTarget());
        intent.putExtra(AccountDetailListActivity.INTENT_TARGET_INFO,b.getName());
        this.startActivityForResult(intent,Constants.REQUEST_ACCOUNT_DETAIL_LIST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_ACCOUNT_DETAIL_LIST_CODE && resultCode==Activity.RESULT_OK){
            GUIs.delayPost(new Runnable(){
                @Override
                public void run() {
                    reloadData();
                }});
        }
    }
    
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.report_balance_list) {
//            AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;            
            getMenuInflater().inflate(R.menu.balance_ctxmenu, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.balance_menu_piechart:
            doBalancePieChart(info.position);
            return true;
        case R.id.balance_menu_account_yeartimechart:
            doBalanceYearTimeChart(info.position);
            return true;
        case R.id.balance_menu_detlist:
            doDetailList(info.position);
            return true;
        }
        return super.onContextItemSelected(item);
    }
    
    private void doBalancePieChart(int pos){
        Balance b = listViewData.get(pos);
        List<Balance> group = b.getGroup();
        AccountType at;
        if(b.target instanceof AccountType){
            at = (AccountType)b.target;
        }else{
            //move selection to first
            group = new ArrayList<Balance>(group);
            group.remove(b);
            group.add(0,b);
            at = AccountType.find(((Account)b.target).getType());
        }
        Intent intent = new BalancePieChart(this,GUIs.getOrientation(this),GUIs.getDPRatio(this)).createIntent(at,group);
        startActivity(intent);
    }
    
    private void doBalanceYearTimeChart(int pos){
        Balance b = listViewData.get(pos);
        AccountType at;
        List<Balance> group = b.getGroup();
        if(b.target instanceof AccountType){
            at = (AccountType)b.target;
        }else{
            group = new ArrayList<Balance>(group);
            group.remove(b);
            group.add(0,b);
            at = AccountType.find(((Account)b.target).getType());
        }
        
        List<List<Balance>> yearTimebalance = new ArrayList<List<Balance>>();
        
        
        for(Balance g:group){
            if(!(g.target instanceof Account)){
                continue;
            }
            Account acc = (Account)g.target;
            List<Balance> blist = new ArrayList<Balance>();
            yearTimebalance.add(blist);
            Date d = calHelper.yearStartDate(g.getDate());
            for(int i=0;i<12;i++){
                Balance balance = calBalance(acc, calHelper.monthStartDate(d),calHelper.monthEndDate(d), false);
                blist.add(balance);
                d = calHelper.monthAfter(d,1);
            }
        }
        
        Intent intent = new BalanceYearTimeChart(this,GUIs.getOrientation(this),GUIs.getDPRatio(this)).createIntent(
                i18n.string(R.string.label_balance_account_yeartimechart,at.getDisplay(i18n),yearDateFormat.format(currentDate)),yearTimebalance);
        startActivity(intent);
    }

}
