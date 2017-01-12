package com.atguigu.mobileplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 刘闯 on 2017/1/12.
 */

public class CacheUtils {
    /**
     * 得到缓存的文本数据
     * @param mContext
     * @param key
     * @return
     */
    public static String getString(Context mContext, String key) {
        SharedPreferences sp = mContext.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }

    /**
     * 保持数据
     * @param mContext
     * @param key
     * @param value
     */
    public static void putString(Context mContext, String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
}
