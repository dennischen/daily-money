package com.bottleworks.dailymoney;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bottleworks.commons.util.Calendars;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.ui.Contexts;

/**
 * Edit or create a detail
 * @author dennis
 *
 */
public class DetailEditorDialog extends Dialog implements android.view.View.OnClickListener{

    
    private boolean modeCreate;
    private int counterCreate;
    private Detail detail;
    private Detail workingDetail;
    private OnFinishListener listener;
    
    private DateFormat format;
    
    boolean archived = false;
    
    public DetailEditorDialog(Context context,OnFinishListener listener,boolean modeCreate,Detail detail) {
        super(context,R.style.theme_acceidtor);
        this.modeCreate = modeCreate;
        this.detail = detail;
        this.listener = listener;
        workingDetail = new Detail(detail.getFrom(),detail.getFromDisplay(),detail.getTo(),detail.getToDisplay(),detail.getDate(),detail.getMoney(),detail.getNote());
        workingDetail.setArchived(detail.isArchived());
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_deteditor);
        setContentView(R.layout.deteditor);
        format = Contexts.instance().getDateFormat();
        initialEditor();
    }
    
    
    public Detail getDetail(){
        return detail;
    }
    
    public Detail getWorkingDetail(){
        return workingDetail;
    }
    
    public boolean isModeCreate(){
        return modeCreate;
    }
    
    
    private static String[] spfrom = new String[] { "display"};
    private static int[] spto = new int[] { R.id.simple_spitem_display};
    
    
    Spinner fromEditor; 
    Spinner toEditor; 
    
    EditText dateEditor;
    EditText noteEditor;
    EditText moneyEditor;
    
    Button okBtn;
    Button cancelBtn;
    
    
    private void initialEditor() {
        
        boolean archived = workingDetail.isArchived();
        
        //initial spinner
//        fromEditor = (Spinner) findViewById(R.id.deteditor_from);
//        List<Map<String, Object>> data = new  ArrayList<Map<String, Object>>();
//        String type = workingDetail.getFromAccount();
//        int selpos,i;
//        selpos = i = 0;
//        for (AccountType at : AccountType.getSupportedType()) {
//            Map<String, Object> row = new HashMap<String, Object>();
//            data.add(row);
//            row.put(spfrom[0], AccountType.getDisplay(Contexts.instance().getI18n(),at.getType()));
//            
//            if(at.getType().equals(type)){
//                selpos = i;
//            }
//            i++;
//        }
//        SimpleAdapter adapter = new SimpleAdapter(getContext(), data, R.layout.simple_spitem, spfrom, spto);
//        adapter.setDropDownViewResource(R.layout.simple_spdd);
//        
//        fromEditor.setAdapter(adapter);
//        fromEditor.setSelection(selpos);
//        fromEditor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                AccountType type = AccountType.getSupportedType()[typeEditor.getSelectedItemPosition()];
//                checkType(type);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//        
//        checkType(AccountType.getSupportedType()[selpos]);
        
        
        dateEditor = (EditText)findViewById(R.id.deteditor_date);
        dateEditor.setText(format.format(workingDetail.getDate()));
        dateEditor.setEnabled(!archived);
        
        moneyEditor = (EditText)findViewById(R.id.deteditor_money);
        moneyEditor.setText(Formats.double2String(workingDetail.getMoney()));
        moneyEditor.setEnabled(!archived);
        
        noteEditor = (EditText)findViewById(R.id.deteditor_note);
        noteEditor.setText(workingDetail.getNote());
        
        if(!archived){
            findViewById(R.id.deteditor_prev).setOnClickListener(this);
            findViewById(R.id.deteditor_next).setOnClickListener(this);
            findViewById(R.id.deteditor_today).setOnClickListener(this);
            findViewById(R.id.deteditor_datepicker).setOnClickListener(this);
        }
        
        okBtn = (Button)findViewById(R.id.deteditor_ok); 
        if(modeCreate){
            okBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_add,0,0,0);
            okBtn.setText(R.string.cact_create);
        }else{
            okBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_update,0,0,0);
            okBtn.setText(R.string.cact_update);
        }
        okBtn.setOnClickListener(this);
        
        cancelBtn = (Button)findViewById(R.id.deteditor_cancel); 
        
        findViewById(R.id.deteditor_cancel).setOnClickListener(this);
    }
    
    private void updateDateEditor(Date d){
        dateEditor.setText(format.format(d));
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.deteditor_ok:
            doOk();
            break;
        case R.id.deteditor_cancel:
            doCancel();
            break;
        case R.id.deteditor_prev:
            try {
                Date d = format.parse(dateEditor.getText().toString());
                updateDateEditor(Calendars.yesterday(d));
            } catch (ParseException e) {
                Logger.e(e.getMessage(),e);
            }
            break;
        case R.id.deteditor_next:
            try {
                Date d = format.parse(dateEditor.getText().toString());
                updateDateEditor(Calendars.tomorrow(d));
            } catch (ParseException e) {
                Logger.e(e.getMessage(),e);
            }
            break;
        case R.id.deteditor_today:
            updateDateEditor(Calendars.today());
            break;
        case R.id.deteditor_datepicker:
            try {
            Date d = format.parse(dateEditor.getText().toString());
                GUIs.openDatePicker(getContext(),d,new GUIs.OnFinishListener() {
                    @Override
                    public boolean onFinish(Object data) {
                        updateDateEditor((Date)data);
                        return true;
                    }
                });
            } catch (ParseException e) {
                Logger.e(e.getMessage(),e);
            }
            break;
        }
    }
    
    private void doOk(){
        Logger.d("doOK");   
      //verify
//        if(Spinner.INVALID_POSITION==typeEditor.getSelectedItemPosition()){
//            GUIs.shortToast(getContext(),i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.clabel_type)));
//            return;
//        }
//        String name = nameEditor.getText().toString().trim();
//        if("".equals(name)){
//            GUIs.shortToast(getContext(),i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.clabel_name)));
//            return;
//        }
//        String initval = initvalEditor.getText().toString();
//        if("".equals(initval)){
//            GUIs.shortToast(getContext(),i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.label_initial_value)));
//            return;
//        }        
//        workingDetail.setType(AccountType.getSupportedType()[typeEditor.getSelectedItemPosition()].getType());
//        workingDetail.setName(nameEditor.getText().toString());
//        workingDetail.setInitialValue(Formats.string2Double(initvalEditor.getText().toString()));
        
        if(listener==null){
            dismiss();
        }else if(listener.onFinish(this,findViewById(R.id.deteditor_ok), workingDetail)){
            //continue to editor next record if is new mode
            if(modeCreate){
                //TODO
                counterCreate++;
                okBtn.setText(Contexts.instance().getI18n().string(R.string.cact_create)+"("+counterCreate+")");
                cancelBtn.setText(R.string.cact_close);
            }else{
                dismiss();
            }
        }
    }
    
    private void doCancel(){
        Logger.d("doCancel");
        if(listener==null || listener.onFinish(this,findViewById(R.id.deteditor_cancel), null)){
            dismiss();
        }
    }
    
//    private void checkType(AccountType type){
//        boolean enableInitval = !(type==AccountType.INCOME || type==AccountType.EXPENSE);
//        initvalEditor.setEnabled(enableInitval);
//        if(!enableInitval){
//            initvalEditor.setText("0");
//        }
//    }

    public static interface OnFinishListener {   
        public boolean onFinish(DetailEditorDialog dlg,View v,Object data);
    }

}
