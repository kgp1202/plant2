package com.plant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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
    RoomData matchingR = new RoomData();
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
            final BasicDialog basicDialog = new BasicDialog(mContext, BasicDialog.TEXT_MODE);
            basicDialog.title.setVisibility(View.GONE);
            basicDialog.noButton.setVisibility(View.GONE);
            basicDialog.content.setText("패널티로인해 "+-1*userData.point*30+"초 지연됩니다.");
            basicDialog.yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    basicDialog.dismiss();
                }
            });
            basicDialog.show();
//            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
//            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();     //닫기
//                }
//            });
//            alert.setMessage("패널티로인해 "+-1*userData.point*30+"초 지연됩니다.");
//            alert.show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpRequest myRequest = new HttpRequest("http://plan-t.kr/queue/userMatching.php");
        myRequest.makeQuery(userData.getUserDataJson());
        myRequest.makeQuery(roomData.getRoomDataJson());
        if(HttpRequest.isInternetConnected(mContext)){
            myRequest.start();
            try {
                myRequest.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            do {
                try {
                    Thread.sleep(300);
                    myRequest = new HttpRequest("http://plan-t.kr/queue/checkMatching.php?ID=" + userData.userID);
                    myRequest.start();
                    myRequest.join();
                    JSONObject json=new JSONObject(myRequest.requestResult);
                    matchingR.setRoomDataFromJson(json);
                    matchingR.destPoint = URLDecoder.decode(matchingR.destPoint, "euc-kr");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while  (myRequest.requestResult.equals("false\n")&&(!isCancelled()));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        isFinish = true;
        myTrigger.makeDarker(false);
        myTrigger.getResultFromThread(matchingR);
    }

    @Override
    public void onCancelled(Void params) {
        super.onCancelled(params);
        HttpRequest myThread = new HttpRequest("http://plan-t.kr/queue/cancellableCheck.php?ID=" + userData.userID);
        if(HttpRequest.isInternetConnected(mContext)){
            myThread.start();
            try {
                myThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (myThread.requestResult.equals(false)) {
                Log.d("onCancelled", "failed");
            } else {
                Log.d("onCancelled", "success");
            }
            matchingR.roomID = -1;
            isFinish = true;
            myTrigger.makeDarker(false);
            myTrigger.getResultFromThread(matchingR);
        }
    }
};