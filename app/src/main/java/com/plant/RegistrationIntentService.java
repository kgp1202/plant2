package com.plant;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.plant.Kakao.GlobalApplication;

import java.io.IOException;

/**
 * Created by Kim on 2016-08-25.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * GCM을 위한 Instance ID의 토큰을 생성하여 가져온다.
     * @param intent
     */
    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {
        // GCM Instance ID의 토큰을 가져오는 작업이 시작되면 LocalBoardcast로 GENERATING 액션을 알려 ProgressBar가 동작하도록 한다.
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(QuickstartPreferences.REGISTRATION_GENERATING));

        // GCM을 위한 Instance ID를 가져온다.
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = null;
        try {
            synchronized (TAG) {
                // GCM 앱을 등록하고 획득한 설정파일인 google-services.json을 기반으로 SenderID를 자동으로 가져온다.
                String default_senderId = getString(R.string.gcm_defaultSenderId);
                // GCM 기본 scope는 "GCM"이다.
                String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;
                // Instance ID에 해당하는 토큰을 생성하여 가져온다.
                token = instanceID.getToken(default_senderId, scope, null);

                //현재 userData의 gcmClientID를 update한다.
                if(((GlobalApplication)getApplication()).userData.gcmClientID != null
                        && !((GlobalApplication)getApplication()).userData.gcmClientID.equals(token)){

                    Log.d("userData", " " + ((GlobalApplication)getApplication()).userData.gcmClientID + " " + token);

                    ((GlobalApplication)getApplication()).userData.gcmClientID = token;
                    //데이터 베이스도 갱신.
                    String gcmClientIDRestUrl = "http://plan-t.kr/gcmClientIDReset.php";
                    HttpRequest gcmClientIDResetRequest = new HttpRequest(
                            gcmClientIDRestUrl + "?userID=" + ((GlobalApplication)getApplication()).userData.userID
                            + "&gcmClientID=" + token);
                    gcmClientIDResetRequest.start();
                }
                if(((GlobalApplication)getApplication()).userData.gcmClientID == null){
                    ((GlobalApplication)getApplication()).userData.gcmClientID = token;
                }
                Log.i(TAG, "GCM Registration Token: " + token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
