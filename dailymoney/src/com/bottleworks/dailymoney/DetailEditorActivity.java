package com.bottleworks.dailymoney;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.calculator2.Calculator;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.NamedItem;

/**
 * Edit or create a detail
 * 
 * @author dennis
 * 
 */
public class DetailEditorActivity extends ContextsActivity implements android.view.View.OnClickListener {
    
    public static final String INTENT_MODE_CREATE = "modeCreate";
    public static final String INTENT_DETAIL = "detail";
    
//    public static final String PARAMETER_RESULT_DETAIL = "detail";
//    public static final String PARAMETER_RESULT_WORKING_DETAIL = "workingDetail";
   
    
    private boolean modeCreate;
    private int counterCreate;
    private Detail detail;
    private Detail workingDetail;

    private DateFormat format;

    boolean archived = false;

    private List<Account> fromAccountList;
    private List<Account> toAccountList;

    List<Map<String, Object>> fromAccountMapList;
    List<Map<String, Object>> toAccountMapList;

    private SimpleAdapter fromAccountAdapter;
    private SimpleAdapter toAccountAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deteditor);
        format = Contexts.uiInstance().getDateFormat();
        initIntent();
        initialEditor();
    }
    

    /** clone a detail without id **/
    private Detail clone(Detail detail) {
        Detail d = new Detail(detail.getFrom(), detail.getTo(), detail.getDate(), detail.getMoney(), detail.getNote());
        d.setArchived(detail.isArchived());
        return d;
    }


    private void initIntent() {
        Bundle bundle = getIntentExtras();
        modeCreate = bundle.getBoolean(INTENT_MODE_CREATE,true);
        detail = (Detail)bundle.get(INTENT_DETAIL);
        workingDetail = clone(detail);
        
        if(modeCreate){
            setTitle(R.string.title_deteditor_create);
        }else{
            setTitle(R.string.title_deteditor_update);
        }
    }

    private static String[] spfrom = new String[] { "display" };
    private static int[] spto = new int[] { R.id.simple_spitem_display };

    Spinner fromEditor;
    Spinner toEditor;

    EditText dateEditor;
    EditText noteEditor;
    EditText moneyEditor;

    Button okBtn;
    Button cancelBtn;
    Button closeBtn;

    private void initialEditor() {

        boolean archived = workingDetail.isArchived();

        // initial spinner

        initialSpinner();

        dateEditor = (EditText) findViewById(R.id.deteditor_date);
        dateEditor.setText(format.format(workingDetail.getDate()));
        dateEditor.setEnabled(!archived);

        moneyEditor = (EditText) findViewById(R.id.deteditor_money);
        moneyEditor.setText(workingDetail.getMoney()<=0?"":Formats.double2String(workingDetail.getMoney()));
        moneyEditor.setEnabled(!archived);

        noteEditor = (EditText) findViewById(R.id.deteditor_note);
        noteEditor.setText(workingDetail.getNote());

        if (!archived) {
            findViewById(R.id.deteditor_prev).setOnClickListener(this);
            findViewById(R.id.deteditor_next).setOnClickListener(this);
            findViewById(R.id.deteditor_today).setOnClickListener(this);
            findViewById(R.id.deteditor_datepicker).setOnClickListener(this);
        }
        findViewById(R.id.deteditor_cal2).setOnClickListener(this);

        okBtn = (Button) findViewById(R.id.deteditor_ok);
        if (modeCreate) {
            okBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_add, 0, 0, 0);
            okBtn.setText(R.string.cact_create);
        } else {
            okBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_update, 0, 0, 0);
            okBtn.setText(R.string.cact_update);
        }
        okBtn.setOnClickListener(this);

        cancelBtn = (Button) findViewById(R.id.deteditor_cancel);
        closeBtn = (Button) findViewById(R.id.deteditor_close);

        cancelBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
    }

    private void initialSpinner() {
        fromEditor = (Spinner) findViewById(R.id.deteditor_from);
        fromAccountList = new ArrayList<Account>();
        fromAccountMapList = new ArrayList<Map<String, Object>>();
        fromAccountAdapter = new SimpleAdapter(this, fromAccountMapList, R.layout.simple_spitem, spfrom, spto);
        fromAccountAdapter.setDropDownViewResource(R.layout.simple_spdd);
        fromAccountAdapter.setViewBinder(new AccountViewBinder());
        fromEditor.setAdapter(fromAccountAdapter);
        

        toEditor = (Spinner) findViewById(R.id.deteditor_to);
        toAccountList = new ArrayList<Account>();
        toAccountMapList = new ArrayList<Map<String, Object>>();
        toAccountAdapter = new SimpleAdapter(this, toAccountMapList, R.layout.simple_spitem, spfrom, spto);
        toAccountAdapter.setDropDownViewResource(R.layout.simple_spdd);
        toAccountAdapter.setViewBinder(new AccountViewBinder());
        toEditor.setAdapter(toAccountAdapter);

        reloadSpinnerData();

        fromEditor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Account acc = fromAccountList.get(pos);
                onFromChanged(acc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        toEditor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Account acc = toAccountList.get(pos);
                onToChanged(acc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void reloadSpinnerData() {
        IDataProvider idp = Contexts.uiInstance().getDataProvider();
         I18N i18n = Contexts.uiInstance().getI18n();

        // initial from
        AccountType[] avail = AccountType.getFromType();
        fromAccountList.clear();
        fromAccountMapList.clear();
        for (AccountType at : avail) {
            for (Account acc : idp.listAccount(at)) {
                fromAccountList.add(acc);
            }
        }
        String fromAccount = workingDetail.getFrom();
        int fromsel, i;
        fromsel = i = -1;
        String fromType = null;
        for (Account acc : fromAccountList) {
            i++;
            Map<String, Object> row = new HashMap<String, Object>();
            fromAccountMapList.add(row);
            // String display =
            // AccountType.getDisplay(Contexts.instance().getI18n(),acc.getType())
            // + "-" + acc.getName();
            String display = AccountType.find(acc.getType()).getDisplay(i18n) + "-" + acc.getName();
            row.put(spfrom[0], new NamedItem(spfrom[0],acc,display));
            if (acc.getId().equals(fromAccount)) {
                fromsel = i;
                fromType = acc.getType();
            }
        }

        // initial to
        avail = AccountType.getToType(fromType);
        toAccountList.clear();
        toAccountMapList.clear();
        for (AccountType at : avail) {
            for (Account acc : idp.listAccount(at)) {
                toAccountList.add(acc);
            }
        }
        String toAccount = workingDetail.getTo();
        int tosel;
        tosel = i = -1;
        // String toType = null;
        for (Account acc : toAccountList) {
            i++;
            Map<String, Object> row = new HashMap<String, Object>();
            toAccountMapList.add(row);
            // String display =
            // AccountType.getDisplay(Contexts.instance().getI18n(),acc.getType())
            // + "-" + acc.getName();
            String display = AccountType.find(acc.getType()).getDisplay(i18n) + "-" + acc.getName();
            row.put(spfrom[0], new NamedItem(spfrom[0],acc,display));
            if (acc.getId().equals(toAccount)) {
                tosel = i;
                // toType = acc.getType();
            }
        }

        if (fromsel > -1) {
            fromEditor.setSelection(fromsel);
        } else if (fromAccountList.size() > 0) {
            fromEditor.setSelection(0);
            workingDetail.setFrom(fromAccountList.get(0).getId());
        } else {
            workingDetail.setFrom("");
        }

        if (tosel > -1) {
            toEditor.setSelection(tosel);
        } else if (toAccountList.size() > 0) {
            toEditor.setSelection(0);
            workingDetail.setTo(toAccountList.get(0).getId());
        } else {
            workingDetail.setTo("");
        }

        fromAccountAdapter.notifyDataSetChanged();
        toAccountAdapter.notifyDataSetChanged();
    }

    private void onFromChanged(Account acc) {
        workingDetail.setFrom(acc.getId());
        reloadSpinnerData();
    }

    private void onToChanged(Account acc) {
        workingDetail.setTo(acc.getId());
    }

    private void updateDateEditor(Date d) {
        dateEditor.setText(format.format(d));
    }

    @Override
    public void onClick(View v) {
        CalendarHelper cal = Contexts.uiInstance().getCalendarHelper();
        switch (v.getId()) {
        case R.id.deteditor_ok:
            doOk();
            break;
        case R.id.deteditor_cancel:
            doCancel();
            break;
        case R.id.deteditor_close:
            doClose();
            break;
        case R.id.deteditor_prev:
            try {
                Date d = format.parse(dateEditor.getText().toString());
                updateDateEditor(cal.yesterday(d));
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
            break;
        case R.id.deteditor_next:
            try {
                Date d = format.parse(dateEditor.getText().toString());
                updateDateEditor(cal.tomorrow(d));
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
            break;
        case R.id.deteditor_today:
            updateDateEditor(cal.today());
            break;
        case R.id.deteditor_datepicker:
            try {
                Date d = format.parse(dateEditor.getText().toString());
                GUIs.openDatePicker(this, d, new GUIs.OnFinishListener() {
                    @Override
                    public boolean onFinish(Object data) {
                        updateDateEditor((Date) data);
                        return true;
                    }
                });
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
            break;
        case R.id.deteditor_cal2:
            doCalculator2();
            break;
        }
    }
    
    private void doCalculator2() {
        Intent intent = null;
        intent = new Intent(this,Calculator.class);
        intent.putExtra(Calculator.INTENT_NEED_RESULT,true);
        intent.putExtra(Calculator.INTENT_START_VALUE,moneyEditor.getText().toString());
        startActivityForResult(intent,Constants.REQUEST_CALCULATOR_CODE);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_CALCULATOR_CODE && resultCode==Activity.RESULT_OK){
            String result = data.getExtras().getString(Calculator.INTENT_RESULT_VALUE);
            try{
                double d = Double.parseDouble(result);
                if(d>0){
                    moneyEditor.setText(Formats.double2String(d));
                }else{
                    moneyEditor.setText("0");
                }
            }catch(Exception x){
            }
        }
    }

    private void doOk() {
        I18N i18n = Contexts.uiInstance().getI18n();
        // verify
        int fromPos = fromEditor.getSelectedItemPosition();
        if (Spinner.INVALID_POSITION == fromPos) {
            GUIs.alert(this,
                    i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_from_account)));
            return;
        }
        int toPos = toEditor.getSelectedItemPosition();
        if (Spinner.INVALID_POSITION == toPos) {
            GUIs.alert(this,
                    i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_to_account)));
            return;
        }
        String datestr = dateEditor.getText().toString().trim();
        if ("".equals(datestr)) {
            dateEditor.requestFocus();
            GUIs.alert(this, i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_date)));
            return;
        }

        Date date = null;
        try {
            date = Contexts.uiInstance().getDateFormat().parse(datestr);
        } catch (ParseException e) {
            Logger.e(e.getMessage(), e);
            GUIs.errorToast(this, e);
            return;
        }

        String moneystr = moneyEditor.getText().toString();
        if ("".equals(moneystr)) {
            moneyEditor.requestFocus();
            GUIs.alert(this, i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_money)));
            return;
        }
        double money = Formats.string2Double(moneystr);
        if (money==0) {
            GUIs.alert(this, i18n.string(R.string.cmsg_field_zero, i18n.string(R.string.label_money)));
            return;
        }
        
        String note = noteEditor.getText().toString();

        Account fromAcc = fromAccountList.get(fromPos);
        Account toAcc =  toAccountList.get(toPos);

        if (fromAcc.getId().equals(toAcc.getId())) {
            GUIs.alert(this, i18n.string(R.string.msg_same_from_to));
            return;
        }

        
        
        workingDetail.setFrom(fromAcc.getId());
        workingDetail.setTo(toAcc.getId());

        workingDetail.setDate(date);
        workingDetail.setMoney(money);
        workingDetail.setNote(note.trim());
        IDataProvider idp = Contexts.uiInstance().getDataProvider();
        if (modeCreate) {
            
            idp.newDetail(workingDetail);

            workingDetail = clone(workingDetail);
            workingDetail.setMoney(0D);
            workingDetail.setNote("");
            moneyEditor.setText("");
            moneyEditor.requestFocus();
            noteEditor.setText("");
            counterCreate++;
            okBtn.setText(Contexts.uiInstance().getI18n().string(R.string.cact_create) + "(" + counterCreate + ")");
            cancelBtn.setVisibility(Button.GONE);
            closeBtn.setVisibility(Button.VISIBLE);
        } else {
            
            idp.updateDetail(detail.getId(),workingDetail);
            
            GUIs.shortToast(this, i18n.string(R.string.msg_detail_updated));
            setResult(RESULT_OK);
            finish();
        }
    }

    private void doCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void doClose() {
        setResult(RESULT_OK);
        GUIs.shortToast(this,i18n.string(R.string.msg_created_detail,counterCreate));
        finish();
    }
    
    class AccountViewBinder implements SimpleAdapter.ViewBinder{
        @Override
        public boolean setViewValue(View view, Object data, String text) {
            
            NamedItem item = (NamedItem)data;
            String name = item.getName();
            Account acc = (Account)item.getValue();
            if(!(view instanceof TextView)){
               return false;
            }
            AccountType at = AccountType.find(acc.getType());
            if("display".equals(name)){
                if(AccountType.INCOME == at){
                   ((TextView)view).setTextColor(DetailEditorActivity.this.getResources().getColor(R.color.income_fgd));
                }else if(AccountType.ASSET == at){
                    ((TextView)view).setTextColor(DetailEditorActivity.this.getResources().getColor(R.color.asset_fgd)); 
                }else if(AccountType.EXPENSE == at){
                    ((TextView)view).setTextColor(DetailEditorActivity.this.getResources().getColor(R.color.expense_fgd));
                }else if(AccountType.LIABILITY == at){
                    ((TextView)view).setTextColor(DetailEditorActivity.this.getResources().getColor(R.color.liability_fgd)); 
                }else if(AccountType.OTHER == at){
                    ((TextView)view).setTextColor(DetailEditorActivity.this.getResources().getColor(R.color.other_fgd)); 
                }else{
                    ((TextView)view).setTextColor(DetailEditorActivity.this.getResources().getColor(R.color.unknow_fgd));
                }
                ((TextView)view).setText(item.getToString());
                return true;
            }
            return false;
        }
    }

}
