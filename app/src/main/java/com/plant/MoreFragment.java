package com.plant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.kakaotalk.KakaoTalkService;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;


public class MoreFragment extends Fragment implements View.OnClickListener {
    private final String SAVE_FOLDER = "/save_folder";

    UserData userData;
    Context mContext;

    /***************** UI ***********************/
    View rootView;
    Button logout_btn;
    TextView point;
    ImageView profileImg;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_more, container, false);
        mContext = getContext();

        init();

        return rootView;
    }

    public void init(){
        /************************* UI ****************************/
        logout_btn = (Button) rootView.findViewById(R.id.logout_btn);
        point=(TextView)rootView.findViewById(R.id.myPoint);
        profileImg = (ImageView) rootView.findViewById(R.id.profile);

        logout_btn.setOnClickListener(this);


        /**************** UserData 받아오기 ************************/
        FrameActivity frameActivity = (FrameActivity) getActivity();
        userData = frameActivity.userData;
        point.setText("포인트 : "+userData.point+"");
        /**************** Profile Img 설정 *************************/
        if(!userData.profilePath.equals(""))
            Glide.with(mContext).load(userData.profilePath).into(profileImg);

    }

    @Override
    public void onClick(View v) {
        switch(userData.loginFrom) {
            case UserData.KAKAO:
                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(mContext, Login_Activity.class);
                        startActivity(intent);
                    }
                });
                break;
            case UserData.NAVER:
                OAuthLogin.getInstance().logoutAndDeleteToken(getContext());
                Intent intent = new Intent(mContext, Login_Activity.class);
                startActivity(intent);
                break;
            default:
                Log.d("Error", "UserData.loginFrom is not defined");
                break;
        }

        //SharedPreference에 저장되어 있던 정보 삭제
        SharedPreferences pref = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}