package com.bottleworks.dailymoney.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Book;
import com.bottleworks.dailymoney.data.IMasterDataProvider;
import com.bottleworks.dailymoney.data.SymbolPosition;

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
        Book b = new Book(book.getName(),book.getSymbol(),book.getSymbolPosition(),book.getNote());
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

    /** need to mapping twice to do different mapping in spitem and spdropdown item*/
    private static String[] spfrom = new String[] { Constants.DISPLAY,Constants.DISPLAY};
    private static int[] spto = new int[] { R.id.simple_spitem_display, R.id.simple_spdditem_display};
    
    EditText nameEditor;
    EditText symbolEditor;
    EditText noteEditor;
    Spinner positionEditor;
    
    
    Button okBtn;
    Button cancelBtn;
    
    private void initialEditor() {
        nameEditor = (EditText)findViewById(R.id.bookeditor_name);
        nameEditor.setText(workingBook.getName());
        
        symbolEditor = (EditText)findViewById(R.id.bookeditor_symbol);
        symbolEditor.setText(workingBook.getSymbol());
        
      //initial spinner
        positionEditor = (Spinner) findViewById(R.id.bookeditor_symbol_position);
        List<Map<String, Object>> data = new  ArrayList<Map<String, Object>>();
        SymbolPosition symbolPos = workingBook.getSymbolPosition();
        int selpos,i;
        selpos = i = -1;
        for (SymbolPosition sp : SymbolPosition.getAvailable()) {
            i++;
            Map<String, Object> row = new HashMap<String, Object>();
            data.add(row);
            row.put(spfrom[0], new NamedItem(spfrom[0],sp,sp.getDisplay(i18n)));
            
            if(sp.equals(symbolPos)){
                selpos = i;
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.simple_spitem, spfrom, spto);
        adapter.setDropDownViewResource(R.layout.simple_spdd);
        adapter.setViewBinder(new SymbolPositionViewBinder());
        positionEditor.setAdapter(adapter);
        if(selpos>-1){
            positionEditor.setSelection(selpos);
        }
        
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
      //verify
        if(Spinner.INVALID_POSITION==positionEditor.getSelectedItemPosition()){
            GUIs.shortToast(this,i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.label_symbol_position)));
            return;
        }
        
        String name = nameEditor.getText().toString().trim();
        if("".equals(name)){
            nameEditor.requestFocus();
            GUIs.alert(this,i18n.string(R.string.cmsg_field_empty,i18n.string(R.string.clabel_name)));
            return;
        }

        SymbolPosition pos = SymbolPosition.getAvailable()[positionEditor.getSelectedItemPosition()];
        
        //assign
        workingBook.setName(name);
        workingBook.setSymbol(symbolEditor.getText().toString().trim());
        workingBook.setNote(noteEditor.getText().toString().trim());
        workingBook.setSymbolPosition(pos);
        
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
    
    class SymbolPositionViewBinder implements SimpleAdapter.ViewBinder{
        @Override
        public boolean setViewValue(View view, Object data, String text) {
            
            NamedItem item = (NamedItem)data;
            String name = item.getName();
            if(!(view instanceof TextView)){
               return false;
            }
            if(Constants.DISPLAY.equals(name)){
                ((TextView)view).setText(item.getToString());
                return true;
            }
            return false;
        }
    }
}
