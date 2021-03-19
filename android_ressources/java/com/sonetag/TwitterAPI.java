package com.sonetag;

import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the Twitter API requests for user tokens
 * @version 1
 * @author Benjamin BOURG
 */
public class TwitterAPI {

    /**
     * Constructor
     * @param lang app language
     * @param activity the app
     * @param firebaseAuth fire authentification instance
     * @param db firebase DB
     * @param submenu menu
     */
    public TwitterAPI(String lang, final Activity activity, FirebaseAuth firebaseAuth, final FirebaseFirestore db, final Menu submenu){
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        provider.addCustomParameter("lang", lang);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //Create a new network
                                Map<String, String> account = new HashMap<>();
                                account.put("type", "twitter");
                                account.put("accessToken", ((OAuthCredential)task.getResult().getCredential()).getAccessToken());
                                account.put("accessTokenSecret", ((OAuthCredential)task.getResult().getCredential()).getSecret());
                                account.put("accountNameId", task.getResult().getAdditionalUserInfo().getUsername());
                                account.put("accountName", task.getResult().getAdditionalUserInfo().getProfile().get("name").toString());
                                ArrayList<Map<String, String>> account2 = new ArrayList<>();
                                if(SingletonUserData.getInstance().listSocialAccounts != null) {
                                    account2 = SingletonUserData.getInstance().listSocialAccounts;
                                }
                                //Add new network to the list
                                account2.add(account);
                                //Save the new list
                                SingletonUserData.getInstance().addSocialAccount(account);
                                UserSessionManager userSession = new UserSessionManager(activity.getApplicationContext());
                                db.collection("users").document(userSession.getUserSessionData().get(firebaseUser.getUid()).toString()).update("socialAccounts", account2);
                                Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.prompt_success), Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.prompt_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            firebaseUser
                    .startActivityForLinkWithProvider( activity, provider.build())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //Create a new network
                                Map<String, String> account = new HashMap<>();
                                account.put("type", "twitter");
                                account.put("accessToken", ((OAuthCredential)task.getResult().getCredential()).getAccessToken());
                                account.put("accessTokenSecret", ((OAuthCredential)task.getResult().getCredential()).getSecret());
                                account.put("accountNameId", task.getResult().getAdditionalUserInfo().getUsername());
                                account.put("accountName", task.getResult().getAdditionalUserInfo().getProfile().get("name").toString());
                                ArrayList<Map<String, String>> account2 = new ArrayList<>();
                                if(SingletonUserData.getInstance().listSocialAccounts != null) {
                                    account2 = SingletonUserData.getInstance().listSocialAccounts;
                                }
                                //Add new network to the list
                                account2.add(account);
                                //Save the new list
                                db.collection("users").document(firebaseAuth.getUid()).update("socialAccounts", account2);
                                Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.prompt_success), Toast.LENGTH_SHORT).show();
                                //Add account to the menu
                                submenu.setGroupEnabled(0, true);
                                submenu.addSubMenu(0, account2.size()-1, account2.size()-1, (String)account.get("accountName"));
                            }else {
                                //Error
                                Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.prompt_error) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

}
