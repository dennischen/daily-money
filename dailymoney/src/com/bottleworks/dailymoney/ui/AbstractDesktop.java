package com.bottleworks.dailymoney.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.core.R;

import java.util.HashMap;

/**
 * @author dennis
 */
public abstract class AbstractDesktop extends Desktop implements AdapterView.OnItemClickListener {
    protected I18N i18n;
    private GridView gridView;
    private DesktopItem lastClickedItem;
    private HashMap<Object, DesktopItem> dtHashMap = new HashMap<>();

    public AbstractDesktop() {
        this.i18n = Contexts.instance().getI18n();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.desktop_fragment, container, false);

        DesktopItemAdapter gridViewAdapter = new DesktopItemAdapter();
        this.gridView = (GridView) fragment.findViewById(R.id.dt_grid);
        this.gridView.setAdapter(gridViewAdapter);
        this.gridView.setOnItemClickListener(this);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.init(context);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        //item clicked in grid view
        if (parent == gridView) {
            DesktopItem di = dtHashMap.get(view);
            if (di != null) {
                lastClickedItem = di;
                lastClickedItem.run();
            }
        }
    }

    public void onMenuItemClick(DesktopItem dtitem) {
        lastClickedItem = dtitem;
        lastClickedItem.run();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (lastClickedItem != null) {
            lastClickedItem.onActivityResult(requestCode, resultCode, data);
        }
    }

    abstract protected void init(Context context);

    public class DesktopItemAdapter extends BaseAdapter {

        public int getCount() {
            return getVisibleItems().size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;
            TextView tv;
            LinearLayout view;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                view = new LinearLayout(AbstractDesktop.this.getActivity());
                view.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
                GUIs.inflateView(AbstractDesktop.this.getActivity(), view, R.layout.desktop_item);
            } else {
                view = (LinearLayout) convertView;
            }

            iv = (ImageView) view.findViewById(R.id.dt_icon);
            tv = (TextView) view.findViewById(R.id.dt_label);

            DesktopItem item = getVisibleItems().get(position);
            iv.setImageResource(item.getIcon());
            tv.setText(item.getLabel());
            dtHashMap.put(view, item);
            return view;
        }
    }

}
