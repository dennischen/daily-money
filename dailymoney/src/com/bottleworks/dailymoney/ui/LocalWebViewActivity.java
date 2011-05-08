package com.bottleworks.dailymoney.ui;

import android.os.Bundle;
import android.webkit.WebView;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
/**
 * 
 * @author dennis
 *
 */
public class LocalWebViewActivity extends ContextsActivity {
    
    public static final String INTENT_URI = "uri";
    public static final String INTENT_URI_ID = "uriid";
    public static final String INTENT_TITLE = "title";
    
    WebView webView;
    
    
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.webview);
        
        webView = (WebView)findViewById(R.id.webview);
        
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSCallHandler(),"dmctrl");
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); 
        
        initInit();
        
        
    }
    
    private void initInit() {
        Bundle bundle = getIntentExtras();
        String uri = null;
        int rid = bundle.getInt(INTENT_URI_ID,-1);
        if(rid!=-1){
            uri = i18n.string(rid);
        }else{
            uri = bundle.getString(INTENT_URI);
        }
        
        String title = bundle.getString(INTENT_TITLE);
        if(title!=null){
            this.setTitle(title);
        }
        
        webView.loadUrl(Constants.LOCAL_URL_PREFIX+uri);
    }

    private void onLinkClicked(final String path){
       webView.loadUrl(Constants.LOCAL_URL_PREFIX+path);
    }
    
    class JSCallHandler {
        public void onLinkClicked(final String path){
            GUIs.post(new Runnable(){
                public void run() {
                    LocalWebViewActivity.this.onLinkClicked(path);
                }});
        }
    }
}
