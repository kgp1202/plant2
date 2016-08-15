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
public class LoginPHP extends AsyncTask<UserData, Void, String> {
    private String loginURL = "http://plan-t.kr/login.php";
    public UserData userData = new UserData();
    public ArrayList<RoomData> roomDataList = new ArrayList<RoomData>();
    private Context mContext;

    public LoginPHP(Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(UserData... tempUserData) {
        //tempUserData을 json형식으로 login,php에 접속하여 정보 존재 여부를 확인후 로그인 혹은 정보 생성
        StringBuilder jsonResult = new StringBuilder();
        try {
            URL loginObj = new URL(loginURL);
            HttpURLConnection conn = (HttpURLConnection) loginObj.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(2000);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(tempUserData[0].getUserDataJSONString().getBytes());
            outputStream.flush();

            if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                boolean isUserData = true;
                while ( true ) {
                    if(isUserData) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        jsonResult.append(line + "\n");
                        isUserData = false;
                    } else {
                        String line = br.readLine();
                        if ( line == null )
                            break;
                        jsonResult.append(line + "\n");
                        RoomData tempRoomData = new Gson().fromJson(line, RoomData.class);
                        roomDataList.add(tempRoomData);
                    }
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
        return jsonResult.toString();
    }

    protected void onPostExecute(String jsonResult) {
        //Set userData by using jsonResult
        try {
            userData.setUserDataFromJson(new JSONObject(jsonResult));
            Log.d("after", userData.getUserDataJSONString());

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

        //권한을 물어본다.
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            Log.d("ImageDownload", "Permission Denied");
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

        }else {
            Log.d("image path", " " + userData.profilePath);
            //User의 ProfilePath에 존재하는 이미지를 다운로드 받는다.
            ImageDownload imageDownload = new ImageDownload(mContext);
            imageDownload.execute(userData.profilePath);

            //Go to FrameActivity!!
            Intent intent=new Intent(mContext,FrameActivity.class);
            intent.putExtra("UserData", userData);
            intent.putExtra("RoomDataList", roomDataList);
            mContext.startActivity(intent);
        }
    }
}
/************* Login.php로 연결 END ***********************/
