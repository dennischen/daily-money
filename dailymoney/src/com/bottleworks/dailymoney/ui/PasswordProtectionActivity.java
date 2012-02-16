package com.bottleworks.dailymoney.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;

/**
 * 
 * @author dennis
 *
 */
public class PasswordProtectionActivity extends ContextsActivity implements OnClickListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdprotection);
        findViewById(R.id.pdprot_ok).setOnClickListener(this);
    }
    

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pdprot_ok) {
            doPasswordOk();
        }
    }

    private void doPasswordOk() {
        String password = getContexts().getPrefPassword();
        String pd = ((TextView)findViewById(R.id.pdprot_text)).getText().toString();
        if(password.equals(pd)){
           setResult(RESULT_OK);
           finish();
        }else{
            GUIs.shortToast(this,R.string.msg_wrong_password);
        }
    }
}
