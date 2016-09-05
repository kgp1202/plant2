package com.plant;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.util.Log;

import com.plant.Kakao.GlobalApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by angks on 2016-05-19.
 */
public class HttpRequest extends Thread {
    HttpURLConnection conn;
    boolean uriExi;
    String query="";
    JSONObject object=new JSONObject();
    Context mContext;

    public String requestResult="";
    URL url;

    public HttpRequest(String inputUrl){
        uriExi=false;
        Log.d("url",inputUrl);
        try {
            url=new URL(inputUrl);
            conn=(HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(7000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
        } catch (MalformedURLException e) {
            Log.d("test","URL casting error!");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void makeQuery(JSONObject input){
        Iterator iterator=input.keys();
        uriExi=true;
        while(iterator.hasNext()){
            String key=(String)iterator.next();
            try {
                String value=input.getString(key);
                object.put(key,value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static Boolean isInternetConnected(Context context){
        if(InternetFailDIalog.checkInternetConnection(context) == false) {
            Log.d("Internet fail", "conetect X");
            InternetFailDIalog internetFailDIalog = new InternetFailDIalog(context);
            internetFailDIalog.show();

            return false;
        }else { return true; }
    }

    public void setContext(Context context){ mContext = context; }
    @Override
    public void run(){
        try {
            if(mContext != null){
                ((GlobalApplication)((Activity)mContext).getApplication()).setCurrentRequest(this);
            }
            if (uriExi) {
                query = object.toString();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
            }
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //Log.d("httpRequest","done");
                requestResult = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String temp2;
                while ((temp2 = br.readLine()) != null) {
                    temp2+="\n";
                    requestResult += temp2;
                }
                Log.d("request Result", requestResult);
            }

            if(mContext != null){
                ((GlobalApplication)((Activity)mContext).getApplication()).clearCurrentRequest();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}