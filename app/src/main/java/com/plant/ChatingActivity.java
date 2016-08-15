package com.plant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kakao.usermgmt.response.model.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ChatingActivity extends Activity implements View.OnClickListener{
    ViewGroup.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    EditText inputContent;
    TextView chatingTimer;
    LinearLayout textBody;
    EditText chatingContent;
    ScrollView scroll;
    long time;
    ImageView sendBtn;
    ImageView backBtn;
    ImageView moreBtn;

    RoomData myRoomData;
    UserData myUserData;
    ArrayList<UserData> participatedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        Intent intent=getIntent();
        myRoomData=(RoomData) intent.getSerializableExtra("roomData");
        myUserData=(UserData)intent.getSerializableExtra("userData");
        participatedUser=(ArrayList<UserData>)intent.getSerializableExtra("participated");
        for(int i=0; i<participatedUser.size(); i++){
            Log.d("test",participatedUser.get(i).userID);
        }
        init();
    }
    void init(){
        if(myRoomData.roomID==-1){
            Log.d("ERROR","No Room Data Input");
            finish();
        }
        else{
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            String timeStr="";
            if(myRoomData.roomType==2){
                String day="날";
                time= myRoomData.startTime-Calendar.getInstance().getTimeInMillis();
                if(time>=24*60*60*1000){
                    day=(time/24*60*60*1000)+"날";
                    time=time%24*60*60*1000;
                }
                timeStr=format.format(new Date(time)) ;
                timeStr= timeStr.substring(0, 19);
                timeStr=day+timeStr;
            }
            else{
                time=1*60*1000;
                timeStr=format.format(new Date(time)) ;
            }

            chatingTimer=(TextView)findViewById(R.id.chatingTimer);
            chatingTimer.setText(timeStr);
            textBody=(LinearLayout)findViewById(R.id.textBody);
            (sendBtn=(ImageView)findViewById(R.id.sendBtn)).setOnClickListener(this);
            (backBtn=(ImageView)findViewById(R.id.backBtn)).setOnClickListener(this);
            (moreBtn=(ImageView)findViewById(R.id.chatingMore)).setOnClickListener(this);
        }
        scroll=(ScrollView)findViewById(R.id.scroll);
        chatingContent=(EditText)findViewById(R.id.chatingContents);
        chatingContent.setOnFocusChangeListener(new MyFocusChangeListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                Random r=new Random();
                if(r.nextBoolean()){
                    textBody.addView(new MyTextView(this,"123123123123123123").getLayout());
                }
                else
                    textBody.addView(new OtherTextView(this,"12312312312312312312").getLayout());
                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.fullScroll(View.FOCUS_DOWN);
                    }
                });
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.chatingMore:
                break;

        }
    }
    private class MyFocusChangeListener implements View.OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus){
            if(v.getId() == R.id.chatingContents && !hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
