package com.sonetag;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @version 1
 * @author Benjamin BOURG
 */

public class ConnectionParentClass extends AppCompatActivity {

    protected FirebaseAuth mAuth;
    final protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * method is used for checking if an email has a valid format
     * @param email the email to verify
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean validFormat = false;
        //Email expression format
        String emailFormat = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(emailFormat, Pattern.CASE_INSENSITIVE);
        //Check the email format
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches())
        {
            validFormat = true;
        }
        return validFormat;
    }

    /**
     * Change the app language
     * @param lang the new language
     * @param position position of the selected language in the list
     */
    public void setLang(String lang, int position) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration conf = resources.getConfiguration();
        conf.locale = new Locale(lang);
        resources.updateConfiguration(conf, dm);
        Intent intent = new Intent(this, Login.class);
        Bundle b = new Bundle();
        b.putInt("position", position);
        intent.putExtras(b);
        startActivity(intent);
        this.finish();
    }

}
