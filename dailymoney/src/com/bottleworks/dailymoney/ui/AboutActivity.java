package com.bottleworks.dailymoney.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
/**
 * 
 * @author dennis
 *
 */
public class AboutActivity extends ContextsActivity {
    
    WebView whatsnew;
    WebView aboutapp;
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.about);
        
        whatsnew = (WebView)findViewById(R.id.about_whatsnew);
        aboutapp = (WebView)findViewById(R.id.about_app);
        
        whatsnew.getSettings().setAllowFileAccess(true);
        whatsnew.getSettings().setJavaScriptEnabled(true);
        whatsnew.addJavascriptInterface(new JSCallHandler(),"dmctrl");
        whatsnew.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); 
        
        aboutapp.getSettings().setAllowFileAccess(true);
        aboutapp.getSettings().setJavaScriptEnabled(true);
        aboutapp.addJavascriptInterface(new JSCallHandler(),"dmctrl");
        aboutapp.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); 
        
        
        whatsnew.loadUrl(Constants.LOCAL_URL_PREFIX+i18n.string(R.string.path_what_is_new));
        aboutapp.loadUrl(Constants.LOCAL_URL_PREFIX+i18n.string(R.string.path_about_app));
    }
    
    private void onLinkClicked(final String path){
       whatsnew.setVisibility(View.GONE);
       aboutapp.loadUrl(Constants.LOCAL_URL_PREFIX+path);
    }
    
    class JSCallHandler {
        public void onLinkClicked(final String path){
            GUIs.post(new Runnable(){
                public void run() {
                    AboutActivity.this.onLinkClicked(path);
                }});
        }
    }
}
