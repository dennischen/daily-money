package com.bottleworks.dailymoney;

import java.io.ObjectInputStream.GetField;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.bottleworks.commons.util.Calendars;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;

/**
 * Edit or create a detail
 * 
 * @author dennis
 * 
 */
public class DetailEditorDialog extends Dialog implements android.view.View.OnClickListener {

    private boolean modeCreate;
    private int counterCreate;
    private Detail detail;
    private Detail workingDetail;
    private OnFinishListener listener;

    private DateFormat format;

    boolean archived = false;

    private List<Account> fromAccountList;
    private List<Account> toAccountList;

    List<Map<String, Object>> fromAccountMapList;
    List<Map<String, Object>> toAccountMapList;

    private SimpleAdapter fromAccountAdapter;
    private SimpleAdapter toAccountAdapter;

    public DetailEditorDialog(Context context, OnFinishListener listener, boolean modeCreate, Detail detail) {
        super(context, R.style.theme_acceidtor);
        this.modeCreate = modeCreate;
        this.detail = detail;
        this.listener = listener;
        workingDetail = clone(detail);
    }

    /** clone a detail without id **/
    private Detail clone(Detail detail) {
        Detail d = new Detail(detail.getFrom(), detail.getTo(), detail.getDate(), detail.getMoney(), detail.getNote());
        d.setArchived(detail.isArchived());
        return d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(modeCreate){
            setTitle(R.string.title_deteditor_create);
        }else{
            setTitle(R.string.title_deteditor_update);
        }
        setContentView(R.layout.deteditor);
        format = Contexts.instance().getDateFormat();
        initialEditor();
    }

    public Detail getDetail() {
        return detail;
    }

    // public Detail getWorkingDetail(){
    // return workingDetail;
    // }

    public boolean isModeCreate() {
        return modeCreate;
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
    
    public int getCounter(){
        return counterCreate;
    }

    private void initialSpinner() {
        fromEditor = (Spinner) findViewById(R.id.deteditor_from);
        fromAccountList = new ArrayList<Account>();
        fromAccountMapList = new ArrayList<Map<String, Object>>();
        fromAccountAdapter = new SimpleAdapter(getContext(), fromAccountMapList, R.layout.simple_spitem, spfrom, spto);
        fromAccountAdapter.setDropDownViewResource(R.layout.simple_spdd);
        fromEditor.setAdapter(fromAccountAdapter);

        toEditor = (Spinner) findViewById(R.id.deteditor_to);
        toAccountList = new ArrayList<Account>();
        toAccountMapList = new ArrayList<Map<String, Object>>();
        toAccountAdapter = new SimpleAdapter(getContext(), toAccountMapList, R.layout.simple_spitem, spfrom, spto);
        toAccountAdapter.setDropDownViewResource(R.layout.simple_spdd);
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
        IDataProvider idp = Contexts.instance().getDataProvider();
        // I18N i18n = Contexts.instance().getI18n();

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
            String display = acc.getType() + "-" + acc.getName();
            row.put(spfrom[0], display);
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
            String display = acc.getType() + "-" + acc.getName();
            row.put(spfrom[0], display);
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
                updateDateEditor(Calendars.yesterday(d));
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
            break;
        case R.id.deteditor_next:
            try {
                Date d = format.parse(dateEditor.getText().toString());
                updateDateEditor(Calendars.tomorrow(d));
            } catch (ParseException e) {
                Logger.e(e.getMessage(), e);
            }
            break;
        case R.id.deteditor_today:
            updateDateEditor(Calendars.today());
            break;
        case R.id.deteditor_datepicker:
            try {
                Date d = format.parse(dateEditor.getText().toString());
                GUIs.openDatePicker(getContext(), d, new GUIs.OnFinishListener() {
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
        }
    }

    private void doOk() {
        Logger.d("doOK");
        I18N i18n = Contexts.instance().getI18n();
        // verify
        int fromPos = fromEditor.getSelectedItemPosition();
        if (Spinner.INVALID_POSITION == fromPos) {
            GUIs.alert(getContext(),
                    i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_from_account)));
            return;
        }
        int toPos = toEditor.getSelectedItemPosition();
        if (Spinner.INVALID_POSITION == toPos) {
            GUIs.alert(getContext(),
                    i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_to_account)));
            return;
        }
        String datestr = dateEditor.getText().toString().trim();
        if ("".equals(datestr)) {
            GUIs.alert(getContext(), i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_date)));
            return;
        }

        Date date = null;
        try {
            date = Contexts.instance().getDateFormat().parse(datestr);
        } catch (ParseException e) {
            Logger.e(e.getMessage(), e);
            GUIs.errorToast(getContext(), e);
            return;
        }

        String moneystr = moneyEditor.getText().toString();
        if ("".equals(moneystr)) {
            GUIs.alert(getContext(), i18n.string(R.string.cmsg_field_empty, i18n.string(R.string.label_money)));
            return;
        }
        double money = Formats.string2Double(moneystr);
        if (money==0) {
            GUIs.alert(getContext(), i18n.string(R.string.cmsg_field_zero, i18n.string(R.string.label_money)));
            return;
        }
        
        String note = noteEditor.getText().toString();

        Account fromAcc = fromAccountList.get(fromPos);
        Account toAcc =  toAccountList.get(toPos);

        if (fromAcc.getId().equals(toAcc.getId())) {
            GUIs.alert(getContext(), i18n.string(R.string.msg_same_from_to));
            return;
        }

        
        
        workingDetail.setFrom(fromAcc.getId());
        workingDetail.setTo(toAcc.getId());

        workingDetail.setDate(date);
        workingDetail.setMoney(money);
        workingDetail.setNote(note.trim());

        if (listener == null) {
            dismiss();
        } else if (listener.onFinish(this, findViewById(R.id.deteditor_ok), workingDetail)) {
            // continue to editor next record if is new mode
            if (modeCreate) {
                workingDetail = clone(workingDetail);
                workingDetail.setMoney(0D);
                moneyEditor.setText("");
                counterCreate++;
                okBtn.setText(Contexts.instance().getI18n().string(R.string.cact_create) + "(" + counterCreate + ")");
                cancelBtn.setVisibility(Button.GONE);
                closeBtn.setVisibility(Button.VISIBLE);
            } else {
                dismiss();
            }
        }
    }

    private void doCancel() {
        Logger.d("doCancel");
        if (listener == null || listener.onFinish(this, findViewById(R.id.deteditor_cancel), null)) {
            dismiss();
        }
    }

    private void doClose() {
        Logger.d("doClose");
        if (listener == null || listener.onFinish(this, findViewById(R.id.deteditor_close), null)) {
            dismiss();
        }
    }

    public static interface OnFinishListener {
        public boolean onFinish(DetailEditorDialog dlg, View v, Object data);
    }

}
