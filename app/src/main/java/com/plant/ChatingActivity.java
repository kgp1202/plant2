package com.plant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import java.util.Random;
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
                DateFormat sdFormat = new SimpleDateFormat("HH:mm:ss");
                Date tempDate=new Date();
                try {
                    tempDate = sdFormat.parse("00:01:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timeStr=format.format(tempDate);
            }

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
                    obj.put("profile",temp.profilePath);
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
            new Thread(httpRequest).start();
            while(!httpRequest.isFinish){};
            returnV=httpRequest.line;
            return returnV;
        }
        @Override
        protected void onPostExecute(String params){
            Log.d("getFromChating",params);
        }
    }
    class getData extends AsyncTask<Void,String,String>{
        boolean endActivity=false;
        @Override
        protected String doInBackground(Void a[]) {
            String returnV="";
            while(!endActivity){
                HttpRequest httpRequest=new HttpRequest("http://www.plan-t.kr/chating/getChating.php?roomID="+myRoomData.roomID+"&ID="+id);
                new Thread(httpRequest).start();
                while(!httpRequest.isFinish){};
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
                    id++;
                    // Log.d("obj",obj.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        myChatingThread.endActivity=true;
    }
}
