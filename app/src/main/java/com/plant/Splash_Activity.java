package com.plant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.kakao.auth.Session;
import com.kakao.kakaotalk.KakaoTalkService;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.network.ErrorResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Splash_Activity extends Activity {
    LoginPHP loginPHP;
    boolean isPastLogin;
    private UserData pastLoginUserData;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_);

        mContext = this;

        //과거 로그인 여부를 확인.
        SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
        isPastLogin = pref.getBoolean("isLogin", false);

        if(isPastLogin){
            /********* 보안문제 발생 가능************/
            pastLoginUserData = new UserData();
            pastLoginUserData.userID = pref.getString("userID", "");
            pastLoginUserData.loginFrom = pref.getInt("loginFrom", 0);

            //카카오톡으로 로그인 시 프로필 정보 갱신.
            if(pastLoginUserData.loginFrom == UserData.KAKAO){
                Log.d("session", " " + Session.getCurrentSession().checkState());
                updateProfile();
            }else {
                loginPHP = new LoginPHP(this);
                loginPHP.execute(pastLoginUserData);
                finish();
            }
        }else {
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(Splash_Activity.this, Login_Activity.class));
                    finish();
                }
            }, secondsDelayed * 500);
        }
    }


    private void updateProfile() {
        if (InternetFailDIalog.checkInternetConnection(this) == false) {
            final InternetFailDIalog internetFailDIalog = new InternetFailDIalog(this);
            internetFailDIalog.okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    internetFailDIalog.dismiss();
                    finish();
                }
            });
            internetFailDIalog.show();
        } else {
            KakaoTalkService.requestProfile(new TalkResponseCallback<KakaoTalkProfile>() {

                @Override
                public void onSuccess(KakaoTalkProfile talkProfile) {
                    pastLoginUserData.name = talkProfile.getNickName();
                    pastLoginUserData.profilePath = talkProfile.getProfileImageUrl();
                    pastLoginUserData.thumbnailPath = talkProfile.getThumbnailUrl();
                    Log.d("update success", " " + pastLoginUserData.getUserDataJSONString());

                    loginPHP = new LoginPHP(mContext);
                    loginPHP.execute(pastLoginUserData);
                    finish();
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.d("a", "onSessionClosed");

                    int secondsDelayed = 1;
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            startActivity(new Intent(Splash_Activity.this, Login_Activity.class));
                            finish();
                        }
                    }, secondsDelayed * 500);
                }

                @Override
                public void onNotSignedUp() {
                    Log.d("a", "onNotSignedUp");
                }

                @Override
                public void onNotKakaoTalkUser() {
                    Log.d("a", "onNotKakaoTalkUser");
                }
            });
        }
    }
}
