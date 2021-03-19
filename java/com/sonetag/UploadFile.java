package com.sonetag;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class manages post publication on the server
 * @version 1
 * @author Benjamin BOURG
 */
public class UploadFile extends AsyncTask<Object, String, String> {

    /**
     * Upload post to the server
     * @param params the url, post text, post file, context
     * @return
     */
    @Override
    protected String doInBackground(Object[] params) {
        Log.i("TAG", "doInBackground: ");
        UriToPath uToP = new UriToPath((Context)params[3]);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain");
        //Create the request body
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",uToP.getFilePathByUri(Uri.parse((String)params[2])),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(uToP.getFilePathByUri(Uri.parse((String)params[2])))))
                .addFormDataPart("content",(String)params[1])
                .build();
        //Send the request
        Request request = new Request.Builder()
                .url((String)params[0])
                .method("POST", body)
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}