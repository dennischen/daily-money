package com.bottleworks.dailymoney;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.DuplicateKeyException;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.NamedItem;

/**
 * 
 * @author dennis
 *
 */
public class DetailListHelper implements OnItemClickListener,DetailEditorDialog.OnFinishListener {
    
private static String[] bindingFrom = new String[] { "layout","from", "to", "money" , "note", "date" };
    
    private static int[] bindingTo = new int[] { R.id.detlist_item_layout,R.id.detlist_item_from, R.id.detlist_item_to, R.id.detlist_item_money,R.id.detlist_item_note,R.id.detlist_item_date };
    
    
    private List<Detail> listViewData = new ArrayList<Detail>();
    
    private List<Map<String, Object>> listViewMapList = new ArrayList<Map<String, Object>>();

    private ListView listView;
    
    private SimpleAdapter listViewAdapter;
    
    private Map<String,Account> accountCache = new HashMap<String,Account>();
    
    private boolean editable;
    
    private OnDetailChangedListener listener;
    
    Activity activity;
    I18N i18n;
    public DetailListHelper(Activity context,I18N i18n,boolean editable,OnDetailChangedListener listener){
        this.activity = context;
        this.i18n = i18n;
        this.editable = editable;
        this.listener = listener;
    }
    
    
    
    public void setup(ListView listview){
        listViewAdapter = new SimpleAdapter(activity, listViewMapList, R.layout.detlist_item, bindingFrom, bindingTo);
        listViewAdapter.setViewBinder(new SimpleAdapter.ViewBinder(){
            
            AccountType last = null;
            @Override
            public boolean setViewValue(View view, Object data, String text) {
                NamedItem item = (NamedItem)data;
                String name = item.getName();
                Detail det = (Detail)item.getValue();

                if(name.equals(bindingFrom[0])){
                    
                    RelativeLayout layout = (RelativeLayout)view;
                    
                    Account fromAcc = accountCache.get(det.getFrom());
                    Account toAcc = accountCache.get(det.getTo());
                    int flag = 0;
                    if(toAcc!=null){
                        if(AccountType.EXPENSE.getType().equals(toAcc.getType())){
                            flag |= 1;
                        }else if(AccountType.ASSET.getType().equals(toAcc.getType())){
                            flag |= 4;
                        }
                    }
                    if(fromAcc!=null){
                        if(AccountType.INCOME.getType().equals(fromAcc.getType())){
                            flag |= 2;
                        }
                    }
                    if( (flag & 1) == 1){
                        layout.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_expense));
                        last = AccountType.EXPENSE;
                    }else if( (flag & 2) == 2){
                        layout.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_income));
                        last = AccountType.INCOME;
                    }else if( (flag & 4) == 4){
                        layout.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_asset));
                        last = AccountType.ASSET;
                    }else{
                        layout.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_unknow));
                        last = null;
                    }
                    layout.getBackground().setState(new int[]{});
                    return true;
                }
                
                if(!(view instanceof TextView)){
                   return false;
                }
                
                if(AccountType.INCOME.equals(last)){
                   ((TextView)view).setTextColor(activity.getResources().getColor(R.color.income_fg));
                   if("money".equals(name)){
                       ((TextView)view).setText("+"+text);
                       return true;
                   }
                }else if(AccountType.ASSET.equals(last)){
                    ((TextView)view).setTextColor(activity.getResources().getColor(R.color.asset_fg)); 
                }else if(AccountType.EXPENSE.equals(last)){
                    ((TextView)view).setTextColor(activity.getResources().getColor(R.color.expense_fg));
                    if("money".equals(name)){
                        ((TextView)view).setText("-"+text);
                        return true;
                    }
                }else{
                    ((TextView)view).setTextColor(activity.getResources().getColor(R.color.unknow_fg));
                }
                
                return false;
            }});
        
        listView = listview;
        listView.setAdapter(listViewAdapter);
        if(editable){
            listView.setOnItemClickListener(this);
        }
        
        IDataProvider idp = Contexts.instance().getDataProvider();
        for(Account acc:idp.listAccount(null)){
            accountCache.put(acc.getId(),acc);
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        //click for editor
        if(parent == listView){
            doEditDetail(pos);
        }
    }
    
    public void doEditDetail(int pos) {
        Detail det = (Detail) listViewData.get(pos);
        DetailEditorDialog dlg = new DetailEditorDialog(activity, this, false, det);
        dlg.show();
    }
    
//    public doCreateDetail(){
//        
//    }
//    
//    public doCopyDetail(){
//        
//    }
    
    
    public void reloadData(List<Detail> data) {
        listViewData = data;;
        listViewMapList.clear();
        DateFormat format = Contexts.instance().getDateFormat();
        for (Detail det : listViewData) {
            Map<String, Object> row = new HashMap<String, Object>();
            listViewMapList.add(row);
            Account fromAcc = accountCache.get(det.getFrom());
            Account toAcc = accountCache.get(det.getTo());
            
            String from = fromAcc==null?det.getFrom():(i18n.string(R.string.label_detlist_from,fromAcc.getName(),AccountType.getDisplay(i18n, fromAcc.getType())));
            String to = toAcc==null?det.getTo():(i18n.string(R.string.label_detlist_to,toAcc.getName(),AccountType.getDisplay(i18n, toAcc.getType())));
            String money = i18n.string(R.string.label_detlist_money,Formats.double2String(det.getMoney()));
            row.put(bindingFrom[0], new NamedItem(bindingFrom[0],det,bindingFrom[0]));
            row.put(bindingFrom[1], new NamedItem(bindingFrom[1],det,from));
            row.put(bindingFrom[2], new NamedItem(bindingFrom[2],det,to));
            row.put(bindingFrom[3], new NamedItem(bindingFrom[3],det,money));
            row.put(bindingFrom[4], new NamedItem(bindingFrom[4],det,det.getNote()));
            row.put(bindingFrom[5], new NamedItem(bindingFrom[5],det,format.format(det.getDate())));
        }

        listViewAdapter.notifyDataSetChanged();
    }



    @Override
    public boolean onFinish(DetailEditorDialog dlg, View v, Object data) {
        switch (v.getId()) {
        case R.id.deteditor_ok:
            Detail workingdet = (Detail)data;
            boolean modeCreate = dlg.isModeCreate();
            IDataProvider idp = Contexts.instance().getDataProvider();
            if (modeCreate) {
                if(v.getId()==R.id.deteditor_ok){
                    Detail dt = (Detail)data;
                    Contexts.instance().getDataProvider().newDetail(dt);
                }else if(v.getId()==R.id.deteditor_close){
                    GUIs.shortToast(activity,i18n.string(R.string.msg_created_detail,dlg.getCounter()));
                }
            } else {
                Detail odet = dlg.getDetail();
                idp.updateDetail(odet.getId(),workingdet);
            }
            if(listener!=null){
                listener.onDetailChanged(workingdet);
            }
        }
        return true;
    }
   

    public void doNewAccount() {
        Detail d = new Detail("","",new Date(),0D,"");
        DetailEditorDialog dlg = new DetailEditorDialog(activity,this, true, d);
        dlg.show();
    }



    public void doEditAccount(int pos) {
        Detail d = (Detail) listViewData.get(pos);
        DetailEditorDialog dlg = new DetailEditorDialog(activity,this, false, d);
        dlg.show();
    }



    public void doDeleteAccount(int pos) {
        Detail d = (Detail) listViewData.get(pos);
        Contexts.instance().getDataProvider().deleteDetail(d.getId());
        if(listener!=null){
            listener.onDetailDeleted(d);
        }
    }



    public void doCopyAccount(int pos) {
        Detail d = (Detail) listViewData.get(pos);
        DetailEditorDialog dlg = new DetailEditorDialog(activity,this, true, d);
        dlg.show();
    }
    
    public static interface OnDetailChangedListener {
        public void onDetailChanged(Detail detail);
        public void onDetailDeleted(Detail detail);
    }
    
    public static class OnDetailChangedAdapter implements OnDetailChangedListener{

        @Override
        public void onDetailChanged(Detail detail) {
        }
        @Override
        public void onDetailDeleted(Detail detail) {
        }
    }
    
}
