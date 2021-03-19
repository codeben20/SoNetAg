package com.sonetag;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * This class manage the display of one social network
 * @version 1
 * @author Benjamin BOURG
 */
public class SocialNetworkFragment extends Fragment implements TabLayoutMediator.TabConfigurationStrategy {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    /**
     * Social network account name
     */
    private String socialName;
    /**
     * Index of the account in the list
     */
    private int index;

    /**
     * Constructor
     */
    public SocialNetworkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SocialNetworkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialNetworkFragment newInstance(String socialName, int index) {
        SocialNetworkFragment fragment = new SocialNetworkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, socialName);
        args.putInt(ARG_PARAM2, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            socialName = getArguments().getString(ARG_PARAM1);
            index = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setUpViewPager(view);

        TabLayout tabLayout = view.findViewById(R.id.socialAccount_tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.socialAccount_pageViewer);
        final String lables[] = {};
        new TabLayoutMediator(tabLayout, viewPager, this).attach();

        //this.setUpTabLayout(view);
    }



    private void setUpViewPager(View v){
        ViewPager2 pager = v.findViewById(R.id.socialAccount_pageViewer);
        pager.setAdapter(new PageAdapter(this, index));
    }



    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        if(position == 0){
            tab.setText(R.string.prompt_text_home);
        }else if(position == 1){
            tab.setText(R.string.createPost_title);
        }
    }
}