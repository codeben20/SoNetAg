package com.sonetag;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class manages user information
 * @version 1
 * @author Benjamin BOURG
 */
public class SingletonUserData {

    /**
     * list of social accounts
     */
    ArrayList<Map<String, String>> listSocialAccounts;
    /**
     * App context
     */
    Context context;

    private static final SingletonUserData ourInstance = new SingletonUserData();
    public static SingletonUserData getInstance() {
        return ourInstance;
    }
    private SingletonUserData() { }

    /**
     * Modify the account list
     * @param list the new list
     */
    public void setListSocialAccounts(ArrayList<Map<String, String>> list){
        listSocialAccounts = list;
    }

    /**
     * Add a new account to the networks list
     * @param account the new account
     */
    public void addSocialAccount(Map<String, String> account){
        listSocialAccounts.add(account);
    }

    /**
     * Remove a social network from the list
     * @param index index of the account to remove
     */
    public void removeSocialAccount(int index){
        listSocialAccounts.remove(index);
    }

    /**
     * Get user social networks
     * @return list of social networks
     */
    public ArrayList<Map<String, String>> getListSocialAccounts(){
        return listSocialAccounts;
    }

    /**
     * Modify app context
     * @param context the new context
     */
    public void setContext(Context context){
        this.context = context;
    }
}
