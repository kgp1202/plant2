package com.plant;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kakao.usermgmt.response.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class ChatingActivity extends Activity implements View.OnClickListener{
    ViewGroup.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    TextView chatingTimer;
    ListView textBody;
    EditText chatingContent;
    long time;
    ImageView sendBtn;
    ImageView backBtn;
    ImageView moreBtn;

    RoomData myRoomData;
    UserData myUserData;
    ArrayList<UserData> participatedUser;
    ArrayList<Integer> withNumber;
    ChatingListViewAdapter adapter;

    getData myChatingThread;
    boolean end=false;
    int id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        Intent intent=getIntent();
        myRoomData=(RoomData) intent.getSerializableExtra("roomData");
        myUserData=(UserData)intent.getSerializableExtra("userData");
        participatedUser=(ArrayList<UserData>)intent.getSerializableExtra("participated");
        withNumber=(ArrayList<Integer>)intent.getSerializableExtra("withNumber");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window=this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#77361a"));
        }
        init();
        initData();
        threadStart();
        textBody.setAdapter(adapter);
    }
    void init(){
        if(myRoomData.roomID==-1){
            Log.d("ERROR","No Room Data Input");
            finish();
        }
        else{
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss + dd일");
            Date d=new Date(myRoomData.startTime-Calendar.getInstance().getTimeInMillis());
            String timeStr=format.format(d);
            chatingTimer=(TextView)findViewById(R.id.chatingTimer);
            chatingTimer.setText(timeStr);
            textBody=(ListView) findViewById(R.id.textBody);
            (sendBtn=(ImageView)findViewById(R.id.sendBtn)).setOnClickListener(this);
            (backBtn=(ImageView)findViewById(R.id.backBtn)).setOnClickListener(this);
            (moreBtn=(ImageView)findViewById(R.id.chatingMore)).setOnClickListener(this);
        }
        chatingContent=(EditText)findViewById(R.id.chatingContents);
        chatingContent.setOnFocusChangeListener(new MyFocusChangeListener());
    }
    void initData(){
        adapter=new ChatingListViewAdapter(this,myUserData);
        try {
            String getData= new getFirstData().execute("http://www.plan-t.kr/chating/getFirstChating.php?roomID="+myRoomData.roomID).get();
            JSONObject myJsonObject;
            try {
                myJsonObject=new JSONObject(getData);
                for(int i=0; i<myJsonObject.length(); i++){
                    JSONObject obj=new JSONObject(myJsonObject.getString((id+1)+""));
                    int num=getUserDataFromParticipates(obj.getString("userID"));
                    UserData temp=participatedUser.get(num);
                    String content= URLDecoder.decode(obj.getString("content"),"euc-kr");
                    obj.remove("content");
                    obj.put("content",content);
                    obj.put("profile",temp.thumbnailPath);
                    obj.put("userNum",withNumber.get(num));
                    obj.put("name",temp.name);
                    adapter.myJsonObjectList.add(obj);
                    id++;
                   // Log.d("obj",obj.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    void threadStart(){
        myChatingThread= new getData();
        myChatingThread.execute();
        timeStart();
    }
    int getUserDataFromParticipates(String ID){
        for(int i=0; i<participatedUser.size(); i++){
            if(participatedUser.get(i).userID.equals(ID))
                return i;
        }
        return 0;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                String input=chatingContent.getText().toString();
                chatingContent.setText("");
                HttpRequest myRequest=new HttpRequest("http://plan-t.kr/chating/insertChating.php");
                JSONObject json=new JSONObject();
                try{
                    json.put("userID",myUserData.userID);
                    json.put("roomID",myRoomData.roomID);
                    json.put("content",input);
                }catch (Exception e){
                    e.getStackTrace();
                }
                myRequest.makeQuery(json);
                new Thread(myRequest).start();
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
    class getFirstData extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            String returnV="";
            HttpRequest httpRequest=new HttpRequest(params[0]);
            Thread t=new Thread(httpRequest);
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            returnV=httpRequest.line;
            return returnV;
        }
        @Override
        protected void onPostExecute(String params){
            Log.d("getFromChating",params);
        }
    }
    class getData extends AsyncTask<Void,String,String>{
        @Override
        protected String doInBackground(Void a[]) {
            String returnV="";
            while(!end){
                HttpRequest httpRequest=new HttpRequest("http://www.plan-t.kr/chating/getChating.php?roomID="+myRoomData.roomID+"&ID="+id);
                Thread t=new Thread(httpRequest);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(httpRequest.line);
                returnV=httpRequest.line;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return returnV;
        }
        @Override
        protected void onProgressUpdate(String ...params){
            JSONObject myJsonObject;
            try {
                myJsonObject=new JSONObject(params[0]);
                for(int i=0; i<myJsonObject.length(); i++){
                    JSONObject obj=new JSONObject(myJsonObject.getString((id+1)+""));
                    int num=getUserDataFromParticipates(obj.getString("userID"));
                    UserData temp=participatedUser.get(num);
                    String content= URLDecoder.decode(obj.getString("content"),"euc-kr");
                    obj.remove("content");
                    obj.put("content",content);
                    obj.put("profile",temp.profilePath);
                    obj.put("userNum",withNumber.get(num));
                    obj.put("name",temp.name);
                    adapter.myJsonObjectList.add(obj);
                    adapter.notifyDataSetChanged();
                    textBody.setSelection(adapter.getCount()-1);
                    id++;
                    // Log.d("obj",obj.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private TimerTask second;
    private final Handler handler = new Handler();
    long timer_sec;
    Timer timer = new Timer();
    String dayCnt="";
    public void timeStart() {
        second = new TimerTask() {
            @Override
            public void run() {
                Update();
                Calendar tempC=Calendar.getInstance();
                Calendar nowC=Calendar.getInstance();
                timer_sec=myRoomData.startTime-nowC.getTimeInMillis();
                tempC.setTimeInMillis(myRoomData.startTime);
                if(tempC.get(Calendar.MONTH)-nowC.get(Calendar.MONTH)>0)
                    dayCnt="7일+";
                else{
                    int c=tempC.get(Calendar.DAY_OF_MONTH)-nowC.get(Calendar.DAY_OF_MONTH);
                    if((c)>7)
                        dayCnt="7일+";
                    else
                        dayCnt=c+"일";
                }
            }
        };
        timer.schedule(second, 0, 1000);
    }

    protected void Update() {
        Runnable updater = new Runnable() {
            public void run() {
                if(timer_sec<=0){
                    finish();
                }
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss / ");
                chatingTimer.setText(format.format(timer_sec)+dayCnt);
            }
        };
        handler.post(updater);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        end=true;
        timer.cancel();
    }
}
