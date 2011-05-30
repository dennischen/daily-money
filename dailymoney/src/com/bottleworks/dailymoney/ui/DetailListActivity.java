package com.bottleworks.dailymoney.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;

/**
 * 
 * @author dennis
 * 
 */
public class DetailListActivity extends ContextsActivity implements OnClickListener {
    
    public static final int MODE_DAY = 0;
    public static final int MODE_WEEK = 1;
    public static final int MODE_MONTH = 2;
    public static final int MODE_YEAR = 3;
    public static final int MODE_ALL = 4;
    
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_TARGET_DATE = "target";
    public static final String INTENT_ALLOW_SWITCH_YEAR = "switchyear";
    
    DetailListHelper detailListHelper;
    
    TextView infoView;
    TextView sumIncomeView;
    TextView sumExpenseView;
    TextView sumAssetView;
    TextView sumLiabilityView;
    TextView sumOtherView;
    TextView sumUnknowView;
    
    View toolbarView;
    
    private Date targetDate;
    private Date currentDate;
    private int mode = MODE_WEEK;
    private boolean allowYearSwitch = true;
    
    private DateFormat dayDateFormat;
    private DateFormat weekDateFormat;
    private DateFormat monthDateFormat;
    private DateFormat yearDateFormat;
    
    ImageButton modeBtn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detlist);
        initialIntent();
        initialContent();
        GUIs.delayPost(new Runnable() {
            @Override
            public void run() {
                reloadData();
            }
        },25);
    }
    

    private void initialIntent() {
        Bundle b = getIntentExtras(); 
        mode = b.getInt(INTENT_MODE,MODE_WEEK);
        Object o = b.get(INTENT_TARGET_DATE);
        if(o instanceof Date){
            targetDate = (Date)o;
        }else{
            targetDate = new Date();
        }
        currentDate = targetDate;
        allowYearSwitch = b.getBoolean(INTENT_ALLOW_SWITCH_YEAR,false);
    }


    private void initialContent() {
        dayDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        weekDateFormat = new SimpleDateFormat("MM/dd");
        monthDateFormat = new SimpleDateFormat("yyyy/MM - MMM");
        yearDateFormat = new SimpleDateFormat("yyyy");
        
        detailListHelper = new DetailListHelper(this, i18n,calHelper,true, new DetailListHelper.OnDetailListener() {
            @Override
            public void onDetailDeleted(Detail detail) {
                GUIs.shortToast(DetailListActivity.this, i18n.string(R.string.msg_detail_deleted));
                reloadData();
            }
        });
        
        
        infoView = (TextView)findViewById(R.id.detlist_infobar);
        toolbarView = findViewById(R.id.detlist_toolbar);
        sumIncomeView = (TextView)findViewById(R.id.detlist_sum_income);
        sumExpenseView = (TextView)findViewById(R.id.detlist_sum_expense);
        sumAssetView = (TextView)findViewById(R.id.detlist_sum_asset);
        sumLiabilityView = (TextView)findViewById(R.id.detlist_sum_liability);
        sumOtherView = (TextView)findViewById(R.id.detlist_sum_other);
        sumUnknowView = (TextView)findViewById(R.id.detlist_sum_unknow);
        
        
        findViewById(R.id.detlist_prev).setOnClickListener(this);
        findViewById(R.id.detlist_next).setOnClickListener(this);
        findViewById(R.id.detlist_today).setOnClickListener(this);
        modeBtn = (ImageButton)findViewById(R.id.detlist_mode);
        modeBtn.setOnClickListener(this);
        
        ListView listView = (ListView)findViewById(R.id.detlist_list);
        detailListHelper.setup(listView);
        registerForContextMenu(listView);
        
        reloadToolbar();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_DETAIL_EDITOR_CODE && resultCode==Activity.RESULT_OK){
            GUIs.delayPost(new Runnable(){
                @Override
                public void run() {
                    reloadData();
                }});
            
        }
    }
    
    private void reloadToolbar(){
        switch(mode){
        case MODE_ALL:
            toolbarView.setVisibility(TextView.GONE);
            break;
        case MODE_MONTH:
            toolbarView.setVisibility(TextView.VISIBLE);
            modeBtn.setVisibility(ImageButton.VISIBLE);
            if(allowYearSwitch){
                modeBtn.setImageResource(R.drawable.btn_year);
            }else{
                modeBtn.setImageResource(R.drawable.btn_day);
            }
            break;
        case MODE_DAY:
            toolbarView.setVisibility(TextView.VISIBLE);
            modeBtn.setVisibility(ImageButton.VISIBLE);
            modeBtn.setImageResource(R.drawable.btn_week);
            break;    
        case MODE_YEAR:
            toolbarView.setVisibility(TextView.VISIBLE);
            if(allowYearSwitch){
                modeBtn.setVisibility(ImageButton.VISIBLE);
                modeBtn.setImageResource(R.drawable.btn_week);
            }else{
                modeBtn.setVisibility(ImageButton.GONE);
            }
            break;
        default:
            toolbarView.setVisibility(TextView.VISIBLE);
            modeBtn.setVisibility(ImageButton.VISIBLE);
            modeBtn.setImageResource(R.drawable.btn_month);
            break;
        }
    }
    

    private void reloadData() {
        final CalendarHelper cal = getContexts().getCalendarHelper();
        final Date start;
        final Date end;
        infoView.setText("");
        reloadToolbar();
        sumIncomeView.setVisibility(TextView.GONE);
        sumExpenseView.setVisibility(TextView.GONE);
        sumAssetView.setVisibility(TextView.GONE);
        sumLiabilityView.setVisibility(TextView.GONE);
        sumOtherView.setVisibility(TextView.GONE);
        
        sumUnknowView.setVisibility(TextView.VISIBLE);
        
        
        switch(mode){
        case MODE_ALL:
            start = end = null;
//            toolbarView.setVisibility(TextView.GONE);
            break;
        case MODE_MONTH:
            start = cal.monthStartDate(currentDate);
            end = cal.monthEndDate(currentDate);
//            toolbarView.setVisibility(TextView.VISIBLE);
//            
//            modeBtn.setVisibility(ImageButton.VISIBLE);
//            if(allowYearSwitch){
//                modeBtn.setImageResource(R.drawable.btn_year);
//            }else{
//                modeBtn.setImageResource(R.drawable.btn_week);
//            }
            break;
        case MODE_DAY:
            start = cal.toDayStart(currentDate);
            end = cal.toDayEnd(currentDate);
            break;
        case MODE_YEAR:
            start = cal.yearStartDate(currentDate);
            end = cal.yearEndDate(currentDate);
//            toolbarView.setVisibility(TextView.VISIBLE);
//
//            if(allowYearSwitch){
//                modeBtn.setVisibility(ImageButton.VISIBLE);
//                modeBtn.setImageResource(R.drawable.btn_week);
//            }else{
//                modeBtn.setVisibility(ImageButton.GONE);
//            }
            
            break;
        default:
            start = cal.weekStartDate(currentDate);
            end = cal.weekEndDate(currentDate);
//            toolbarView.setVisibility(TextView.VISIBLE);
//            modeBtn.setVisibility(ImageButton.VISIBLE);
//            modeBtn.setImageResource(R.drawable.btn_month);
            break;
        }
        final IDataProvider idp = getContexts().getDataProvider();
//        detailListHelper.reloadData(idp.listAllDetail());
        GUIs.doBusy(this,new GUIs.BusyAdapter() {
            List<Detail> data = null;
            
            double expense;
            double income;
            double asset;
            double liability;
            double other;
            int count;
            
            @Override
            public void run() {
                data = idp.listDetail(start,end,getContexts().getPrefMaxRecords());
                count = idp.countDetail(start, end);
                income = idp.sumFrom(AccountType.INCOME,start,end);
                expense = idp.sumTo(AccountType.EXPENSE,start,end);//nagivate
                asset = idp.sumTo(AccountType.ASSET,start,end) - idp.sumFrom(AccountType.ASSET,start,end);
                liability = idp.sumTo(AccountType.LIABILITY,start,end) - idp.sumFrom(AccountType.LIABILITY,start,end);
                liability = -liability;
                other = idp.sumTo(AccountType.OTHER,start,end) - idp.sumFrom(AccountType.OTHER,start,end);
            }
            @Override
            public void onBusyFinish() {
                final CalendarHelper cal = getContexts().getCalendarHelper();
                sumUnknowView.setVisibility(TextView.GONE);
              //update data
                detailListHelper.reloadData(data);
                int showcount = 0;
                if(income!=0){
                    sumIncomeView.setText(i18n.string(R.string.label_detlist_sum_income,getContexts().toFormattedMoneyString((income))));
                    sumIncomeView.setVisibility(TextView.VISIBLE);
                    showcount++;
                }
                if(expense!=0){
                    sumExpenseView.setText(i18n.string(R.string.label_detlist_sum_expense,getContexts().toFormattedMoneyString((expense))));
                    sumExpenseView.setVisibility(TextView.VISIBLE);
                    showcount++;
                }
                if(asset!=0){
                    sumAssetView.setText(i18n.string(R.string.label_detlist_sum_asset,getContexts().toFormattedMoneyString((asset))));
                    sumAssetView.setVisibility(TextView.VISIBLE);
                    showcount++;
                }
                if(liability!=0){
                    sumLiabilityView.setText(i18n.string(R.string.label_detlist_sum_liability,getContexts().toFormattedMoneyString((liability))));
                    sumLiabilityView.setVisibility(TextView.VISIBLE);
                    showcount++;
                }
                if(other!=0){
                    sumOtherView.setText(i18n.string(R.string.label_detlist_sum_other,getContexts().toFormattedMoneyString((other))));
                    sumOtherView.setVisibility(TextView.VISIBLE);
                    showcount++;
                }
                
                adjustTextSize(sumIncomeView,showcount);
                adjustTextSize(sumExpenseView,showcount);
                adjustTextSize(sumAssetView,showcount);
                adjustTextSize(sumLiabilityView,showcount);
                adjustTextSize(sumOtherView,showcount);
                
              //update info
                switch(mode){
                case MODE_ALL:
                    infoView.setText(i18n.string(R.string.label_all_details,Integer.toString(count)));
                    break;
                case MODE_MONTH:
                    infoView.setText(i18n.string(R.string.label_month_details,monthDateFormat.format(cal.monthStartDate(currentDate)),Integer.toString(count)));
                    break;
                case MODE_DAY:
                    infoView.setText(i18n.string(R.string.label_day_details,dayDateFormat.format(currentDate),Integer.toString(count)));
                    break;
                case MODE_YEAR:
                    infoView.setText(i18n.string(R.string.label_year_details,yearDateFormat.format(currentDate),Integer.toString(count)));
                    break;
                default:
                    infoView.setText(i18n.string(R.string.label_week_details,weekDateFormat.format(start),weekDateFormat.format(end),
                            cal.weekOfMonth(currentDate),cal.weekOfYear(currentDate),yearDateFormat.format(start),Integer.toString(count)));
                    break;
                }
                
            }
        });
        
        
    }
    
    private void adjustTextSize(TextView view,int count){
        if(count<=3){
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }else{
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.detlist_optmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.detlist_menu_new:
            detailListHelper.doNewDetail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.detlist_list) {
            getMenuInflater().inflate(R.menu.detlist_ctxmenu, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.detlist_menu_edit:
            detailListHelper.doEditDetail(info.position);
            return true;
        case R.id.detlist_menu_delete:
            detailListHelper.doDeleteDetail(info.position);
            return true;
        case R.id.detlist_menu_copy:
            detailListHelper.doCopyDetail(info.position);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.detlist_prev:
            onPrev();
            break;
        case R.id.detlist_next:
            onNext();
            break;
        case R.id.detlist_today:
            onToday();
            break;
        case R.id.detlist_mode:
            onMode();
            break;
        }
    }

    private void onMode() {
        switch(mode){
        case MODE_WEEK:
            mode = MODE_MONTH;
            reloadData();
            break;
        case MODE_DAY:
            mode = MODE_WEEK;
            reloadData();
            break;    
        case MODE_MONTH:
            mode = allowYearSwitch?MODE_YEAR:MODE_DAY;
            reloadData();
            break;
        case MODE_YEAR:
            mode = allowYearSwitch?MODE_DAY:MODE_YEAR;
            reloadData();
            break;
        }
        
    }

    private void onNext() {
        CalendarHelper cal = getContexts().getCalendarHelper();
        switch(mode){
        case MODE_DAY:
            currentDate = cal.dateAfter(currentDate,1);
            reloadData();
            break;
        case MODE_WEEK:
            currentDate = cal.dateAfter(currentDate,7);
            reloadData();
            break;
        case MODE_MONTH:
            currentDate = cal.monthAfter(currentDate,1);
            reloadData();
            break;
        case MODE_YEAR:
            currentDate = cal.yearAfter(currentDate,1);
            reloadData();
            break;
        }
    }

    private void onPrev() {
        CalendarHelper cal = getContexts().getCalendarHelper();
        switch(mode){
        case MODE_DAY:
            currentDate = cal.dateBefore(currentDate,1);
            reloadData();
            break;
        case MODE_WEEK:
            currentDate = cal.dateBefore(currentDate,7);
            reloadData();
            break;
        case MODE_MONTH:
            currentDate = cal.monthBefore(currentDate,1);
            reloadData();
            break;
        case MODE_YEAR:
            currentDate = cal.yearBefore(currentDate,1);
            reloadData();
            break;
        }
    }
    
    private void onToday() {
        switch(mode){
        case MODE_WEEK:
        case MODE_MONTH:
        case MODE_DAY:
        case MODE_YEAR:
            currentDate = targetDate;
            reloadData();
            break;
        }
    }

}
