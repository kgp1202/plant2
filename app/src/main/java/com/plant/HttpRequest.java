package com.plant;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by angks on 2016-05-19.
 */
public class HttpRequest extends Thread {
    HttpURLConnection conn;
    URL url;
    public HttpRequest(String inputUrl){
        try {
            url=new URL(inputUrl);
        } catch (MalformedURLException e) {
            Log.d("test","URL casting error!");
        }
    }
    @Override
    public void run(){

    }
}
