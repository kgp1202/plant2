package com.plant;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kim on 2016-07-25.
 */
public class FindUserDataFromRoomData extends AsyncTask<String, Void, Void> {
    public static final String SearchURL = "http://www.plan-t.kr/findUserDataFromRoomData.php";
    private ArrayList<UserData> userDataList = new ArrayList<UserData>();

    RoomDataListViewOnItemClickListener parent;

    public FindUserDataFromRoomData(RoomDataListViewOnItemClickListener input){
        parent = input;
    }

    @Override
    protected Void doInBackground(String... params) {
        String roomID = params[0];
        StringBuilder jsonResult = new StringBuilder();
        try {
            URL url = new URL(SearchURL + "?roomID=" + roomID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                while ( true ) {
                    String line = br.readLine();
                    if (line == null)
                        break;
                    jsonResult.append(line + "\n");

                    userDataList.add(new Gson().fromJson(line, UserData.class));
                }
                br.close();
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        parent.userDataLodingComplete = true;
        parent.participateUserData = userDataList;

        return null;
    }
}
