package com.sonetag;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * This class is the page adapter for one social network display
 * @version 1
 * @author Benjamin BOURG
 */
public class PageAdapter extends FragmentStateAdapter {

    /**
     * Network index in the list
     */
    private final int index;

    /**
     * Constructor
     */
    public PageAdapter(Fragment f, int index) {
        super(f);
        this.index = index;
    }

    /**
     * Number of pages
     */
    @Override
    public int getItemCount() {
        return 2;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            SingletonSelectedSocial.getInstance().setIndex(index);
            return(OneSocialFragment.newInstance());
        }else{
            return(CreatePostOneSocialFragment.newInstance());
        }
    }


}
