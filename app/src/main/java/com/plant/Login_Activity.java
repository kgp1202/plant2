package com.plant;

import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Login_Activity extends Activity implements View.OnClickListener{
    /************* activity *************/
    ImageView btnFB;
    OAuthLoginButton btnNAV;
    com.plant.Kakao.KakaoBtnLayout btnKKO;

    Animation anim1;
    Animation anim2;
    /************* activity *************/

    LoginPHP loginPHP;

    private SessionCallback callback;

    public void init(){
        //UI를 정의하기 전에 SharedPreference를 이용해서
        //기존에 UserData가 존재하면 이 정보를 통해서 로그인.
        /*SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
        Boolean pasteIsLogin = pref.getBoolean("isLogin", false);
        Log.d("pastLogin", ""+pasteIsLogin);
        if(pasteIsLogin){
            *//********* 보안문제 발생 가능************//*
            UserData tempUserData = new UserData();
            tempUserData.userID = pref.getString("userID", "");
            tempUserData.loginFrom = pref.getInt("loginFrom", 0);

            LoginPHP loginPHP = new LoginPHP();
            loginPHP.execute(tempUserData);
        }*/

        btnFB=(ImageView)findViewById(R.id.btnFB);
        btnNAV = (OAuthLoginButton) findViewById(R.id.btnNAV);
        btnKKO=(com.plant.Kakao.KakaoBtnLayout)findViewById(R.id.btnKKO);

        btnFB.setOnClickListener(this);
        //btnNAV.setOnClickListener(this);
        btnKKO.setOnClickListener(this);

        anim1= AnimationUtils.loadAnimation(this,R.anim.scale_down);
        anim2 = AnimationUtils.loadAnimation(this,R.anim.scale_up);
    }

    /********** 권한 설정 다이얼로그 결과 ***************/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //granted
            //User의 ProfilePath에 존재하는 이미지를 다운로드 받는다.
            ImageDownload imageDownload = new ImageDownload(this);
            imageDownload.execute(loginPHP.userData.profilePath);

            //Go to FrameActivity!!
            Intent intent=new Intent(this,FrameActivity.class);
            intent.putExtra("UserData", loginPHP.userData);
            intent.putExtra("RoomDataList", loginPHP.roomDataList);
            startActivity(intent);
            finish();
        } else {
            //refuesd
        }
    }
    /********** 권한 설정 다이얼로그 결과 END ***********/

    /************* NAVER extend class  and function *************/
    private static String OAUTH_CLIENT_ID = "59HIuAACdfvIhVWZV2MD";
    private static String OAUTH_CLIENT_SECRET = "feFvP6XdVx";
    private static String OAUTH_CLIENT_NAME = "plan T";

    private static OAuthLogin mOAuthLoginInstance;
    private static Context mContext;

    private void initBtnNAV(){
        mContext = this;

        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

        btnNAV.setBgResourceId(R.color.colorMainOrange);
        btnNAV.setImageResource(R.drawable.login_nav);
        btnNAV.setOAuthLoginHandler(mOAuthLoginHandler);
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                new RequestApiTask().execute();     //사용자 정보 조회

            } else {
                Toast.makeText(mContext, mOAuthLoginInstance.getLastErrorCode(mContext) + " "
                        + mOAuthLoginInstance.getLastErrorDesc(mContext), Toast.LENGTH_SHORT).show();
            }
        };
    };

    //사용자 정보 조회를 통해서 UserData를 세팅한다.
    private class RequestApiTask extends AsyncTask<Void, Void, UserData> {
        String naverRequestURL = "https://openapi.naver.com/v1/nid/getUserProfile.xml";

        @Override
        protected void onPreExecute() { }
        @Override
        protected UserData doInBackground(Void... params) {
            //naverAPI를 통해서 userID와 이름을 검색한다.
            String at = mOAuthLoginInstance.getAccessToken(mContext);

            String naverRequestResult = mOAuthLoginInstance.requestApi(mContext, at, naverRequestURL);
            String[] arr = naverRequestResult.split("CDATA");

            Log.d("before ", naverRequestResult);
            UserData tempUserData = new UserData();
            tempUserData.loginFrom = 3; //NAVER
            for(int i = 0; i < arr.length; i++){
                if(arr[i].matches(".*</enc_id>.*")){
                    tempUserData.userID =  arr[i].substring(1, arr[i].indexOf("]"));
                } else if(arr[i].matches(".*</name>.*")){
                    tempUserData.name = arr[i].substring(1, arr[i].indexOf("]"));
                } else if(arr[i].matches(".*</profile_image>.*")){
                    tempUserData.profilePath = arr[i].substring(1, arr[i].indexOf("]"));
                }
            }

            Log.d("before", tempUserData.getUserDataJSONString());
            return tempUserData;
        }

        protected void onPostExecute(UserData tempUserData) {
            loginPHP = new LoginPHP(getBaseContext());
            loginPHP.execute(tempUserData);
        }
    }
    /************* NAVER extend class  and function END *************/

    /************* KAKAO extend class  and function *************/
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("onSessionOpened", " ");
            redirectSignupActivity();  // 세션 연결성공 시 redirectSignupActivity() 호출
        }
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d("onSessionOpenFailed", " ");
            if(exception != null) {
                Logger.e(exception);
            }
            //setContentView(R.layout.activity_login_); // 세션 연결이 실패했을때
        }                                              // 로그인화면을 다시 불러옴
    }
    protected void redirectSignupActivity() {       //세션 연결 성공 시 SignupActivity로 넘김
//        final Intent intent = new Intent(this, com.plant.Kakao.KakaoSignUp.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(intent);
//        finish();
        requestMe();
    }
    public void kakaoInit(){
        /*
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Log.d("test","logout success");
            }
        });
        */
        callback = new SessionCallback();                  // 이 두개의 함수 중요함
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }
    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {} // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                //여기서 user 데이터를 자기가 원하는 모양으로 입력
                String kakaoID = String.valueOf(userProfile.getId()); // userProfile에서 ID값을 가져옴
                String kakaoNickname = userProfile.getNickname();     // Nickname 값을 가져옴
                Log.d("test","onSuccess Login");

                UserData tempUserData =new UserData();
                tempUserData.userID = kakaoID;
                tempUserData.loginFrom= com.plant.UserData.KAKAO;
                tempUserData.name=userProfile.getNickname();
                tempUserData.profilePath=userProfile.getProfileImagePath();
                redirectMainActivity(tempUserData); // 로그인 성공시 MainActivity로
            }
        });
    }
    private void redirectMainActivity(UserData input) {
        LoginPHP loginPHP = new LoginPHP(this);
        loginPHP.execute(input);
    }
    protected void redirectLoginActivity() {
//        Log.d("test","session close or anything");
//        final Intent intent = new Intent(this, com.plant.Login_Activity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(intent);
        finish();
    }
    /************* KAKAO Class & Function END *************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        init(); //actvity init
        kakaoInit();//kakao Init;
        initBtnNAV();//naver init;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }
    @Override
    public void onClick(final View v) {
        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.startAnimation(anim2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                switch(v.getId()){
                    case R.id.btnFB:
                        Intent intent=new Intent(Login_Activity.this,FrameActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.btnKKO:
                        break;
                    case R.id.btnNAV:
                        break;
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        v.startAnimation(anim1);
    }
}
