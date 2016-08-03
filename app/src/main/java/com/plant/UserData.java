package com.plant;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kim on 2016-07-13.
 */
public class UserData implements Serializable {
    public static final int FACEBOOK=1;
    public static final int KAKAO=2;
    public static final int NAVER=3;
    public String userID;
    public int loginFrom;
    public ArrayList<Long> reserveRoomID;
    public int point;
    public String name;
    public String profilePath;
    public JSONObject getUserDataJson(){
        Gson makeString=new Gson();
        JSONObject returnV=null;
        try {
            returnV=new JSONObject(makeString.toJson(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnV;
    }
    public String getUserDataJSONString(){
        Gson makeString=new Gson();
        return makeString.toJson(this);
    }
    public void setUserDataFromJson(JSONObject input){
        Gson makeClasee=new Gson();
        UserData temp=new UserData();
        temp=makeClasee.fromJson(input.toString(),UserData.class);
        this.userID=temp.userID;
        this.loginFrom=temp.loginFrom;
        this.profilePath=temp.profilePath;
        this.point=temp.point;
        this.reserveRoomID=temp.reserveRoomID;
        this.point=temp.point;
        this.name=temp.name;
    }
    public String getDecodedProfilePath(){
        return profilePath.replace('/', '_');
    }
}
