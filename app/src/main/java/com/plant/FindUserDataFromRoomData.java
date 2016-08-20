package com.plant;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kim on 2016-07-25.
 */
public class FindUserDataFromRoomData extends AsyncTask<String, Void, ArrayList<UserData>> {
    public static final String SearchURL = "http://www.plan-t.kr/findUserDataFromRoomData.php";
    public ArrayList<UserData> participateUserData = new ArrayList<UserData>();
    public ArrayList<Integer> withNumber = new ArrayList<Integer>();
    public boolean isComplete = false;

    public FindUserDataFromRoomData() {    }

    @Override
    protected ArrayList<UserData> doInBackground(String... params) {
        String roomID = params[0];
        //StringBuilder jsonResult = new StringBuilder();
        try {
            URL url = new URL(SearchURL + "?roomID=" + roomID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                while (true) {
                    String line = br.readLine();
                    if (line == null)
                        break;
                    //jsonResult.append(line + "\n");
                    participateUserData.add(new Gson().fromJson(line, UserData.class));
                    line = br.readLine();
                    withNumber.add(Integer.parseInt(line));
                }
                br.close();
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < participateUserData.size(); i++){
            Log.d("findUserData", " " + participateUserData.get(i).userID + " " + withNumber.get(i));
        }

        isComplete = true;
        return participateUserData;
    }
}
