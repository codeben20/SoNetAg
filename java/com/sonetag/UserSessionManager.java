package com.sonetag;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Locale;

/**
 * This class manages user session information
 * @version 1
 * @author Benjamin BOURG
 */
public class UserSessionManager {

    /**
     * Attributes
     */
    private Context context;
    private SharedPreferences session;
    private SharedPreferences.Editor editor;

    /**
     * Variables names
     */
    public static final String USER_CONNECTED = "user_connected";
    public static final String USER_NAME = "user_name";
    public static final String USER_FIRST_NAME = "user_first_name";
    public static final String USER_LANG = "user_lang";
    public static final String USER_SUBSCRIPTION = "user_subscription";

    /**
     * Constructor
     * @param context the app context
     */
    public UserSessionManager(Context context){
        this.context = context;
        this.session = this.context.getSharedPreferences("userSession", Context.MODE_PRIVATE);
        this.editor = this.session.edit();
    }


    /**
     * Save information on the device
     * @param name user name
     * @param firstName user first name
     * @param lang language
     * @param sub subscription type
     */
    public void createUserSession(String name, String firstName, String lang, String sub){
        editor.putBoolean(USER_CONNECTED, true);
        editor.putString(USER_NAME, name);
        editor.putString(USER_FIRST_NAME, firstName);
        editor.putString(USER_SUBSCRIPTION, sub);
        editor.putString(USER_LANG, lang);
        editor.commit();
    }

    /**
     * Get if the user is already connected
     * @return if the user is log in
     */
    public boolean isLogIn(){
        return session.getBoolean(USER_CONNECTED, false);
    }

    /**
     * Get user information from device
     * @return map of information
     */
    public HashMap<String, Object> getUserSessionData(){
        HashMap<String, Object> map = new HashMap<>();

        map.put(USER_NAME, this.session.getString(USER_NAME, null));
        map.put(USER_FIRST_NAME, this.session.getString(USER_FIRST_NAME, null));
        map.put(USER_SUBSCRIPTION, this.session.getString(USER_SUBSCRIPTION, null));
        map.put(USER_LANG, this.session.getString(USER_LANG, Locale.getDefault().getDisplayLanguage()));

        return map;
    }

    /**
     * Log out an user
     */
    public void logOutUserSession(){
        editor.clear();
        editor.commit();
    }
}
