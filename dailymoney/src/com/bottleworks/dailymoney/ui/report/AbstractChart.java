package com.bottleworks.dailymoney.ui.report;

import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.bottleworks.commons.util.I18N;

import android.content.Context;

/**
 * 
 * @author dennis
 * 
 */
public abstract class AbstractChart {

    final protected Context context;

    final protected float dpRatio;

    final protected I18N i18n;

    final protected int orientation;

    public static final int RED = 0xD0FF0000;
    public static final int GREEN = 0xD000FF00;
    public static final int BLUE = 0xD00000FF;
    public static final int YELLOW = 0xD0FFFF00;
    public static final int CYAN = 0xD000FFFF;
    public static final int MAGENTA = 0xD0FF00FF;
    public static final int ORANGE = 0xD0FF8C66;
    

    public static final int RED1 = 0xA0FF7777;
    public static final int GREEN1 = 0xA077FF77;
    public static final int BLUE1 = 0xA07777FF;
    public static final int YELLOW1 = 0xA0FFFF77;
    public static final int CYAN1 = 0xA077FFFF;
    public static final int MAGENTA1 = 0xA0FF77FF;
    public static final int ORANGE1 = 0xD0FF8C66;

    public static final int[] colorPool = new int[] { GREEN, ORANGE, BLUE, RED, YELLOW, CYAN, MAGENTA, GREEN1, ORANGE1, BLUE1, RED1,
            YELLOW1, CYAN1, MAGENTA1 };

    public static final PointStyle[] pointPool = new PointStyle[] { PointStyle.CIRCLE,PointStyle.DIAMOND,PointStyle.TRIANGLE,PointStyle.SQUARE,PointStyle.X};
    
    public AbstractChart(Context context, int orientation, float dpRatio) {
        this.context = context;
        this.dpRatio = dpRatio;
        i18n = new I18N(context);
        this.orientation = orientation;
    }
    
    public PointStyle[] createPointStyle(int size) {
        PointStyle[] point = new PointStyle[size];
        if (size == 0)
            return point;
        int i = 0;
        int cmindex = 0;
        while (true) {
            cmindex = i % pointPool.length;
            point[i] = pointPool[cmindex];
            i++;
            if (i == point.length) {
                break;
            }

        }
        return point;
    }

    public int[] createColor(int size) {
        int[] color = new int[size];
        if (size == 0)
            return color;
        int i = 0;
        int cmindex = 0;
        int trans = 0xFFFFFFFF;
        while (true) {
            cmindex = i % colorPool.length;
            if (i != 0 && cmindex == 0) {
                trans = trans - 0x40000000;// increase transparent
            }
            color[i] = colorPool[cmindex];
            color[i] = color[i] & trans;
            i++;
            if (i == color.length) {
                break;
            }

        }
        return color;
    }

    protected DefaultRenderer buildCategoryRenderer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle,
            double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

}
