package com.bottleworks.dailymoney.ui.report;

import java.text.DecimalFormat;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.content.Intent;

import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Balance;

public class BalancePieChart extends AbstractChart {

    DecimalFormat percentageFormat = new DecimalFormat("##0");

    public BalancePieChart(Context context, int orientation, float dpRatio) {
        super(context, orientation, dpRatio);
    }

    public Intent createIntent(AccountType at, List<Balance> balances) {
        double total = 0;
        for (Balance b : balances) {
            total += b.getMoney() <= 0 ? 0 : b.getMoney();
        }
        CategorySeries series = new CategorySeries(at.getDisplay(i18n));
        for (Balance b : balances) {
            if (b.getMoney() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(b.getName());
                double p = (b.getMoney() * 100) / total;
                if (p >= 1) {
                    sb.append("(").append(percentageFormat.format(p)).append("%)");
                    series.add(sb.toString(), b.getMoney());
                }
            }
        }
        int[] color = createColor(series.getItemCount());
        DefaultRenderer renderer = buildCategoryRenderer(color);
        renderer.setLabelsTextSize(14 * dpRatio);
        renderer.setLegendTextSize(16 * dpRatio);
        return ChartFactory.getPieChartIntent(context, series, renderer, series.getTitle());
    }
}
