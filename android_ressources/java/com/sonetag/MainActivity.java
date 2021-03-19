package com.sonetag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage the app
 * @version 1
 * @author Benjamin BOURG
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnButtonClickedListener {

    /**
     * Attributes
     */
    final private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final int OPEN_REQUEST_CODE = 41;

    private TextView tv;
    private ImageView iv;
    private DrawerLayout drawerLayout;

    /**
     * User data
     */
    private UserSessionManager userSession;

    /**
     * Fragments attributes for the different pages
     */
    private Fragment homeFragment;
    private Fragment profileFragment;
    private Fragment createPostFragment;
    private Fragment addAccountFragment;
    private Fragment socialNetworkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create user session object
        userSession = new UserSessionManager(this);

        // Configure Drawer Layout
        this.drawerLayout = findViewById(R.id.activity_main_drawer_layout);

        tv = findViewById(R.id.page_title);
        iv = findViewById(R.id.main_rightIcon);

        //Set up hamburger menu
        ((ImageView)findViewById(R.id.hamburger_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Set up the menu
        setUpMenu();

        this.configureAndShowMainFragment();
    }

    /**
     * Handle menu interactions
     * @param menuItem selected item in the menu
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //Delete right icon
        iv.setImageResource(0);

        switch (menuItem.getItemId()){
            case R.id.nav_home :
                if (this.homeFragment == null) {
                    this.homeFragment = HomeFragment.newInstance();
                }
                this.startTransactionFragment(this.homeFragment);
                tv.setText(R.string.prompt_text_home);
                iv.setImageResource(R.drawable.ic_add_circle);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createNewPost();
                        //Delete right icon
                        iv.setImageResource(0);

                        tv.setText(R.string.createPost_title);
                    }
                });
                break;

            case R.id.nav_profile :
                if (this.profileFragment == null) {
                    this.profileFragment = ProfileFragment.newInstance();
                }
                this.startTransactionFragment(this.profileFragment);
                tv.setText(R.string.profile_myProfile);
                iv.setImageResource(R.drawable.ic_save);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = ((EditText) v.getRootView().findViewById(R.id.profile_email)).getText().toString();
                        if(!email.equals(mAuth.getCurrentUser().getEmail())) {
                            mAuth.getCurrentUser().updateEmail(email)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(v.getContext(), R.string.prompt_dataUpdated, Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(), R.string.prompt_error, Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                    }
                });
                break;

            case R.id.nav_addAccount :
                if (this.addAccountFragment == null) {
                    this.addAccountFragment = AddAccountFragment.newInstance();
                }
                this.startTransactionFragment(this.addAccountFragment);
                tv.setText(R.string.addAccount_title);
                break;

            case R.id.nav_logout :
                mAuth.signOut();
                userSession.logOutUserSession();
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                break;

            default:
                this.socialNetworkFragment = SocialNetworkFragment.newInstance(menuItem.getTitle().toString(), menuItem.getItemId());
                this.startTransactionFragment(this.socialNetworkFragment);
                tv.setText(menuItem.getTitle().toString());
                break;


        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * This function set up the menu
     */
    private void setUpMenu(){
        //List the social network accounts
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        Menu submenu = menu.addSubMenu(R.string.menu_prompt_myNetworks);
        ArrayList<Map<String, String>> socialAccounts = SingletonUserData.getInstance().getListSocialAccounts();
        if(socialAccounts != null) {
            for (int i = 0; i < socialAccounts.size(); i++) {
                //submenu.add(socialAccounts.get(i).get("accountName"));
                submenu.add(1, i, i, socialAccounts.get(i).get("accountName"));
            }

            for (int i = 0; i < submenu.size(); i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //((submenu.getItem(i).setActionView(new ImageButton(this))).getActionView()).setBackground(getDrawable(R.drawable.ic_clear));
                    ((submenu.getItem(i).setActionView(new ImageView(this))).getActionView()).setBackground(getDrawable(R.drawable.ic_more_vertical));
                    (submenu.getItem(i).getActionView()).setId(i);
                }
                (submenu.getItem(i).getActionView()).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        final View view = v;
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setMessage(R.string.prompt_delete_account)
                                .setPositiveButton(R.string.prompt_validate, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Map<String, Object> newList = new HashMap<>();
                                        SingletonUserData.getInstance().removeSocialAccount(v.getId());
                                        newList.put("socialAccounts", SingletonUserData.getInstance().getListSocialAccounts());
                                        db.collection("users").document((String) mAuth.getUid())
                                                .update(newList)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                          @Override
                                                                          public void onSuccess(Void aVoid) {
                                                                              //submenu.removeItem(view.getId());
                                                                              setUpMenu();
                                                                              Toast.makeText(view.getContext(), getString(R.string.prompt_account_remove), Toast.LENGTH_LONG).show();
                                                                          }
                                                                      }
                                                );
                                    }
                                })
                                .setNegativeButton(R.string.prompt_cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                        builder.create().show();

                    }
                });
            }
        }

        navigationView.invalidate();

        // Configure NavigationView
        navigationView.setNavigationItemSelectedListener(this);

    }

    /**
     * Display the create a post fragment
     */
    public void createNewPost(){
        if (this.createPostFragment == null) {
            this.createPostFragment = CreatePostFragment.newInstance();
        }
        tv.setText(R.string.createPost_title);
        iv.setImageResource(0);
        this.startTransactionFragment(this.createPostFragment);
    }

    private void configureAndShowMainFragment(){
        // A - Get FragmentManager (Support) and Try to find existing instance of fragment in FrameLayout container
        homeFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_layout);

        if (homeFragment == null) {
            // B - Create new main fragment
            homeFragment = new HomeFragment();
            // C - Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main_frame_layout, homeFragment)
                    .commit();

            iv.setImageResource(R.drawable.ic_add_circle);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewPost();
                }
            });
        }
    }

    private void startTransactionFragment(Fragment fragment){
        if (!fragment.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout, fragment).commit();
        }
    }

    /**
     * Manage the button interactions
     * @param view the actual view
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onButtonClicked(View view) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        if(view.getTag().toString().equals("30")){
            BottomSheet bS = new BottomSheet();
            bS.show(getSupportFragmentManager(), "TAG");
            //Create a post

        }else if (view.getId() == R.id.createPost_image) {
            openFile();
        }else if(view.getTag().toString().equals("515")) {
            ((ImageView) findViewById(R.id.createPost_delete)).setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_empty));
            ((ImageView)findViewById(R.id.createPost_image)).setImageDrawable(getResources().getDrawable(R.drawable.ic_add_photo));
            ((ImageView) findViewById(R.id.createPost_delete)).setTag("510");
            ((ImageView) findViewById(R.id.createPost_image)).setTag("");
        }else if(view.getTag().toString().equals("540")){
            ArrayList<Map<String, String>> list = SingletonUserData.getInstance().getListSocialAccounts();
            final Context context = view.getContext();
            View v = view.getRootView();
            final String url = "https://us-central1-onefeedtest-b74c6.cloudfunctions.net/postTwitterApi_newPost?idToken=";
            for(int i = 0; i < SingletonUserData.getInstance().getListSocialAccounts().size(); i++){
                if(((CheckBox)v.findViewById(i)).isChecked()){
                    if((list.get(i)).get("type").equals("twitter")){
                        final int index = i;
                        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String idToken = task.getResult().getToken();

                                    ImageView img = findViewById(R.id.createPost_image);
                                    String imgStr = img.getTag().toString();
                                    new UploadFile().execute(url+ idToken + "&index=" + index, ((EditText)findViewById(R.id.createPost_text)).getText().toString(), imgStr, context);
                                    //Toast.makeText(context, "send", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle error -> task.getException();
                                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                                }

                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        }else if(view.getTag().toString().equals("twitter")){
            new TwitterAPI(userSession.getUserSessionData().get(userSession.USER_LANG).toString(), this, mAuth, db, (navigationView.getMenu()).addSubMenu(R.string.menu_prompt_myNetworks));
        }else if(view.getTag().toString().equals("reddit")){
            (new RedditAPI(this, db)).reddit();
        }else if(view.getTag().toString().equals("linkedin")) {

        }else if(view.getTag().toString().equals("tumblr")){
            (new TumblrAPI(this, db)).tumblr();
        }else if(view.getTag().toString().equals("yammer")){

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_REQUEST_CODE) {
                if (resultData != null) {
                    ((ImageView) findViewById(R.id.createPost_image)).setImageURI(resultData.getData());
                    ((ImageView) findViewById(R.id.createPost_image)).setTag(resultData.getData());
                    ((ImageView) findViewById(R.id.createPost_delete)).setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_full));
                    ((ImageView) findViewById(R.id.createPost_delete)).setTag("515");
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openFile()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, OPEN_REQUEST_CODE);
    }
}
