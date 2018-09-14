package com.example.hp.cleaners;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


/**
 * Created by HP on 4/26/2018.
 */

public class PostConnection {

    public static void POST(RequestParams params, String url_req, JsonHttpResponseHandler jsonHttpResponseHandler) {

        //String base_url = "http://10.0.2.2:88/clean/index.php/api/secured/";
        String base_url = "http://rmcltd.com.ng/clean/index.php/api/secured/";
        String post_url = base_url + url_req;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth("appUser","appTester#2");
        //Log.e("TAG", client.toString());
        client.post(post_url, params, jsonHttpResponseHandler);
    }

}
