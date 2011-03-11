package com.bottleworks.dailymoney.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.calculator2.Calculator;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.DuplicateKeyException;
import com.bottleworks.dailymoney.data.IDataProvider;

/**
 * Edit or create a account
 * @author dennis
 *
 */
public class AccountEditorActivity extends ContextsActivity implements android.view.View.OnClickListener{

    public static final String INTENT_MODE_CREATE = "modeCreate";
    public static final String INTENT_ACCOUNT = "account";
        
    private boolean modeCreate;
    private int counterCreate;
    private Account account;
    private Account workingAccount;

    Activity activity;
    
    ImageButton cal2Btn;
    
    
    /** clone account without id **/
    private Account clone(Account account){
        Account acc = new Account(account.getType(),account.getName(),account.getInitialValue());
        acc.setCashAccount(account.isCashAccount());
        return acc;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceditor);
        initIntent();
        initialEditor();
    }
    
    private void initIntent() {
        Bundle bundle = getIntentExtras();
        modeCreate = bundle.getBoolean(INTENT_MODE_CREATE,true);
        account = (Account)bundle.get(INTENT_ACCOUNT);
        workingAccount = clone(account);
        
        if(modeCreate){
            setTitle(R.string.title_acceditor_create);
        }else{
            setTitle(R.string.title_acceditor_update);
        }
    }
    
    /** need to mapping twice to do different mapping in spitem and spdropdown item*/
    private static String[] spfrom = new String[] { Constants.DISPLAY,Constants.DISPLAY};
    private static int[] spto = new int[] { R.id.simple_spitem_display, R.id.simple_spdditem_display};
    
    EditText nameEditor;
    EditText initvalEditor;
    Spinner typeEditor;
    CheckBox cashAccountEditor;
    
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
            row.put(spfrom[0], new NamedItem(spfrom[0],at,at.getDisplay(i18n)));
            
            if(at.getType().equals(type)){
                selpos = i;
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.simple_spitem, spfrom, spto);
        adapter.setDropDownViewResource(R.layout.simple_spdd);
        adapter.setViewBinder(new AccountTypeViewBinder());
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
        
        
        cashAccountEditor = (CheckBox)findViewById(R.id.acceditor_cash_account);
        cashAccountEditor.setChecked(workingAccount.isCashAccount());
        
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
        cal2Btn = (ImageButton)findViewById(R.id.acceditor_cal2);
        
        cancelBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        cal2Btn.setOnClickListener(this);
        
        onTypeChanged(AccountType.getSupportedType()[selpos]);
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
        case R.id.acceditor_cal2:
            doCalculator2();
            break;
        }
    }
    
    
    private void doCalculator2() {
        Intent intent = null;
        intent = new Intent(this,Calculator.class);
        intent.putExtra(Calculator.INTENT_NEED_RESULT,true);
        intent.putExtra(Calculator.INTENT_START_VALUE,initvalEditor.getText().toString());
        startActivityForResult(intent,Constants.REQUEST_CALCULATOR_CODE);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_CALCULATOR_CODE && resultCode==Activity.RESULT_OK){
            String result = data.getExtras().getString(Calculator.INTENT_RESULT_VALUE);
            try{
                double d = Double.parseDouble(result);
                initvalEditor.setText(Formats.double2String(d));
            }catch(Exception x){
            }
        }
    }
    private void doOk(){   
        //verify
        if(Spinner.INVALID_POSITION==typeEditor.getSelectedItemPosition()){
            GUIs.shortToast(this,i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.clabel_type)));
            return;
        }
        String name = nameEditor.getText().toString().trim();
        if("".equals(name)){
            nameEditor.requestFocus();
            GUIs.alert(this,i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.clabel_name)));
            return;
        }
        String initval = initvalEditor.getText().toString();
        if("".equals(initval)){
            initvalEditor.requestFocus();
            GUIs.alert(this,i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.label_initial_value)));
            return;
        }
        String type = AccountType.getSupportedType()[typeEditor.getSelectedItemPosition()].getType();
        //assign
        workingAccount.setType(type);
        workingAccount.setName(name);
        workingAccount.setInitialValue(Formats.string2Double(initval));
        workingAccount.setCashAccount(cashAccountEditor.isChecked());
        
        IDataProvider idp = getContexts().getDataProvider();
        
        Account namedAcc = idp.findAccount(type,name);
        if (modeCreate) {
            if (namedAcc != null) {
                GUIs.alert(
                        this,i18n.string(R.string.msg_account_existed, name,
                                AccountType.getDisplay(i18n, namedAcc.getType())));
                return;
            } else {
                try {
                    idp.newAccount(workingAccount);
                    GUIs.shortToast(this, i18n.string(R.string.msg_account_created, name,AccountType.getDisplay(i18n, workingAccount.getType())));
                } catch (DuplicateKeyException e) {
                    GUIs.alert(this, i18n.string(R.string.cmsg_error, e.getMessage()));
                    return;
                }
            }
            setResult(RESULT_OK);
            workingAccount = clone(workingAccount);
            workingAccount.setName("");
            nameEditor.setText("");
            nameEditor.requestFocus();
            counterCreate++;
            okBtn.setText(i18n.string(R.string.cact_create) + "(" + counterCreate + ")");
            cancelBtn.setVisibility(Button.GONE);
            closeBtn.setVisibility(Button.VISIBLE);
            
        } else {
            if (namedAcc != null && !namedAcc.getId().equals(account.getId())) {
                GUIs.alert(this,i18n.string(R.string.msg_account_existed, name,
                                AccountType.getDisplay(i18n, namedAcc.getType())));
                return;
            } else {
                idp.updateAccount(account.getId(),workingAccount);
                GUIs.shortToast(this, i18n.string(R.string.msg_account_updated, name,AccountType.getDisplay(i18n, workingAccount.getType())));
            }
            
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
        GUIs.shortToast(this,i18n.string(R.string.msg_created_account,counterCreate));
        finish();
    }
    
    private void onTypeChanged(AccountType type){
        //allow income and expense have initial value, since 1/15
//        boolean enableInitval = !(type==AccountType.INCOME || type==AccountType.EXPENSE);
//        initvalEditor.setEnabled(enableInitval);
//        cal2Btn.setEnabled(enableInitval);
//        if(!enableInitval){
//            initvalEditor.setText("0");
//        }
    }
    
    class AccountTypeViewBinder implements SimpleAdapter.ViewBinder{
        @Override
        public boolean setViewValue(View view, Object data, String text) {
            
            NamedItem item = (NamedItem)data;
            String name = item.getName();
            AccountType at = (AccountType)item.getValue();
            if(!(view instanceof TextView)){
               return false;
            }
            if(Constants.DISPLAY.equals(name)){
                if(AccountType.INCOME == at){
                   ((TextView)view).setTextColor(getResources().getColor(R.color.income_fgd));
                }else if(AccountType.ASSET == at){
                    ((TextView)view).setTextColor(getResources().getColor(R.color.asset_fgd)); 
                }else if(AccountType.EXPENSE == at){
                    ((TextView)view).setTextColor(getResources().getColor(R.color.expense_fgd));
                }else if(AccountType.LIABILITY == at){
                    ((TextView)view).setTextColor(getResources().getColor(R.color.liability_fgd)); 
                }else if(AccountType.OTHER == at){
                    ((TextView)view).setTextColor(getResources().getColor(R.color.other_fgd)); 
                }else{
                    ((TextView)view).setTextColor(getResources().getColor(R.color.unknow_fgd));
                }
                ((TextView)view).setText(item.getToString());
                return true;
            }
            return false;
        }
    }

}
