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
    
    public static final int RED1         = 0xEFFF5555;
    public static final int GREEN1       = 0xEF55FF55;
    public static final int BLUE1        = 0xEF5555FF;
    public static final int YELLOW1      = 0xEFFFFF55;
    public static final int CYAN1        = 0xEF55FFFF;
    public static final int MAGENTA1     = 0xEFFF55FF;
    
    public static final int RED2         = 0xEFFFAAAA;
    public static final int GREEN2       = 0xEFAAFFAA;
    public static final int BLUE2        = 0xEFAAAAFF;
    public static final int YELLOW2      = 0xEFFFFFAA;
    public static final int CYAN2        = 0xEFAAFFFF;
    public static final int MAGENTA2     = 0xEFFFAAFF;
    
    
    public static final int[] colorMaterial = new int[]{GREEN,BLUE,RED,YELLOW,CYAN,MAGENTA,
        GREEN1,BLUE1,RED1,YELLOW1,CYAN1,MAGENTA1,
        GREEN2,BLUE2,RED2,YELLOW2,CYAN2,MAGENTA2};

    public AbstractChart(Context context, float dpRatio) {
        this.context = context;
        this.dpRatio = dpRatio;
        i18n = new I18N(context);
    }
    
    public int[] createColorArray(int size){
        int[] carr = new int[size];
        int i=0;
        int cmindex=0;
        int trans=0xFFFFFFFF;
        while(true){
            cmindex = i % colorMaterial.length;
            if(i!=0 && cmindex==0){
                trans = trans-0x40000000;//increase transparent
                System.out.println("trans "+Integer.toHexString(trans));
            }
            carr[i] = colorMaterial[ cmindex];
            carr[i] = carr[i]&trans;
            i++;
            if(i==carr.length){
                break;
            }
            
            
        }
        return carr;
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
