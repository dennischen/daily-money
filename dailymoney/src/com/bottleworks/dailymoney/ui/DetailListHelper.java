package com.bottleworks.dailymoney.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;

/**
 * 
 * @author dennis
 *
 */
public class DetailListHelper implements OnItemClickListener{
    
    private static String[] bindingFrom = new String[] { "layout","layout_inner","from", "to", "money" , "note", "date" };
    
    private static int[] bindingTo = new int[] { R.id.detlist_item_layout,R.id.detlist_item_layout_inner,R.id.detlist_item_from, R.id.detlist_item_to, R.id.detlist_item_money,R.id.detlist_item_note,R.id.detlist_item_date };
    
    
    private List<Detail> listViewData = new ArrayList<Detail>();
    
    private List<Map<String, Object>> listViewMapList = new ArrayList<Map<String, Object>>();

    private ListView listView;
    
    private SimpleAdapter listViewAdapter;
    
    private Map<String,Account> accountCache = new HashMap<String,Account>();
    
    private boolean clickeditable;
    
    private OnDetailListener listener;
    
    private Activity activity;
    private I18N i18n;
    private CalendarHelper calHelper;
    public DetailListHelper(Activity activity,I18N i18n,CalendarHelper calHelper, boolean clickeditable,OnDetailListener listener){
        this.activity = activity;
        this.i18n = i18n;
        this.clickeditable = clickeditable;
        this.listener = listener;
        this.calHelper = calHelper;
    }
    
    
    
    public void setup(ListView listview){
        
        int layout = 0;
        switch(Contexts.instance().getPrefDetailListLayout()){
        case 2:
            layout = R.layout.detlist_item2;
            break;
        case 3:
            layout = R.layout.detlist_item3;
            break;
        case 4:
            layout = R.layout.detlist_item4;
            break;
        default:
            layout = R.layout.detlist_item1;
        }
        
        listViewAdapter = new SimpleAdapter(activity, listViewMapList, layout, bindingFrom, bindingTo);
        listViewAdapter.setViewBinder(new ListViewBinder());
        
        listView = listview;
        listView.setAdapter(listViewAdapter);
        if(clickeditable){
            listView.setOnItemClickListener(this);
        }
        
        IDataProvider idp = Contexts.instance().getDataProvider();
        for(Account acc:idp.listAccount(null)){
            accountCache.put(acc.getId(),acc);
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if(parent == listView){
            doEditDetail(pos);
        }
    }
    
    private DateFormat dayOfWeekFormat = new SimpleDateFormat("E"); 
    public void reloadData(List<Detail> data) {
        listViewData = data;;
        listViewMapList.clear();
        DateFormat dateFormat = Contexts.instance().getDateFormat();//for 2010/01/01
        for (Detail det : listViewData) {
            Map<String, Object> row = toDetailMap(det,dateFormat);
            listViewMapList.add(row);
        }

        listViewAdapter.notifyDataSetChanged();
    }

    private Map<String, Object> toDetailMap(Detail det,DateFormat format){
        Map<String, Object> row = new HashMap<String, Object>();
        Account fromAcc = accountCache.get(det.getFrom());
        Account toAcc = accountCache.get(det.getTo());
        
        String from = fromAcc==null?det.getFrom():(i18n.string(R.string.label_detlist_from,fromAcc.getName(),AccountType.getDisplay(i18n, fromAcc.getType())));
        String to = toAcc==null?det.getTo():(i18n.string(R.string.label_detlist_to,toAcc.getName(),AccountType.getDisplay(i18n, toAcc.getType())));
        String money = Formats.money2String(det.getMoney());
        row.put(bindingFrom[0], new NamedItem(bindingFrom[0],det,bindingFrom[0]));
        row.put(bindingFrom[1], new NamedItem(bindingFrom[1],det,bindingFrom[1]));
        row.put(bindingFrom[2], new NamedItem(bindingFrom[2],det,from));
        row.put(bindingFrom[3], new NamedItem(bindingFrom[3],det,to));
        row.put(bindingFrom[4], new NamedItem(bindingFrom[4],det,money));
        row.put(bindingFrom[5], new NamedItem(bindingFrom[5],det,det.getNote()));
        row.put(bindingFrom[6], new NamedItem(bindingFrom[6],det,format.format(det.getDate())+" "+dayOfWeekFormat.format(det.getDate())+","));
        
        return row;
    }

    public void doNewDetail() {
        Detail d = new Detail("","",new Date(),0D,"");
        Intent intent = null;
        intent = new Intent(activity,DetailEditorActivity.class);
        intent.putExtra(DetailEditorActivity.INTENT_MODE_CREATE,true);
        intent.putExtra(DetailEditorActivity.INTENT_DETAIL,d);
        activity.startActivityForResult(intent,Constants.REQUEST_DETAIL_EDITOR_CODE);
    }



    public void doEditDetail(int pos) {
        Detail d = (Detail) listViewData.get(pos);
        Intent intent = null;
        intent = new Intent(activity,DetailEditorActivity.class);
        intent.putExtra(DetailEditorActivity.INTENT_MODE_CREATE,false);
        intent.putExtra(DetailEditorActivity.INTENT_DETAIL,d);
        activity.startActivityForResult(intent,Constants.REQUEST_DETAIL_EDITOR_CODE);
    }

    public void doDeleteDetail(int pos) {
        Detail d = (Detail) listViewData.get(pos);
        Contexts.instance().getDataProvider().deleteDetail(d.getId());
        if(listener!=null){
            listener.onDetailDeleted(d);
        }else{
            listViewData.remove(pos);
            listViewMapList.remove(pos);
            listViewAdapter.notifyDataSetChanged();
        }
    }



    public void doCopyDetail(int pos) {
        Detail d = (Detail) listViewData.get(pos);
        Intent intent = null;
        intent = new Intent(activity,DetailEditorActivity.class);
        intent.putExtra(DetailEditorActivity.INTENT_MODE_CREATE,true);
        intent.putExtra(DetailEditorActivity.INTENT_DETAIL,d);
        activity.startActivityForResult(intent,Constants.REQUEST_DETAIL_EDITOR_CODE);
    }
    
    
    public static interface OnDetailListener {
        public void onDetailDeleted(Detail detail);
    }
    
    class ListViewBinder implements SimpleAdapter.ViewBinder{
        AccountType last = null;
        AccountType lastFrom = null;
        AccountType lastTo = null;
        @Override
        public boolean setViewValue(View view, Object data, String text) {
            NamedItem item = (NamedItem)data;
            String name = item.getName();
            Detail det = (Detail)item.getValue();

            if("layout".equals(name)){
                
                RelativeLayout layout = (RelativeLayout)view;
                
                Account fromAcc = accountCache.get(det.getFrom());
                Account toAcc = accountCache.get(det.getTo());
                int flag = 0;
                if(toAcc!=null){
                    if(AccountType.EXPENSE.getType().equals(toAcc.getType())){
                        flag |= 1;
                    }else if(AccountType.ASSET.getType().equals(toAcc.getType())){
                        flag |= 4;
                    }else if(AccountType.LIABILITY.getType().equals(toAcc.getType())){
                        flag |= 8;
                    }else if(AccountType.OTHER.getType().equals(toAcc.getType())){
                        flag |= 16;
                    }
                    lastTo = AccountType.find(toAcc.getType());
                }else{
                    lastTo = AccountType.UNKONW;
                }
                if(fromAcc!=null){
                    if(AccountType.INCOME.getType().equals(fromAcc.getType())){
                        flag |= 2;
                    }
                    lastFrom = AccountType.find(fromAcc.getType());
                }else{
                    lastFrom = AccountType.UNKONW;
                }
                int drawid;
                if( (flag & 1) == 1){//expense
                    drawid = R.drawable.selector_expense;
                    last = AccountType.EXPENSE;
                }else if( (flag & 2) == 2){//income
                    drawid = R.drawable.selector_income;
                    last = AccountType.INCOME;
                }else if( (flag & 4) == 4){
                    drawid = R.drawable.selector_asset;
                    last = AccountType.ASSET;
                }else if( (flag & 8) == 8){
                    drawid = R.drawable.selector_liability;
                    last = AccountType.LIABILITY;
                }else if( (flag & 16) == 16){
                    drawid = R.drawable.selector_other;
                    last = AccountType.OTHER;
                }else{
                    drawid = R.drawable.selector_unknow;
                    last = null;
                }
                Drawable draw = activity.getResources().getDrawable(drawid);
                layout.setBackgroundDrawable(draw);
                return true;
            }
            
            
            if("layout_inner".equals(name)){
                //make it light
                LinearLayout inner = (LinearLayout)view;
                Date now = new Date();
                Drawable draw = null;
                if(calHelper.isSameDay(now, det.getDate())){
                    draw = new ColorDrawable(0x33FFFFFF);
                }else if(calHelper.isFutureDay(now, det.getDate())){
                    draw = new ColorDrawable(0x66FFFFFF);
                }
                inner.setBackgroundDrawable(draw);
                return true;
            }
            
            if(!(view instanceof TextView)){
               return false;
            }
            
            if(AccountType.INCOME.equals(last)){
               ((TextView)view).setTextColor(activity.getResources().getColor(R.color.income_fg));
//               if("money".equals(name)){
//                   ((TextView)view).setText(text);
//                   return true;
//               }
            }else if(AccountType.ASSET.equals(last)){
                ((TextView)view).setTextColor(activity.getResources().getColor(R.color.asset_fg)); 
            }else if(AccountType.EXPENSE.equals(last)){
                ((TextView)view).setTextColor(activity.getResources().getColor(R.color.expense_fg));
//                if("money".equals(name)){
//                    ((TextView)view).setText(text);
//                    return true;
//                }
            }else if(AccountType.LIABILITY.equals(last)){
                ((TextView)view).setTextColor(activity.getResources().getColor(R.color.liability_fg)); 
            }else if(AccountType.OTHER.equals(last)){
                ((TextView)view).setTextColor(activity.getResources().getColor(R.color.other_fg)); 
            }else{
                ((TextView)view).setTextColor(activity.getResources().getColor(R.color.unknow_fg));
            }
            
            
            
            if("from".equals(name)){
                view.setBackgroundDrawable(null);
                if(last!=lastFrom){
                    if(AccountType.INCOME== lastFrom){
                        view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_income));
                    }else if(AccountType.EXPENSE== lastFrom){
                        view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_expense));
                    }else if(AccountType.ASSET== lastFrom){
                        view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_asset));
                    }else if(AccountType.LIABILITY== lastFrom){
                        view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_liability));
                    }else if(AccountType.OTHER== lastFrom){
                        view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_other));
                    }else{
                        view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_unknow));
                    }
                }
            }
            
            if("to".equals(name)){
                view.setBackgroundDrawable(null);
                if(AccountType.INCOME == lastFrom){
                    if(AccountType.ASSET== lastTo){
                        view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_asset));
                    }else if(AccountType.LIABILITY== lastTo){
                        view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_liability));
                    }else if(AccountType.OTHER== lastTo){
                        view.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selector_other));
                    }
                }
            }
            
            
            return false;
        }
    }
    
}
