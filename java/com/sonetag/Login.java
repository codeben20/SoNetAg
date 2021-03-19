package com.sonetag;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

/**
 * Class manages the login activity
 * @version 1
 * @author Benjamin BOURG
 */
public class Login extends ConnectionParentClass{
    /**
     * Attributes
     */
    /**
     * Firebase authentification
     */
    private FirebaseAuth mAuth;
    /**
     * Email field
     */
    private EditText email;
    /**
     * Password field
     */
    private EditText password;
    /**
     * Language choice
     */
    private Spinner spinner;
    private int previousPosition = 0;
    /**
     * Treatment status
     */
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //Get element from layout
        email = findViewById(R.id.signIn_email);
        password = findViewById(R.id.signIn_pw);
        pb = findViewById(R.id.signIn_progress);
        pb.setVisibility(View.INVISIBLE);

        //Add listener to submit button
        findViewById(R.id.button_signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(password.getText().toString()).isEmpty()) {
                    if (isEmailValid(email.getText().toString())) {
                        userConnection();
                    } else {
                        Toast.makeText(Login.this, getString(R.string.prompt_emailInvalid), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        //Allow to change the app language
        spinner = findViewById(R.id.signIn_lang);
        //List items values
        String[] listNetworks={"En","Fr"};
        ArrayAdapter<String> dataAdapterR = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listNetworks);
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Add the list to the menu
        spinner.setAdapter(dataAdapterR);

        //Display the selected language
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            previousPosition = extras.getInt("position");
            spinner.setSelection(previousPosition);
        }else {
            previousPosition = spinner.getSelectedItemPosition();
        }

        //Add listener to the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position != previousPosition) {
                    changeLanguage(parentView.getItemAtPosition(position).toString(), position);
                    previousPosition = position;

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }


    /**
     * Connect an user
     */
    public void userConnection() {
        pb.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String userID = mAuth.getCurrentUser().getUid();
                            db.collection("users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                     //Create session
                                    (new UserSessionManager(Login.this)).createUserSession(value.getString("name"), value.getString("firstName"), value.getString("lang"), value.getString("subscription"));

                                    SingletonUserData.getInstance().setListSocialAccounts((ArrayList)value.get("socialAccounts"));

                                    //Start the main activity
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                        } else {
                            Toast.makeText(Login.this, getString(R.string.prompt_authFailed), Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }

    /**
     * Change app language
     * @param lang new language
     * @param position language position in spinner
     */
    public void changeLanguage(String lang, int position){
        setLang(lang, position);
    }

    /**
     * Method called when the user click on register
     * The method open the register activity
     * @param view the actual view
     */
    public void openRegisterForm(View view){
        Intent registerActivity = new Intent(Login.this, Register.class);
        registerActivity.putExtra("lang", (String)spinner.getSelectedItem());
        startActivity(registerActivity);
    }

    /**
     * Build password forgotten popup
     * @param v the actual view
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void forgottenPW(View v){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set layout
        final View customLayout = getLayoutInflater().inflate( R.layout.forgotten_pw_layout,null);
        builder.setView(customLayout);

        // add buttons
        builder.setPositiveButton(getResources().getString(R.string.prompt_send), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = ((EditText)customLayout.findViewById(R.id.forgottenPW_email)).getText().toString();
                if (isEmailValid(email)) {
                    //Send email for resetting password
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Email sent
                                Toast.makeText(Login.this, getResources().getString(R.string.prompt_emailSent), Toast.LENGTH_SHORT).show();
                            }else {
                                //Error
                                Toast.makeText(Login.this, getResources().getString(R.string.prompt_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    //error
                    Toast.makeText(Login.this, getResources().getString(R.string.prompt_emailInvalid), Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton(getResources().getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Create and show
        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }
}
