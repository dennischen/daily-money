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
import com.bottleworks.dailymoney.data.Book;
import com.bottleworks.dailymoney.data.DuplicateKeyException;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.IMasterDataProvider;

/**
 * Edit or create a book
 * @author dennis
 *
 */
public class BookEditorActivity extends ContextsActivity implements android.view.View.OnClickListener{

    public static final String INTENT_MODE_CREATE = "modeCreate";
    public static final String INTENT_BOOK = "book";
        
    private boolean modeCreate;
    private Book book;
    private Book workingBook;

    Activity activity;
    
    
    /** clone book without id **/
    private Book clone(Book book){
        Book b = new Book(book.getName(),book.getSymbol(),book.isSymboInFront(),book.getNote());
        return b;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookeditor);
        initIntent();
        initialEditor();
    }
    
    private void initIntent() {
        Bundle bundle = getIntentExtras();
        modeCreate = bundle.getBoolean(INTENT_MODE_CREATE,true);
        book = (Book)bundle.get(INTENT_BOOK);
        workingBook = clone(book);
        
        if(modeCreate){
            setTitle(R.string.title_bookeditor_create);
        }else{
            setTitle(R.string.title_bookeditor_update);
        }
    }

    
    EditText nameEditor;
    EditText symbolEditor;
    CheckBox symbolInfrontEditor;
    EditText noteEditor;
    
    
    Button okBtn;
    Button cancelBtn;
    
    private void initialEditor() {
        nameEditor = (EditText)findViewById(R.id.bookeditor_name);
        nameEditor.setText(workingBook.getName());
        
        symbolEditor = (EditText)findViewById(R.id.bookeditor_symbol);
        symbolEditor.setText(workingBook.getSymbol());
        
        symbolInfrontEditor = (CheckBox)findViewById(R.id.bookeditor_symbol_infront);
        symbolInfrontEditor.setChecked(workingBook.isSymboInFront());
        
        noteEditor = (EditText)findViewById(R.id.bookeditor_note);
        noteEditor.setText(workingBook.getNote());
        
        okBtn = (Button)findViewById(R.id.btn_ok);
        if(modeCreate){
            okBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_add,0,0,0);
            okBtn.setText(R.string.cact_create);
        }else{
            okBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_update,0,0,0);
            okBtn.setText(R.string.cact_update);
        }
        okBtn.setOnClickListener(this);
        
        
        cancelBtn = (Button)findViewById(R.id.btn_cancel); 

        
        cancelBtn.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.btn_ok:
            doOk();
            break;
        case R.id.btn_cancel:
            doCancel();
            break;
        }
    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void doOk(){   
        //verify
        String name = nameEditor.getText().toString().trim();
        if("".equals(name)){
            nameEditor.requestFocus();
            GUIs.alert(this,i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.clabel_name)));
            return;
        }
        
        String symbol = symbolEditor.getText().toString().trim();
        if("".equals(name)){
            symbolEditor.requestFocus();
            GUIs.alert(this,i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.label_symbol)));
            return;
        }

        //assign
        workingBook.setName(name);
        workingBook.setSymbol(symbol);
        workingBook.setNote(noteEditor.getText().toString().trim());
        workingBook.setSymboInFront(symbolInfrontEditor.isChecked());
        
        IMasterDataProvider idp = getContexts().getMasterDataProvider();

        if (modeCreate) {
            idp.newBook(workingBook);
            GUIs.shortToast(this, i18n.string(R.string.msg_book_created, name));
        } else {
            idp.updateBook(book.getId(),workingBook);
            GUIs.shortToast(this, i18n.string(R.string.msg_book_updated, name));
            setResult(RESULT_OK);
            finish();
        }
        setResult(RESULT_OK);
        finish();
        
    }
    
    private void doCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
