package kr.ac.kaist.kyotong.utils.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yearnning on 15. 1. 20..
 */
public class PreferenceManager extends PreferenceBase{

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, String value) {

        SharedPreferences prefs = context.getSharedPreferences(PreferenceBase.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefs_editor;

        prefs_editor = prefs.edit();
        prefs_editor.putString(key, value);
        prefs_editor.commit();
    }

    public static void put(Context context, String key, int value) {

        SharedPreferences prefs = context.getSharedPreferences(PreferenceBase.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefs_editor;

        prefs_editor = prefs.edit();
        prefs_editor.putInt(key, value);
        prefs_editor.commit();
    }

    public static void put(Context context, String key, boolean value) {

        SharedPreferences prefs = context.getSharedPreferences(PreferenceBase.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefs_editor;

        prefs_editor = prefs.edit();
        prefs_editor.putBoolean(key, value);
        prefs_editor.commit();
    }

    public static void put(Context context, String key, long value) {

        SharedPreferences prefs = context.getSharedPreferences(PreferenceBase.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefs_editor;

        prefs_editor = prefs.edit();
        prefs_editor.putLong(key, value);
        prefs_editor.commit();
    }

    /**
     * @param context
     * @param key
     * @param value_init
     * @return
     */
    public static String get(Context context, String key, String value_init) {
        SharedPreferences prefs = context.getSharedPreferences(PreferenceBase.PREFS, Context.MODE_PRIVATE);
        return prefs.getString(key, value_init);
    }

    public static int get(Context context, String key, int value_init) {
        SharedPreferences prefs = context.getSharedPreferences(PreferenceBase.PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(key, value_init);
    }

    public static boolean get(Context context, String key, boolean value_init) {
        SharedPreferences prefs = context.getSharedPreferences(PreferenceBase.PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, value_init);
    }

    public static long get(Context context, String key, long value_init) {
        SharedPreferences prefs = context.getSharedPreferences(PreferenceBase.PREFS, Context.MODE_PRIVATE);
        return prefs.getLong(key, value_init);
    }


}
