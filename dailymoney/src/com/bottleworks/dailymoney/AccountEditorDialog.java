package com.bottleworks.dailymoney;

import java.util.ArrayList;
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

import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.ui.Contexts;

/**
 * Edit or create a account
 * @author dennis
 *
 */
public class AccountEditorDialog extends Dialog implements android.view.View.OnClickListener{

    
    private boolean modeCreate;
    private int counterCreate;
    private Account account;
    private Account workingAccount;
    private OnFinishListener listener;
    
    public AccountEditorDialog(Context context,OnFinishListener listener,boolean modeCreate,Account account) {
        super(context,android.R.style.Theme);
        this.modeCreate = modeCreate;
        this.account = account;
        this.listener = listener;
        workingAccount = clone(account);
    }
    
    /** clone account without id **/
    private Account clone(Account account){
        return new Account(account.getType(),account.getName(),account.getInitialValue());
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(modeCreate){
            setTitle(R.string.title_acceditor_create);
        }else{
            setTitle(R.string.title_acceditor_update);
        }
        setContentView(R.layout.acceditor);
        initialEditor();
    }
    
    public Account getAccount(){
        return account;
    }
    
//    public Account getWorkingAccount(){
//        return workingAccount;
//    }
    
    public boolean isModeCreate(){
        return modeCreate;
    }
    
    
    private static String[] spfrom = new String[] { "display"};
    private static int[] spto = new int[] { R.id.simple_spitem_display};
    
    EditText nameEditor;
    EditText initvalEditor;
    Spinner typeEditor; 
    
    Button okBtn;
    Button cancelBtn;
    Button closeBtn;
    
    private void initialEditor() {
        nameEditor = (EditText)findViewById(R.id.acceditor_name);
        nameEditor.setText(workingAccount.getName());
        
        initvalEditor = (EditText)findViewById(R.id.acceditor_initval);
        initvalEditor.setText(Formats.double2String(workingAccount.getInitialValue()));
        
        //initial spinner
        typeEditor = (Spinner) findViewById(R.id.acceditor_type);
        List<Map<String, Object>> data = new  ArrayList<Map<String, Object>>();
        String type = workingAccount.getType();
        int selpos,i;
        selpos = i = -1;
        for (AccountType at : AccountType.getSupportedType()) {
            i++;
            Map<String, Object> row = new HashMap<String, Object>();
            data.add(row);
            row.put(spfrom[0], AccountType.getDisplay(Contexts.instance().getI18n(),at.getType()));
            
            if(at.getType().equals(type)){
                selpos = i;
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(getContext(), data, R.layout.simple_spitem, spfrom, spto);
        adapter.setDropDownViewResource(R.layout.simple_spdd);
        
        typeEditor.setAdapter(adapter);
        if(selpos>-1){
            typeEditor.setSelection(selpos);
        }
        typeEditor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                AccountType type = AccountType.getSupportedType()[typeEditor.getSelectedItemPosition()];
                onTypeChanged(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        
        onTypeChanged(AccountType.getSupportedType()[selpos]);
        
        okBtn = (Button)findViewById(R.id.acceditor_ok); 
        if(modeCreate){
            okBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_add,0,0,0);
            okBtn.setText(R.string.cact_create);
        }else{
            okBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_update,0,0,0);
            okBtn.setText(R.string.cact_update);
        }
        okBtn.setOnClickListener(this);
        
        
        cancelBtn = (Button)findViewById(R.id.acceditor_cancel); 
        closeBtn =  (Button)findViewById(R.id.acceditor_close);
        
        cancelBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.acceditor_ok:
            doOk();
            break;
        case R.id.acceditor_cancel:
            doCancel();
            break;
        case R.id.acceditor_close:
            doClose();
            break;
        }
    }
    
    
    public int getCounter(){
        return counterCreate;
    }
    
    private void doOk(){   
        I18N i18n = Contexts.instance().getI18n();
        //verify
        if(Spinner.INVALID_POSITION==typeEditor.getSelectedItemPosition()){
            GUIs.shortToast(getContext(),i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.clabel_type)));
            return;
        }
        String name = nameEditor.getText().toString().trim();
        if("".equals(name)){
            GUIs.alert(getContext(),i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.clabel_name)));
            return;
        }
        String initval = initvalEditor.getText().toString();
        if("".equals(initval)){
            GUIs.alert(getContext(),i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.label_initial_value)));
            return;
        }
        //assign
        workingAccount.setType(AccountType.getSupportedType()[typeEditor.getSelectedItemPosition()].getType());
        workingAccount.setName(name);
        workingAccount.setInitialValue(Formats.string2Double(initval));
        if (listener == null) {
            dismiss();
        } else if (listener.onFinish(this, findViewById(R.id.acceditor_ok), workingAccount)) {
            // continue to editor next record if is new mode
            if (modeCreate) {
                workingAccount = clone(workingAccount);
                workingAccount.setName("");
                nameEditor.setText("");
                nameEditor.requestFocus();
                counterCreate++;
                okBtn.setText(Contexts.instance().getI18n().string(R.string.cact_create) + "(" + counterCreate + ")");
                cancelBtn.setVisibility(Button.GONE);
                closeBtn.setVisibility(Button.VISIBLE);
            } else {
                dismiss();
            }
        }
    }
    
    private void doCancel(){
        if(listener==null || listener.onFinish(this,findViewById(R.id.acceditor_cancel), null)){
            dismiss();
        }
    }
    
    private void doClose(){
        if(listener==null || listener.onFinish(this,findViewById(R.id.acceditor_close), null)){
            dismiss();
        }
    }
    
    private void onTypeChanged(AccountType type){
        boolean enableInitval = !(type==AccountType.INCOME || type==AccountType.EXPENSE);
        initvalEditor.setEnabled(enableInitval);
        if(!enableInitval){
            initvalEditor.setText("0");
        }
    }


    public static interface OnFinishListener {   
        public boolean onFinish(AccountEditorDialog dlg,View v,Object data);
    }

}
