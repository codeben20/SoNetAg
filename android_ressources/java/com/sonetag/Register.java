package com.sonetag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the registration of a new user
 * @version 1
 * @author Benjamin BOURG
 */
public class Register extends ConnectionParentClass {

    /**
     * Firebase authentification instance
     */
    private FirebaseAuth mAuth;
    /**
     * Name field
     */
    private EditText name;
    /**
     * First name field
     */
    private EditText firstName;
    /**
     * Email field
     */
    private EditText email;
    /**
     * Password 1 field
     */
    private EditText password1;
    /**
     * Verify password field
     */
    private EditText password2;
    /**
     * Progress bar
     */
    private ProgressBar pb;

    /**
     * App language
     */
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        this.pb = findViewById(R.id.register_progressBar);

        Intent intent = getIntent();
        this.lang = intent.getStringExtra("lang");

        //Get all elements from layout
        name = findViewById(R.id.register_name);
        firstName = findViewById(R.id.register_firstname);
        email = findViewById(R.id.register_email);
        password1 = findViewById(R.id.register_pw1);
        password2 = findViewById(R.id.register_pw2);

        //Set up progress bar visibility to invisible
        pb.setVisibility(View.INVISIBLE);

        //Add listener to the submit button
        findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set up progress bar visibility to visible
                pb.setVisibility(View.VISIBLE);
                //Verify is the 2 pPW are equals
                if((password1.getText().toString()).compareTo(password2.getText().toString()) == 0) {
                    if(isEmailValid(email.getText().toString())) {
                        userRegistration();
                    }else {
                        //Error
                        Toast.makeText(Register.this, getString(R.string.prompt_emailInvalid), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    //The 2 PW are different
                    Toast.makeText(Register.this, getString(R.string.prompt_pwDif), Toast.LENGTH_SHORT).show();
                }
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }


    /**
     * Register a new user
     */
    public void userRegistration(){
        //Create an account with email and password
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password1.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Create a new user with a first and last name
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name.getText().toString());
                            user.put("firstName", firstName.getText().toString());
                            user.put("socialAccounts", new ArrayList<Map<String, String>>());
                            user.put("lang", lang);
                            user.put("subscription", "free");

                            // Add a new document to the DB
                            db.collection("users").document(mAuth.getUid())
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                       (new UserSessionManager(Register.this)).createUserSession(name.getText().toString(), firstName.getText().toString(), lang, "free");
                                        SingletonUserData.getInstance().setListSocialAccounts(new ArrayList<Map<String, String>>());

                                        //Start the home activity
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        startActivity(intent);
                                    }else {
                                        //Error
                                        Toast.makeText(Register.this, getString(R.string.prompt_dbFailed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(Register.this, getString(R.string.prompt_authFailed), Toast.LENGTH_SHORT).show();
                        }
                        pb.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
