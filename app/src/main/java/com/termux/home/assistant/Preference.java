package com.termux.home.assistant;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
    private static final String CONFIG_FILE = "config";
    private static final String HAS_INSTALL_SERVICE = "install_service";
    /**
     * 由于解压缩后的文件没有执行权限，所以给可以执行的权限
     */
    private static final String HAS_RUN_CHOMD = "chmod_bin";

    public static boolean hasInstallService() {
        SharedPreferences sharedPreferences = BaseApplication.baseContext.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(HAS_INSTALL_SERVICE, false);
    }

    public static void saveInstallService(Context context, Boolean valuse) {
        save(context, HAS_INSTALL_SERVICE, valuse);
    }

    public static boolean hasRunChmod() {
        SharedPreferences sharedPreferences = BaseApplication.baseContext.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(HAS_RUN_CHOMD, false);
    }

    public static void saveRunChmod(Context context, Boolean value) {
        save(context, HAS_RUN_CHOMD, value);
    }

    private static void save(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void save(Context context, String key, Boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
