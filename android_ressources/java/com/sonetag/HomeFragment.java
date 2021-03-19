package com.sonetag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * This class manages the home page interactions
 * @version 1
 * @author Benjamin BOURG
 */
public class HomeFragment  extends Fragment {

    /**
     * Attribute
     */
    private GetAllPost gAP;

    /**
     * Constructor
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_home, container, false);
        gAP = new GetAllPost(result);
        //Set up refresh option
        SwipeRefreshLayout sRL = result.findViewById(R.id.home_refresh);
        sRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(SingletonUserData.getInstance().getListSocialAccounts() != null && SingletonUserData.getInstance().getListSocialAccounts().size() != 0) {
                    gAP.getPost();
                }
                sRL.setRefreshing(false);
            }
        });
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Get home feed
        if(SingletonUserData.getInstance().getListSocialAccounts() != null && SingletonUserData.getInstance().getListSocialAccounts().size() != 0) {
            gAP.getPost();
        }
    }

}
