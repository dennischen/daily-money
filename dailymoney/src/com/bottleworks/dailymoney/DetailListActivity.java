package com.bottleworks.dailymoney;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
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
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.ContextsActivity;

/**
 * 
 * @author dennis
 * 
 */
public class DetailListActivity extends ContextsActivity implements OnClickListener {
    
    public static final int MODE_WEEK = 0;
    public static final int MODE_MONTH = 1;
    public static final int MODE_YEAR = 2;
    public static final int MODE_ALL = 3;
    
    public static final String INTENT_MODE = "mode";
    public static final String INTENT_TARGET_DATE = "target";
    public static final String INTENT_ALLOW_SWITCH_YEAR = "switchyear";
    
    DetailListHelper detailListHelper;
    
    TextView infoView;
    TextView summaryView;
    View toolbarView;
    
    private Date targetDate;
    private Date currentDate;
    private int mode = MODE_WEEK;
    private boolean allowYearSwitch = true;
    
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
    }

    @Override
    public void onResume() {
        super.onResume();
        GUIs.postResume(new Runnable() {
            @Override
            public void run() {
                reloadData();
            }
        });
        
    }
    

    private void initialIntent() {
        Intent intent = getIntent();
        if(intent==null){
            return;
        }
        Bundle b = intent.getExtras(); 
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
        
        weekDateFormat = new SimpleDateFormat("MM/dd");
        monthDateFormat = new SimpleDateFormat("yyyy/MM - MMM");
        yearDateFormat = new SimpleDateFormat("yyyy");
        
        detailListHelper = new DetailListHelper(this, i18n,false, new DetailListHelper.OnDetailListener() {
            @Override
            public boolean onDetailUpdated(Detail detail) {
                GUIs.shortToast(DetailListActivity.this, i18n.string(R.string.msg_detail_updated));
                reloadData();
                return true;
            }

            @Override
            public boolean onDetailDeleted(Detail detail) {
                GUIs.shortToast(DetailListActivity.this, i18n.string(R.string.msg_detail_deleted));
                reloadData();
                return true;
            }

            @Override
            public boolean onDetailCreated(Detail detail) {
                return false;
            }

            @Override
            public boolean onEditorClosed(int counter) {
                if(counter>0){
                    GUIs.shortToast(DetailListActivity.this,i18n.string(R.string.msg_created_detail,counter));
                    reloadData();
                    return true;
                }
                return false;
            }
        });
        
        
        infoView = (TextView)findViewById(R.id.detlist_infobar);
        toolbarView = findViewById(R.id.detlist_toolbar);
        summaryView = (TextView)findViewById(R.id.detlist_summarybar);
        
        
        findViewById(R.id.detlist_prev).setOnClickListener(this);
        findViewById(R.id.detlist_next).setOnClickListener(this);
        findViewById(R.id.detlist_today).setOnClickListener(this);
        modeBtn = (ImageButton)findViewById(R.id.detlist_mode);
        modeBtn.setOnClickListener(this);
        
        ListView listView = (ListView)findViewById(R.id.detlist_list);
        detailListHelper.setup(listView);
        registerForContextMenu(listView);
    }
    

    private void reloadData() {
        CalendarHelper cal = Contexts.instance().getCalendarHelper();
        final Date start;
        final Date end;
        switch(mode){
        case MODE_ALL:
            start = end = null;
            toolbarView.setVisibility(TextView.GONE);
            infoView.setText(i18n.string(R.string.label_all_details));
            break;
        case MODE_MONTH:
            start = cal.monthStartDate(currentDate);
            end = cal.monthEndDate(currentDate);
            toolbarView.setVisibility(TextView.VISIBLE);
            infoView.setText(i18n.string(R.string.label_month_details,monthDateFormat.format(currentDate)));
            modeBtn.setVisibility(ImageButton.VISIBLE);
            if(allowYearSwitch){
                modeBtn.setImageResource(R.drawable.btn_year);
            }else{
                modeBtn.setImageResource(R.drawable.btn_week);
            }
            break;
        case MODE_YEAR:
            start = cal.yearStartDate(currentDate);
            end = cal.yearEndDate(currentDate);
            toolbarView.setVisibility(TextView.VISIBLE);
            infoView.setText(i18n.string(R.string.label_year_details,yearDateFormat.format(currentDate)));

            if(allowYearSwitch){
                modeBtn.setVisibility(ImageButton.VISIBLE);
                modeBtn.setImageResource(R.drawable.btn_week);
            }else{
                modeBtn.setVisibility(ImageButton.GONE);
            }
            
            break;
        default:
            start = cal.weekStartDate(currentDate);
            end = cal.weekEndDate(currentDate);
            toolbarView.setVisibility(TextView.VISIBLE);
            infoView.setText(i18n.string(R.string.label_week_details,weekDateFormat.format(start),weekDateFormat.format(end),
                    cal.weekOfMonth(currentDate),cal.weekOfYear(currentDate),yearDateFormat.format(start)));
            modeBtn.setVisibility(ImageButton.VISIBLE);
            modeBtn.setImageResource(R.drawable.btn_month);
            break;
        }
        final IDataProvider idp = Contexts.instance().getDataProvider();
//        detailListHelper.reloadData(idp.listAllDetail());
        GUIs.doBusy(this,new GUIs.BusyAdapter() {
            List<Detail> data = null;
            
            double expense;
            double income;
            int count;
            
            @Override
            public void run() {
                data = idp.listDetail(start,end,Contexts.instance().getPrefMaxRecords());
                count = idp.countDetail(start, end);
                income = idp.sumIncome(start,end);
                expense = idp.sumExpense(start,end);
            }
            @Override
            public void onBusyFinish() {
                
              //update data
                detailListHelper.reloadData(data);
                summaryView.setText(i18n.string(R.string.label_detlist_summary,Integer.toString(count),Formats.double2String(income),Formats.double2String(expense)));
              //update info
                switch(mode){
                case MODE_ALL:
                    break;
                case MODE_MONTH:
                    break;
                case MODE_YEAR:
                    break;
                default:
                    
                    break;
                }
                
            }
        });
        
        
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.detlist_optmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d("option menu selected :" + item.getItemId());
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
        Logger.d("context menu selected :" + item.getItemId() + ", pos " + info.position);
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
        case MODE_MONTH:
            mode = allowYearSwitch?MODE_YEAR:MODE_WEEK;
            reloadData();
            break;
        case MODE_YEAR:
            mode = allowYearSwitch?MODE_WEEK:MODE_YEAR;
            reloadData();
            break;
        }
        
    }

    private void onNext() {
        CalendarHelper cal = Contexts.instance().getCalendarHelper();
        switch(mode){
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
        CalendarHelper cal = Contexts.instance().getCalendarHelper();
        switch(mode){
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
        case MODE_YEAR:
            currentDate = targetDate;
            reloadData();
            break;
        }
    }

}
