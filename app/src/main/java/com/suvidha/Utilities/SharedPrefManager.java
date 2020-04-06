package com.suvidha.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * A Singleton for managing your SharedPreferences.
 *
 * You should make sure to change the SETTINGS_NAME to what you want
 * and choose the operating made that suits your needs, the default is
 * MODE_PRIVATE.
 *
 * IMPORTANT: The class is not thread safe. It should work fine in most
 * circumstances since the write and read operations are fast. However
 * if you call edit for bulk updates and do not commit your changes
 * there is a possibility of data loss if a background thread has modified
 * preferences at the same time.
 *
 * Usage:
 *
 * int sampleInt = SharedPrefManager.getInstance(context).getInt(Key.SAMPLE_INT);
 * SharedPrefManager.getInstance(context).set(Key.SAMPLE_INT, sampleInt);
 *
 * If SharedPrefManager.getInstance(Context) has been called once, you can
 * simple use SharedPrefManager.getInstance() to save some precious line space.
 */
public class SharedPrefManager {
    private static final String SETTINGS_NAME = "default";
    private static SharedPrefManager sSharedPrefs;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private boolean mBulkUpdate = false;

    /**
     * Class for keeping all the keys used for shared preferences in one place.
     */
    public static class Key {
        /* Recommended naming convention:
         * ints, floats, doubles, longs:
         * SAMPLE_NUM or SAMPLE_COUNT or SAMPLE_INT, SAMPLE_LONG etc.
         *
         * boolean: IS_SAMPLE, HAS_SAMPLE, CONTAINS_SAMPLE
         *
         * String: SAMPLE_KEY, SAMPLE_STR or just SAMPLE
         */

        public static final String LOGIN_STATUS = "login_status";
        public static final String USER_IMEI = "user_imei";
        public static final String USER_NAME = "user_name";
        public static final String USER_EMAIL = "user_email";
        public static final String USER_PHONE = "user_phone";
        public static final String USER_ID = "user_id";
        public static final String TOKEN_KEY = "token";
        public static final String ZONE_KEY = "zone";
        public static final String QUARENTINE_LAT_KEY = "quarantinelat";
        public static final String QUARENTINE_LON_KEY = "quarantinelon";
        public static final String USER_ADDRESS = "address";
    }

    private SharedPrefManager(Context context) {
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }


    public static SharedPrefManager getInstance(Context context) {
        if (sSharedPrefs == null) {
            sSharedPrefs = new SharedPrefManager(context.getApplicationContext());

        }
        return sSharedPrefs;
    }

    public static SharedPrefManager getInstance() {
        if (sSharedPrefs != null) {
            return sSharedPrefs;
        }

        //Option 1:
        throw new IllegalArgumentException("Should use getInstance(Context) at least once before using this method.");

        //Option 2:
        // Alternatively, you can create a new instance here
        // with something like this:
        // getInstance(MyCustomApplication.getAppContext());
    }
    public void storeToken(String token){
        sSharedPrefs.doEdit();
        mEditor.putString(Key.TOKEN_KEY,token);
        sSharedPrefs.doCommit();
    }
    public String getToken(){
        return sSharedPrefs.getString(Key.TOKEN_KEY);
    }
    public boolean containsKey(String prefKey)
    {
        if(mPref.contains(prefKey))
        {
            return true;
        }
        else
            return false;

    }
    public boolean isLoggedIn(){
        return sSharedPrefs.getBoolean(Key.LOGIN_STATUS,false);
    }
    public void logOut()
    {
        sSharedPrefs.clear();
    }

    public void put(String key, String val) {
        doEdit();
        mEditor.putString(key, val);
        doCommit();
    }


    public void put(String key, int val) {
        doEdit();
        mEditor.putInt(key, val);
        doCommit();
    }
    public void put(String key, boolean val) {
        doEdit();
        mEditor.putBoolean(key, val);
        doCommit();
    }

    public void put(String key,float loc){
        doEdit();
        mEditor.putFloat(key,loc);
        doCommit();
    }
    public float getFloat(String key, float defaultValue) {
        return mPref.getFloat(key,defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    public String getString(String key) {
        return mPref.getString(key, null);
    }

    public int getInt(String key) {
        return mPref.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }


    public boolean getBoolean(String key) {
        return mPref.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPref.getBoolean(key, defaultValue);
    }

    public void remove(String... keys) {
        doEdit();
        for (String key : keys) {
            mEditor.remove(key);
        }
        doCommit();
    }

    /**
     * Remove all keys from SharedPreferences.
     */
    public void clear() {
        doEdit();
        mEditor.clear();
        doCommit();
    }

    public void edit() {
        mBulkUpdate = true;
        mEditor = mPref.edit();
    }

    public void commit() {
        mBulkUpdate = false;
        mEditor.commit();
        mEditor = null;
    }

    public void doEdit() {
        if (!mBulkUpdate && mEditor == null) {
            mEditor = mPref.edit();
        }
    }

    private void doCommit() {
        if (!mBulkUpdate && mEditor != null) {
            mEditor.commit();
            mEditor = null;
        }
    }
}