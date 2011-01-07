package com.bottleworks.dailymoney;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.IDialogFinishListener;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.ui.Contexts;

/**
 * Edit or create a detail
 * @author dennis
 *
 */
public class DetailEditorDialog extends Dialog implements android.view.View.OnClickListener{

    
    private boolean modeCreate;
    private Detail detail;
    private Detail workingDetail;
    private IDialogFinishListener listener;
    
    private DateFormat format;
    
    public DetailEditorDialog(Context context,IDialogFinishListener listener,boolean modeCreate,Detail detail) {
        super(context,R.style.theme_acceidtor);
        this.modeCreate = modeCreate;
        this.detail = detail;
        this.listener = listener;
        workingDetail = new Detail(detail.getFromAccount(),detail.getFromDisplay(),detail.getToAccount(),detail.getToDisplay(),detail.getDate(),detail.getMoney(),detail.getNote());
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
    
    
    private void initialEditor() {
        
        
        
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
         
        moneyEditor = (EditText)findViewById(R.id.deteditor_money);
        moneyEditor.setText(Formats.double2String(workingDetail.getMoney()));
        
        noteEditor = (EditText)findViewById(R.id.deteditor_note);
        noteEditor.setText(workingDetail.getNote());
        
        
        
        
        Button ok = (Button)findViewById(R.id.deteditor_ok); 
        if(modeCreate){
            ok.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_add,0,0,0);
            ok.setText(R.string.cact_create);
        }else{
            ok.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_update,0,0,0);
            ok.setText(R.string.cact_update);
        }
        ok.setOnClickListener(this);
        findViewById(R.id.deteditor_cancel).setOnClickListener(this);
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
        }
    }
    
    private void doOk(){
        Logger.d("doOK");   
        
//        workingDetail.setType(AccountType.getSupportedType()[typeEditor.getSelectedItemPosition()].getType());
//        workingDetail.setName(nameEditor.getText().toString());
//        workingDetail.setInitialValue(Formats.string2Double(initvalEditor.getText().toString()));
        
        if(listener==null || listener.onDialogFinish(this,findViewById(R.id.deteditor_ok), null)){
            dismiss();
        }
    }
    
    private void doCancel(){
        Logger.d("doCancel");
        if(listener==null || listener.onDialogFinish(this,findViewById(R.id.deteditor_cancel), null)){
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



}
