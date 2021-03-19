package com.sonetag;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the Tumblr API requests
 * @version 1
 * @author Benjamin BOURG
 */
public class TumblrAPI {
    /**
     * Attributes
     */
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final AppCompatActivity app;
    private final FirebaseFirestore db;
    private String secret;

    /**
     * Constructor
     * @param app the current app
     * @param db firebase DB
     */
    public TumblrAPI(AppCompatActivity app, FirebaseFirestore db){
        this.app = app;
        this.db = db;
    }

    /**
     * Start the token acquisition process
     */
    public void tumblr() {
        new GetTokens().execute();
    }

    public void defaultDisplay(){
        app.setContentView(R.layout.fragment_add_account);
    }

    public void httpRequest(String verifier, String token) {
        new ConnectionAPI().execute(verifier, token);

    }

    /**
     * Request user tokens
     */
    private class ConnectionAPI extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String s = null;
            try {
                url = new URL("https://europe-west1-onefeedtest-b74c6.cloudfunctions.net/tumblrAPI_oauth_2?oauth_verifier=" + params[0] + "&accessToken=" + params[1] + "&secretToken=" + secret);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int n = 0;
                char[] buffer = new char[1024 * 4];
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                StringWriter writer = new StringWriter();
                while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
                s = writer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }


            return s;
        }

        @Override
        protected void onPostExecute(String result) {
            readResponse(result);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    /**
     * Analyse response from server
     * @param s request result
     */
    public void readResponse(String s){
        JSONObject json;
        try {
            json = new JSONObject(s);

            //Create new social account
            Map<String, String> account = new HashMap<>();
            account.put("type", "tumblr");
            account.put("accessToken", json.getString("oauth_token"));
            account.put("secretToken", json.getString("oauth_token_secret"));
            account.put("accountName", json.getString("name"));

            ArrayList<Map<String, String>> account2 = new ArrayList<>();
            if(SingletonUserData.getInstance().listSocialAccounts != null) {
                account2 = SingletonUserData.getInstance().listSocialAccounts;
            }
            //Add account to the list
            account2.add(account);

            //Save the account
            UserSessionManager userSession = new UserSessionManager(app.getApplicationContext());
            db.collection("users").document(userSession.getUserSessionData().get(mAuth.getUid()).toString()).update("socialAccounts", account2);
            Toast.makeText(app.getApplicationContext(), app.getResources().getString(R.string.prompt_success), Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Request tokens to the server
     */
    private class GetTokens extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String s = null;
            try {
                url = new URL("https://europe-west1-onefeedtest-b74c6.cloudfunctions.net/tumblrAPI_oauth_1");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int n = 0;
                char[] buffer = new char[1024 * 4];
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                StringWriter writer = new StringWriter();
                while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
                s = writer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }


            return s;
        }

        @Override
        protected void onPostExecute(String result) {
            readTokens(result);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    /**
     * Get the tokens and ask permission to the user
     * @param s the result from the request
     */
    public void readTokens(String s){
        JSONObject json;
        try {
            json = new JSONObject(s);

            secret = json.getString("oauth_token_secret");

            WebView myWebView = new WebView(app.getApplicationContext());
            app.setContentView(myWebView);
            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.loadUrl("https://www.tumblr.com/oauth/authorize?oauth_token=" + json.getString("oauth_token"));
            myWebView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if(url.contains("oauth_verifier=")) {
                        view.setVisibility(View.GONE);
                        defaultDisplay();
                        Uri uri = Uri.parse(url);
                        httpRequest(uri.getQueryParameter("oauth_verifier"), uri.getQueryParameter("oauth_token"));
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
