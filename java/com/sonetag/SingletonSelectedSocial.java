package com.sonetag;

/**
 * This class manage the selected social network
 * @version 1
 * @author Benjamin BOURG
 */
public class SingletonSelectedSocial {
    /**
     * Selected social network index in the list
     */
    private int index;

    private static final SingletonSelectedSocial ourInstance = new SingletonSelectedSocial();
    public static SingletonSelectedSocial getInstance() {
        return ourInstance;
    }
    private SingletonSelectedSocial() { }

    /**
     * Get the selected social network index
     * @return index selected social network index
     */
    public int getIndex(){
        return index;
    }

    /**
     * Set the selected social network index
     * @param i selected social network index
     */
    public void setIndex (int i){
        index = i;
    }
}
