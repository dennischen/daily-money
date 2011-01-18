package com.bottleworks.dailymoney.reports;

import java.text.DecimalFormat;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.content.Intent;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.data.AccountType;

public class BalancePieChart extends AbstractChart {

    DecimalFormat percentageFormat = new DecimalFormat("##0");
    
    public BalancePieChart(Context context, float dpRatio) {
        super(context, dpRatio);
    }

    public Intent createIntent(AccountType at,List<Balance> balances) {
        double total = 0;
        for(Balance b : balances){
            total += b.money<=0?0:b.money;
        }
        CategorySeries series = new CategorySeries(at.getDisplay(i18n));
        for(Balance b : balances){
            if(b.money>0){
                StringBuilder sb = new StringBuilder();
                sb.append(b.getName());
                double p = (b.money*100)/total;
                if(p>0){
                    sb.append("(").append(percentageFormat.format(p)).append("%)");
                    series.add(sb.toString(),b.money>0?b.money:0);
                }
            }
        }
        int[] colors = createColorArray(series.getItemCount());
        DefaultRenderer renderer = buildCategoryRenderer(colors);
        renderer.setLabelsTextSize(14 * dpRatio);
        renderer.setLegendTextSize(12 * dpRatio);//font height bug of achartengine the font size could not large than 12
        return ChartFactory.getPieChartIntent(context,series , renderer);
    }
}
