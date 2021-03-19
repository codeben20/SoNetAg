package com.sonetag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

/**
 * This class manages the display of one social feed
 * @version 1
 * @author Benjamin BOURG
 */
public class OneSocialFragment extends Fragment {

    /**
     * Firebase authentification instance
     */
    final private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Constructor
     */
    public OneSocialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OneSocialFragment newInstance() {
        return new OneSocialFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_one_social, container, false);
        //Set up refresh page option
        SwipeRefreshLayout sRL = result.findViewById(R.id.home_refresh);
        sRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(SingletonUserData.getInstance().getListSocialAccounts() != null && SingletonUserData.getInstance().getListSocialAccounts().size() != 0) {
                    display(result);
                }
                sRL.setRefreshing(false);
            }
        });
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        display(view);

    }

    /**
     * Display the feed from the selected network
     * @param view the actual view
     */
    public void display(View view) {
        GetAllPost gAP = new GetAllPost(view);
        //Get uset temporary token
        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    // Selected the right network
                    if(SingletonUserData.getInstance().listSocialAccounts.get(SingletonSelectedSocial.getInstance().getIndex()).get("type").equals("twitter")) {
                        gAP.getPostOneSocial("https://europe-west1-onefeedtest-b74c6.cloudfunctions.net/getTwitterApi_homeFeed?idToken=" + idToken + "&index=" + SingletonSelectedSocial.getInstance().getIndex());
                    }else if(SingletonUserData.getInstance().listSocialAccounts.get(SingletonSelectedSocial.getInstance().getIndex()).get("type").equals("reddit")) {
                        gAP.getPostOneSocial("https://europe-west1-onefeedtest-b74c6.cloudfunctions.net/getRedditApi_homeFeedBest?idToken=" + idToken + "&index=" + SingletonSelectedSocial.getInstance().getIndex());
                    }else if(SingletonUserData.getInstance().listSocialAccounts.get(SingletonSelectedSocial.getInstance().getIndex()).get("type").equals("tumblr")) {
                        gAP.getPostOneSocial("https://europe-west1-onefeedtest-b74c6.cloudfunctions.net/getTumblrApi_homeFeed?idToken=" + idToken + "&index=" + SingletonSelectedSocial.getInstance().getIndex());
                    }else if(SingletonUserData.getInstance().listSocialAccounts.get(SingletonSelectedSocial.getInstance().getIndex()).get("type").equals("yammer")) {
                        gAP.getPostOneSocial("https://europe-west1-onefeedtest-b74c6.cloudfunctions.net/getYammerApi_homeFeed?idToken=" + idToken + "&index=" + SingletonSelectedSocial.getInstance().getIndex());
                    }
                }
            }
        });
    }
}