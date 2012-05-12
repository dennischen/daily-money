package com.bottleworks.dailymoney.ui.report;

import java.math.BigDecimal;
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
        BigDecimal total = BigDecimal.ZERO;
        for (Balance b : balances) {
            total = total.add(b.getMoney().compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : b.getMoney());
        }
        CategorySeries series = new CategorySeries(at.getDisplay(i18n));
        for (Balance b : balances) {
            if (b.getMoney().compareTo(BigDecimal.ZERO) > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(b.getName());
                BigDecimal p = b.getMoney().multiply(new BigDecimal("100")).divide(total);
                if (p.compareTo(BigDecimal.ONE) >= 0) {
                    sb.append("(").append(percentageFormat.format(p)).append("%)");
                    series.add(sb.toString(), b.getMoney().doubleValue());
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
