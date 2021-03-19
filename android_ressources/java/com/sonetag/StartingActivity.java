package com.sonetag;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

/**
 * This class is lauching activity
 * @version 1
 * @author Benjamin BOURG
 */
public class StartingActivity extends ConnectionParentClass {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching);

        //Get firebase authentification instance
        mAuth = FirebaseAuth.getInstance();

        SingletonUserData.getInstance().setContext(this);

        //
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startConnection();
            }
        };

        //Wait 2 secondes before starting
        new Handler().postDelayed(runnable, 2000);
    }

    /**
     * Manage the app connection
     */
    public void startConnection(){
        //Check internet connection
        if(isInternetConnection()) {
            // Check if user is signed in (non-null)
            final FirebaseUser currentUser = mAuth.getCurrentUser();
            //If the user is already connected
            if (currentUser != null) {
                db.collection("users").document(currentUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        //Get user data
                        String subscription = value.getString("subscription");
                        String name = value.getString("name");
                        String firstName = value.getString("firstName");
                        ArrayList socialAccounts = (ArrayList)value.get("socialAccounts");
                        String lang = value.getString("lang");

                        //Check language
                        if (lang.compareToIgnoreCase((String.valueOf(getResources().getConfiguration().locale)).split("_")[0]) != 0) {
                            setLang(lang, 0);
                        }

                        //Create session
                        UserSessionManager userSession = new UserSessionManager(StartingActivity.this);
                        userSession.createUserSession(name, firstName, lang, subscription);

                        SingletonUserData.getInstance().setListSocialAccounts(socialAccounts);

                        //Start the main activity
                        Intent intent = new Intent(StartingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

            } else {
                //If the user is not connected
                //Open the login activity
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
            }
        }
        //If not internet connected
        else {
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            UserSessionManager userSession = new UserSessionManager(this);
            if (userSession.isLogIn()){
                //Start the main activity
                Intent intent = new Intent(StartingActivity.this, MainActivity.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
            }
        }
    }

    /**
     * Require the internet connection
     * @return is the user is connected to internet
     */
    public boolean isInternetConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
