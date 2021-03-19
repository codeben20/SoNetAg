package com.sonetag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

/**
 * This class manages the user profile page
 * @version 1
 * @author Benjamin BOURG
 */
public class ProfileFragment extends Fragment {
    /**
     * User information attribute
     */
    private UserSessionManager userSession;
    /**
     * Firebase authentification instance
     */
    final private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Constructor
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.userSession = new UserSessionManager(view.getContext());
        return view;
    }


    /**
     * List all the socials accounts
     */
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get user session data
        HashMap<String, Object> map = userSession.getUserSessionData();

        //Display name, firstname and email
        ((TextView)view.findViewById(R.id.profile_name)).setText((String)map.get(UserSessionManager.USER_NAME));
        ((TextView)view.findViewById(R.id.profile_firstname)).setText((String)map.get(UserSessionManager.USER_FIRST_NAME));
        ((TextView)view.findViewById(R.id.profile_email)).setText(mAuth.getCurrentUser().getEmail());
    }
}