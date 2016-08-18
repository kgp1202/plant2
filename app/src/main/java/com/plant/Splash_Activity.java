package com.plant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Splash_Activity extends Activity {
    LoginPHP loginPHP;

    boolean isPastLogin;

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;

                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        }
        catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            Log.e("name not found", e1.toString());
        }

        catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            Log.e("no such an algorithm", e.toString());
        }
        catch (Exception e){
            Log.e("exception", e.toString());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAppKeyHash();
        setContentView(R.layout.activity_splash_);

        checkPastLogin();

        if(!isPastLogin) {
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(Splash_Activity.this, Login_Activity.class));
                    finish();
                }
            }, secondsDelayed * 500);
        }
    }

    public void checkPastLogin(){
        SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
        isPastLogin = pref.getBoolean("isLogin", false);

        if(isPastLogin){
            /********* 보안문제 발생 가능************/
            UserData tempUserData = new UserData();
            tempUserData.userID = pref.getString("userID", "");
            tempUserData.loginFrom = pref.getInt("loginFrom", 0);

            loginPHP = new LoginPHP(this);
            loginPHP.execute(tempUserData);

            finish();
        }
    }


    /********** 권한 설정 다이얼로그 결과 ***************/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //granted
            //User의 ProfilePath에 존재하는 이미지를 다운로드 받는다.

            //Go to FrameActivity!!
            Intent intent=new Intent(this,FrameActivity.class);
            intent.putExtra("UserData", loginPHP.userData);
            intent.putExtra("RoomDataList", loginPHP.roomDataList);
            startActivity(intent);
        } else {
            //refuesd


        }
    }
}
