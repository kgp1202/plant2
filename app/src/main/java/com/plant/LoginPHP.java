package com.plant;

/**
 * Created by Kim on 2016-07-16.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/************* Login.php로 연결 ***********************/
//입력으로 UserData를 Parameter로 입력받아서 execute를 하면
//login.php로 연결하여 결과를 전역변수인 userData에 넣어준다.
// 그리고 그 값을 sharedPreference를 통해서
public class LoginPHP extends AsyncTask<UserData, Void, Void> {
    private String loginURL = "http://plan-t.kr/login.php";
    public UserData userData = new UserData();
    public ArrayList<RoomData> roomDataList = new ArrayList<RoomData>();
    private Context mContext;

    HttpRequest loginRequest;

    public LoginPHP(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(UserData... tempUserData) {
        loginRequest = new HttpRequest(mContext, loginURL);
        loginRequest.makeQuery(tempUserData[0].getUserDataJson());
        Thread t = new Thread(loginRequest);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(Void params) {
        String[] lines = loginRequest.requestResult.split(System.getProperty("line.separator"));

        try {
            userData.setUserDataFromJson(new JSONObject(lines[0]));

            for(int i = 1; i < lines.length; i++){
                RoomData tempRoomData = new Gson().fromJson(lines[0], RoomData.class);
                roomDataList.add(tempRoomData);
            }
            //SharedPreference에 userID와 loginFrom저장
            SharedPreferences preferences ;
            SharedPreferences pref = mContext.getSharedPreferences("UserData", mContext.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isLogin", true);
            editor.putString("userID", userData.userID);
            editor.putInt("loginFrom", userData.loginFrom);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent=new Intent(mContext,FrameActivity.class);
        intent.putExtra("UserData", userData);
        //intent.putExtra("RoomDataList", roomDataList);
        mContext.startActivity(intent);
        ((Splash_Activity)mContext).finish();
    }
}
/************* Login.php로 연결 END ***********************/
