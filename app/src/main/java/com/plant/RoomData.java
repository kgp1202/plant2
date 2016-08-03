package com.plant;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by angks on 2016-05-13.
 */

/*********** 추가 **************/
//방장 아이디 만들기.

public class RoomData implements Serializable{
    public static final int ROOM_TYPE_REALTIME = 1;
    public static final int ROOM_TYPE_RESERVE = 2;
    public static final int STARTING_POINT_BACK = 1;
    public static final int STARTING_POINT_FRONT = 2;
    public static final int STARTING_POINT_JUAN = 3;
    public static final int ROOM_OBJECT_CERTIFICATE = 0;
    public static final int ROOM_OBJECT_ENGLISH = 1;
    public static final int ROOM_OBJECT_ETC = 2;



    public long roomID;
    public String hostUserID;
    //public ArrayList<Long> memberUserID;
    public int userNum;
    public int maxUserNum;
    public int roomType;         // 1 = 실시간, 2= 예약
    public int startingPoint;   // 1 = 후문, 2 = 정문, 3 = 주안
    public String destPoint;
    public boolean round;//왕복
    public long startTime;//출발 날짜
    public int roomObject;//목적
    public String comment;
    public RoomData(){
        roomID=0;
        hostUserID = "";
        //durationTime=0;
        userNum=0;
        maxUserNum=0;
        startingPoint=0;
        destPoint="";
        round=false;
        roomObject=0;
        //memberUserID=new ArrayList<Long>();
        startTime= Calendar.getInstance().getTimeInMillis();
        comment="";
        //goal=0;
        roomType=0;
    }

    public void setRoomTimeData(int startYear, int startMonth, int startDay, int startHour, int startMinute){
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss");
        String monthStr = "" + startMonth;
        String dayStr = "" + startDay;
        String hourStr = "" + startHour;
        String minuteStr = "" + startMinute;
        if(monthStr.length() == 1)  monthStr = "0" + monthStr;
        if(dayStr.length() == 1)  dayStr = "0" + dayStr;
        if(hourStr.length() == 1)  hourStr = "0" + hourStr;
        if(minuteStr.length() == 1)  minuteStr = "0" + minuteStr;

        String timeStr = "" + startYear + "년 " + monthStr + "월 " + dayStr + "일 " + hourStr + ":" + minuteStr + ":00";

        try {
            Date date = format.parse(timeStr);
            this.startTime = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getRoomTimeData(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss");
        String timeStr = format.format(new Date(startTime));
        String result = timeStr.substring(0, 19);

        return result;
    }

    public JSONObject getRoomDataJson(){
        Gson makeJson=new Gson();
        JSONObject returnV=null;
        try {
            returnV=new JSONObject(makeJson.toJson(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnV;
    }

    public void setDestPoint(String input){
        input = input.trim();

        if(input.matches(".*여자중학교")){
            input.replace("여자중학교", "여중");
        } else  if(input.matches(".*중학교")){
            input.replace("중학교", "중");
        } else if(input.matches(".*공업고등학교")){
            input.replace("공업고등학교", "공고");
        } else if(input.matches(".*고")){
            input.replace("고등학교", "고");
        } else if(input.matches(".*예비군 훈련장")){
            input.replace(" 훈련장", "");
        }
        destPoint = input;
    }

    public String getDestPointToOutputForm(){
        String temp = destPoint;

        if(temp.matches(".*여중")){
            temp.replace("여중", "여자중학교");
        }else if(temp.matches(".*중")){
            temp += "학교";
        }else if(temp.matches(".*공고")){
            temp.replace("공고", "공업고등학교");
        }else if(temp.matches(".*고")){
            temp += "등학교";
        }else if(temp.matches(".*예비군")){
            temp += " 훈련장";
        }
        return temp;
    }



    public String getRoomDataJSONString(){
        Gson makeString=new Gson();
        return makeString.toJson(this);
    }

    public void setRoomDataFromJson(JSONObject input){
        Gson makeClasee=new Gson();
        RoomData temp=new RoomData();
        temp=makeClasee.fromJson(input.toString(),RoomData.class);
        this.roomID=temp.roomID;
        this.hostUserID =temp.hostUserID;
        //this.memberUserID=temp.memberUserID;
        //this.durationTime=temp.durationTime;
        this.userNum=temp.userNum;
        this.maxUserNum=temp.maxUserNum;
        this.startingPoint=temp.startingPoint;
        this.destPoint=temp.destPoint;
        this.round=temp.round;
        this.startTime=temp.startTime;
        this.roomObject=temp.roomObject;
        this.comment=temp.comment;
    }
}