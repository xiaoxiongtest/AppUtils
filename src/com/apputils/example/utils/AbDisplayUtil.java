package com.apputils.example.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

/**
 * 获取屏幕信息
 */
public class AbDisplayUtil {
    public static int width = 480,height = 800, index = 0;
    
    /**
     * 手机的分辨率
     */
    public static float density = 1.5f;
    
    /**
     * 判断是否初始化过
     */
    private static boolean ischeck = false;
    
    private static TypedValue mTmpValue = new TypedValue();
    
    /**
     * 获取手机的宽度
     * @param ctx
     * @return
     */
    public static int getScreenWidth(Context ctx) {
        if (!ischeck) {
            init(ctx);
        }
        return width;
    }
    
    /** 
     * 获取屏幕分辨率
     */
    public static String getScreenResolution(Context ctx) {
        if (!ischeck) {
            init(ctx);
        }
        return width + "x" + height;
    }
    /**
     * 将手机的分辨率，宽高都获取变成全局变量
     * @param ctx
     */
    public static void init(Context ctx) {
        if (ctx instanceof Activity) {
            Activity activ = (Activity) ctx;
            Display dis = activ.getWindowManager().getDefaultDisplay();
            DisplayMetrics dm = activ.getResources().getDisplayMetrics();
            density = dm.density;
            width = dis.getWidth();
            height = dis.getHeight();
            ischeck = true;
        }
    }
    /**
     * 获取手机的高度
     * @param ctx
     * @return
     */
    public static int getScreenHeight(Context ctx) {
        if (!ischeck) {
            init(ctx);
        }
        return height;
    }
    
    /**
    * dip转换为px.
    */
    public static float dip2px(Context context, float dipValue) {
        DisplayMetrics mDisplayMetrics = AbAppUtil.getDisplayMetrics(context);
        return applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, mDisplayMetrics);
    }
    
    /**
     * 描述：px转换为dip.
     *
     */
    public static float px2dip(Context context, float pxValue) {
        DisplayMetrics mDisplayMetrics = AbAppUtil.getDisplayMetrics(context);
        return pxValue / mDisplayMetrics.density;
    }
    
    /**
     * 描述：sp转换为px.
     *
     */
    public static float sp2px(Context context, float spValue) {
        DisplayMetrics mDisplayMetrics = AbAppUtil.getDisplayMetrics(context);
        return applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, mDisplayMetrics);
    }
    
    /**
     * 描述：px转换为sp.
     *
     */
    public static float px2sp(Context context, float pxValue) {
        DisplayMetrics mDisplayMetrics = AbAppUtil.getDisplayMetrics(context);
        return pxValue / mDisplayMetrics.scaledDensity;
    }
    
    /**
     * TypedValue官方源码中的算法，任意单位转换为PX单位
     */
    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f / 72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }
    
    /**
     * 描述：获取xml中定义大小
     *
     * @param context the context
     * @param id 控件ID
     * @return 对应单位的值
     */
    public static int getXmlDef(Context context, int id) {
        synchronized (mTmpValue) {
            TypedValue value = mTmpValue;
            context.getResources().getValue(id, value, true);
            return (int) TypedValue.complexToFloat(value.data);
        }
    }
}
