package com.bottleworks.dailymoney.reports;

import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

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
    
    

    public static final int RED         = 0xEFFF0000;
    public static final int GREEN       = 0xEF00FF00;
    public static final int BLUE        = 0xEF0000FF;
    public static final int YELLOW      = 0xEFFFFF00;
    public static final int CYAN        = 0xEF00FFFF;
    public static final int MAGENTA     = 0xEFFF00FF;
    
    public static final int RED1         = 0xDFFF7777;
    public static final int GREEN1       = 0xDF77FF77;
    public static final int BLUE1        = 0xDF7777FF;
    public static final int YELLOW1      = 0xDFFFFF77;
    public static final int CYAN1        = 0xDF77FFFF;
    public static final int MAGENTA1     = 0xDFFF77FF;
    
    
    
    public static final int[] colorPool = new int[]{GREEN,BLUE,RED,YELLOW,CYAN,MAGENTA,
        GREEN1,BLUE1,RED1,YELLOW1,CYAN1,MAGENTA1};

    public AbstractChart(Context context, float dpRatio) {
        this.context = context;
        this.dpRatio = dpRatio;
        i18n = new I18N(context);
    }
    
    public int[] createColor(int size){
        int[] color = new int[size];
        if(size==0) return color;
        int i=0;
        int cmindex=0;
        int trans=0xFFFFFFFF;
        while(true){
            cmindex = i % colorPool.length;
            if(i!=0 && cmindex==0){
                trans = trans-0x40000000;//increase transparent
            }
            color[i] = colorPool[cmindex];
            color[i] = color[i]&trans;
            i++;
            if(i==color.length){
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

}
