package com.sonetag;

import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
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
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class manages the get feed request for the different situations
 * @version 1
 * @author Benjamin BOURG
 */
public class GetAllPost {

    final private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final View view;

    /**
     * Constructor
     */
    public GetAllPost(View v){
        this.view = v;
    }

    /**
     * Get feed from one social
     * @param url of the server
     */
    public void getPostOneSocial(String url){
        new LongOperation().execute(url);
    }

    /**
     * Get home feed
     */
    public void getPost(){
        //Get user temporary token
        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    (new LongOperation()).execute("https://europe-west1-onefeedtest-b74c6.cloudfunctions.net/getHomeFeed?idToken=" + idToken);
                }
            }
        });
    }

    /**
     * Do the HTTP request
     */
    public class LongOperation extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String s = null;

            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int n;
                char[] buffer = new char[1024 * 4];
                InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
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
     * Handle HTTP response
     * @param s the result body from the HTTP request
     */
    public void readResponse(String s){
        if(s != null) {
            JSONArray json;
            try {
                ArrayList<JSONObject> list = new ArrayList<>();
                //Convert the string to json
                json = new JSONArray(s);

                //Add each post from the JSON array to the list
                for (int i = 0; i < json.length(); i++) {
                    list.add((JSONObject) json.get(i));
                }

                //Date format
                SimpleDateFormat format = new SimpleDateFormat("EEE d MMM 'at' HH:mm");

                //Build posts
                PostAdapter adapter = new PostAdapter(view.getContext(), list);
                ListView lv = view.findViewById(R.id.main_feed);
                TextView tv = view.findViewById(R.id.home_refreshDate);
                String tmp;
                //Display last update date
                if(lv.getHeaderViewsCount() == 0){
                    tmp = view.getResources().getString(R.string.prompt_last_update) + format.format(new Date(System.currentTimeMillis()));
                    tv.setText(tmp);
                    lv.addHeaderView(tv);
                }else {
                    tmp = view.getResources().getString(R.string.prompt_last_update) + format.format(new Date(System.currentTimeMillis()));
                    tv.setText(tmp);
                }
                lv.setAdapter(adapter);
                lv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if((v.getId() == list.size() - 3) && v.isShown()){
                            Toast.makeText(v.getContext(), "loading", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), "error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
