package com.sonetag;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class manages the Reddit API
 * @version 1
 * @author Benjamin BOURG
 */
public class RedditAPI {

    final private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final AppCompatActivity app;
    private final FirebaseFirestore db;
    private Map<String, String> account;

    /**
     * Constructor
     * @param app the application
     * @param db the database instance
     */
    public RedditAPI(AppCompatActivity app, FirebaseFirestore db){
        this.app = app;
        this.db = db;
    }

    /**
     * Get user authorisation
     */
    public void reddit() {
        WebView myWebView = new WebView(app.getApplicationContext());
        app.setContentView(myWebView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl("https://www.reddit.com/api/v1/authorize?client_id=DF4aAj_lkA0v7w&response_type=code&state=eqrghzzeretherthertyhjreh&redirect_uri=http://localhost/project_course/app/js/redditCallBack&duration=permanent&scope=identity");
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.contains("code=")) {
                    view.setVisibility(View.GONE);
                    defaultDisplay();
                    httpRequest(url.split("code=")[1]);
                }
            }
        });
    }

    public void defaultDisplay(){
        app.setContentView(R.layout.fragment_add_account);
    }

    public void httpRequest(String code) {
        new ConnectionAPI().execute(code);
    }

    /**
     * Get Reddit tokens
     */
    private class ConnectionAPI extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String s = null;
            try {
                url = new URL("https://europe-west1-onefeedtest-b74c6.cloudfunctions.net/getRedditApi_accessToken?code="+params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int n;
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

    //Get Tokens from response and request account name
    public void readResponse(String s){
        JSONObject json;
        try {
            json = new JSONObject(s);

            account = new HashMap<>();
            account.put("type", "reddit");
            account.put("accessToken", json.getString("access_token"));
            account.put("refreshToken", json.getString("refresh_token"));

            new GetAccountName().execute(json.getString("access_token"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get reddit account name associated to the token
     */
    private class GetAccountName extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object[] params) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://oauth.reddit.com/api/v1/me")
                    .method("GET", null)
                    .addHeader("user-agent", "Browser:OneFeed:v0.1 (by /u/Geoffrey-Mo)")
                    .addHeader("Authorization", "Bearer " + params[0])
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            readResponse2(result);

        }

    }

    /**
     * Get account name and save new account in the DB
     * @param s HTTP request response
     */
    public void readResponse2(String s){
        JSONObject json;
        try {
            json = new JSONObject(s);
            account.put("accountName", json.getString("name"));
            ArrayList<Map<String, String>> account2 = new ArrayList<>();
            if(SingletonUserData.getInstance().listSocialAccounts != null) {
                account2 = SingletonUserData.getInstance().listSocialAccounts;
            }
            //Add account to the list
            account2.add(account);
            //Save list
            UserSessionManager userSession = new UserSessionManager(app.getApplicationContext());
            db.collection("users").document(userSession.getUserSessionData().get(mAuth.getUid()).toString()).update("socialAccounts", account2);
            Toast.makeText(app.getApplicationContext(), app.getResources().getString(R.string.prompt_success), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
