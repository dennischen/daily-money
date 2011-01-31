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
        whatsnew.addJavascriptInterface(new JSCallHandler(),"aboutView");
        whatsnew.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); 
        
        aboutapp.getSettings().setAllowFileAccess(true);
        aboutapp.getSettings().setJavaScriptEnabled(true);
        aboutapp.addJavascriptInterface(new JSCallHandler(),"aboutView");
        aboutapp.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); 
        
        
        whatsnew.loadUrl("file:///android_asset/"+i18n.string(R.string.path_what_is_new));
        aboutapp.loadUrl("file:///android_asset/"+i18n.string(R.string.path_about_app));
    }
    
    private void onLinkClicked(String path){
        GUIs.shortToast(this,path);
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
