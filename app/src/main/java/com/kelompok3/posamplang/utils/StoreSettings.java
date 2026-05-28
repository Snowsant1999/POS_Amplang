package com.kelompok3.posamplang.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class StoreSettings {

    private static final String PREF_NAME = "store_settings";
    private static final String KEY_NAME = "store_name";
    private static final String KEY_ADDRESS = "store_address";
    private static final String KEY_EMAIL = "store_email";
    private static final String KEY_PHONE = "store_phone";

    public static final String DEFAULT_NAME = "Amplang Salsabila";
    public static final String DEFAULT_ADDRESS = "Samarinda";
    public static final String DEFAULT_EMAIL = "amplangsalsabila@gmail.com";
    public static final String DEFAULT_PHONE = "081273829217";

    private StoreSettings() { }

    public static StoreSettingsData get(Context context) {
        SharedPreferences prefs = prefs(context);
        return new StoreSettingsData(
                prefs.getString(KEY_NAME, DEFAULT_NAME),
                prefs.getString(KEY_ADDRESS, DEFAULT_ADDRESS),
                prefs.getString(KEY_EMAIL, DEFAULT_EMAIL),
                prefs.getString(KEY_PHONE, DEFAULT_PHONE)
        );
    }

    public static void save(Context context, StoreSettingsData data) {
        prefs(context).edit()
                .putString(KEY_NAME, data.name)
                .putString(KEY_ADDRESS, data.address)
                .putString(KEY_EMAIL, data.email)
                .putString(KEY_PHONE, data.phone)
                .apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static class StoreSettingsData {
        public final String name;
        public final String address;
        public final String email;
        public final String phone;

        public StoreSettingsData(String name, String address, String email, String phone) {
            this.name = name;
            this.address = address;
            this.email = email;
            this.phone = phone;
        }
    }
}
