package com.bottleworks.dailymoney;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bottleworks.commons.util.Formats;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.ContextsActivity;
import com.bottleworks.dailymoney.ui.NamedItem;

/**
 * 
 * @author dennis
 * 
 */
public class DetailListActivity extends ContextsActivity implements OnItemClickListener {

    
    private static String[] bindingFrom = new String[] { "layout","from", "to", "money" , "note", "date" };
    
    private static int[] bindingTo = new int[] { R.id.detlist_item_layout,R.id.detlist_item_from, R.id.detlist_item_to, R.id.detlist_item_money,R.id.detlist_item_note,R.id.detlist_item_date };
    
    
    private List<Detail> listViewData = new ArrayList<Detail>();
    
    private List<Map<String, Object>> listViewMapList = new ArrayList<Map<String, Object>>();

    private ListView listView;
    
    private SimpleAdapter listViewAdapter;
    
    private Map<String,Account> accountCache = new HashMap<String,Account>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detlist);
        initialContent();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }


    private void initialContent() {
        listViewAdapter = new SimpleAdapter(this, listViewMapList, R.layout.detlist_item, bindingFrom, bindingTo);
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
                        layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_expense));
                        last = AccountType.EXPENSE;
                    }else if( (flag & 2) == 2){
                        layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_income));
                        last = AccountType.INCOME;
                    }else if( (flag & 4) == 4){
                        layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_asset));
                        last = AccountType.ASSET;
                    }else{
                        layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_unknow));
                        last = null;
                    }
                    layout.getBackground().setState(new int[]{});
                    return true;
                }
                
                if(!(view instanceof TextView)){
                   return false;
                }
                
                if(AccountType.INCOME.equals(last)){
                   ((TextView)view).setTextColor(getResources().getColor(R.color.income_fg));
                   if("money".equals(name)){
                       ((TextView)view).setText("+"+text);
                       return true;
                   }
                }else if(AccountType.ASSET.equals(last)){
                    ((TextView)view).setTextColor(getResources().getColor(R.color.asset_fg)); 
                }else if(AccountType.EXPENSE.equals(last)){
                    ((TextView)view).setTextColor(getResources().getColor(R.color.expense_fg));
                    if("money".equals(name)){
                        ((TextView)view).setText("-"+text);
                        return true;
                    }
                }else{
                    ((TextView)view).setTextColor(getResources().getColor(R.color.unknow_fg));
                }
                
                return false;
            }});
        
        listView = (ListView) findViewById(R.id.detlist_list);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
    }
    

    private void reloadData() {
        IDataProvider idp = Contexts.instance().getDataProvider();
        
        accountCache.clear();
        for(Account acc:idp.listAccount(null)){
            accountCache.put(acc.getId(),acc);
        }
        
        listViewData = null;

        listViewData = idp.listAllDetail();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub 
    }

}
