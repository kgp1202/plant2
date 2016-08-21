package com.plant;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URLDecoder;

/**
 * Created by angks on 2016-05-25.
 */
class QueueTask extends AsyncTask<Void, Void, Void> {
    ActivityMakeDarker myTrigger;
    RoomData roomData = new RoomData();
    UserData userData = new UserData();
    boolean isFinish;
    RoomData matchingR=new RoomData();
    public boolean isSucess;
    boolean isCancellable = false;

    public QueueTask(ActivityMakeDarker input) {
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
    }

    @Override
    protected Void doInBackground(Void... params) {
            /*Queue를 잡는다**********************/
        HttpRequest myRequest = new HttpRequest("http://plan-t.kr/queue/userMatching.php");
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
                myRequest = new HttpRequest("http://plan-t.kr/queue/checkMatching.php?ID=" + userData.userID);
                new Thread(myRequest).start();
                while (!myRequest.isFinish) {
                }
                JSONObject json=new JSONObject(myRequest.line);
                matchingR.setRoomDataFromJson(json);
                matchingR.destPoint=URLDecoder.decode(matchingR.destPoint,"euc-kr");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while ((myRequest.line.equals("false")) && (!isCancelled()));
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
        HttpRequest myThread = new HttpRequest("http://plan-t.kr/queue/cancellableCheck.php?ID=" + userData.userID);
        new Thread(myThread).start();
        while (!myThread.isFinish) {
        }
        ;
        if (myThread.line.equals(false)) {
            Log.d("onCancelled", "failed");
        } else {
            Log.d("onCancelled", "success");
        }
        isFinish=true;
        myTrigger.makeDarker(false);
        matchingR.roomID=-1;
        myTrigger.getResultFromThread(matchingR);
    }
};