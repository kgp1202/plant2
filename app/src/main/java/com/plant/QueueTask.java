package com.plant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URLDecoder;

/**
 * Created by angks on 2016-05-25.
 */
class QueueTask extends AsyncTask<Void, Void, Void> {
    Context mContext;
    ActivityMakeDarker myTrigger;
    RoomData roomData = new RoomData();
    UserData userData = new UserData();
    boolean isFinish;
    RoomData matchingR=new RoomData();
    public boolean isSucess;
    boolean isCancellable = false;

    public QueueTask(Context context, ActivityMakeDarker input) {
        mContext = context;
        myTrigger = input;
    }

    public void setRoomData(RoomData inputR) {
        roomData = inputR;
    }

    public void setUserData(UserData inputU) {
        userData = inputU;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isFinish=false;
        if(userData.point<0){
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            alert.setMessage("패널티로인해 "+-1*userData.point*30+"초 지연됩니다.");
            alert.show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
            /*Queue를 잡는다**********************/
        if(userData.point<0){
            try {
                Thread.sleep(-1*userData.point*30*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HttpRequest myRequest = new HttpRequest(mContext, "http://plan-t.kr/queue/userMatching.php");
        myRequest.makeQuery(userData.getUserDataJson());
        myRequest.makeQuery(roomData.getRoomDataJson());
        Thread t=new Thread(myRequest);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        do {
            try {
                Thread.sleep(300);
                myRequest = new HttpRequest(mContext, "http://plan-t.kr/queue/checkMatching.php?ID=" + userData.userID);
                Thread t2=new Thread(myRequest);
                t2.start();
                t2.join();
                JSONObject json=new JSONObject(myRequest.requestResult);
                matchingR.setRoomDataFromJson(json);
                matchingR.destPoint=URLDecoder.decode(matchingR.destPoint,"euc-kr");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while ((myRequest.requestResult.equals("false")) && (!isCancelled()));
        /*************************************/
        //if(myRequest.line.equals("sucess")){
        //   isSucess=true;
        //}
        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        isFinish=true;
        myTrigger.makeDarker(false);
        myTrigger.getResultFromThread(matchingR);
    }

    @Override
    public void onCancelled(Void params) {
        super.onCancelled(params);
        HttpRequest myThread = new HttpRequest(mContext, "http://plan-t.kr/queue/cancellableCheck.php?ID=" + userData.userID);

        if(myThread.isInternetConnected() == true){
            Thread t = new Thread(myThread);
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (myThread.requestResult.equals(false)) {
                Log.d("onCancelled", "failed");
            } else {
                Log.d("onCancelled", "success");
            }
            matchingR.roomID = -1;
        }
        else {
            isFinish = true;
            myTrigger.makeDarker(false);
            myTrigger.getResultFromThread(matchingR);
        }
    }
};