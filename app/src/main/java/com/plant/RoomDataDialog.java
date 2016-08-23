package com.plant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kim on 2016-08-20.
 */
public class RoomDataDialog extends Dialog {
    public static final int DIALOG_MODE_JOIN = 1;
    public static final int DIALOG_MODE_CHECK = 2;

    Context mContext;
    public int mode;

    public ViewPager dialogViewPager;
    public DialogViewPagerAdapter mViewPagerAdapter;

    public RoomData roomData;

    public RoomDataDialog(Context context, int mode){
        super(context);

        mContext = context;
        this.mode = mode;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_detail);
        setCancelable(true);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        dialogViewPager = (ViewPager) findViewById(R.id.dialog_detail_viewpager);
        mViewPagerAdapter = new DialogViewPagerAdapter(mContext, dialogViewPager, this);
        dialogViewPager.setAdapter(mViewPagerAdapter);

        ImageView dialogDetailTopImg = (ImageView) findViewById(R.id.dialog_detail_top_img);
        ImageView dialogDetailCancelBtn = (ImageView) findViewById(R.id.dialog_detail_cancel_btn);
        dialogDetailCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        switch(mode){
            case DIALOG_MODE_JOIN:

                break;
            case DIALOG_MODE_CHECK:
                dialogDetailTopImg.setImageResource(R.drawable.dialog_detail_check_head);

                LinearLayout dialog_detail_bottom_layout = (LinearLayout) findViewById(R.id.dialog_detail_bottom_layout);
                dialog_detail_bottom_layout.removeAllViews();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout test = (LinearLayout) inflater.inflate(R.layout.dialog_detail_out_button, null);
                dialog_detail_bottom_layout.addView(test);

                setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
                break;
        }
    }

    public FindUserDataFromRoomData findUserDataFromRoomData;

    public void show(final RoomData showedRoomData) {
        roomData = showedRoomData;

        //참여하고 있는 userData를 얻어온다.
        findUserDataFromRoomData = new FindUserDataFromRoomData(mContext);
        if(HttpRequest.isInternetConnected(mContext)){
            findUserDataFromRoomData.run(Long.toString(roomData.roomID));
        }
        else {
            return;
        }

        mViewPagerAdapter.setFindUserDataFromRoomData(findUserDataFromRoomData);
        mViewPagerAdapter.setRoomData(roomData);

        switch(mode){
            case DIALOG_MODE_JOIN:
                findViewById(R.id.dialog_detail_join_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //이미 참여 중인지 여부 판단
                        boolean isJoining = false;
                        for(int i = 0; i < findUserDataFromRoomData.participateUserData.size(); i++){
                            if(findUserDataFromRoomData.participateUserData.get(i).userID.equals(((FrameActivity)mContext).userData.userID)){
                                isJoining = true;
                                break;
                            }
                        }

                        if(isJoining){
                            Toast.makeText(mContext, "이 방에는 이미 참여중입니다!", Toast.LENGTH_SHORT).show();
                        } else if(roomData.userNum == roomData.maxUserNum){
                            Toast.makeText(mContext, "이 방은 정원이 가득 찼습니다!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //동행인원을 물어보는 다이얼로그 띄우기
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog);
                            alert.setTitle("동행 인원을 선택해주세요.");

                            final String[] item = {"동행인원 없음", "1명과 동행", "2명과 동행"};
                            alert.setItems(item, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int selectedItem) {
                                    int number = selectedItem + 1;
                                    RoomJoinPHP roomJoinPHP = new RoomJoinPHP();
                                    if(HttpRequest.isInternetConnected(getContext())) {
                                        roomJoinPHP.execute("" + roomData.roomID, ((FrameActivity) mContext).userData.userID, "" + number);
                                        HttpRequest myRequest=new HttpRequest(mContext, "http://plan-t.kr/chating/insertChating.php");
                                        JSONObject json=new JSONObject();
                                        try{
                                            json.put("userID",((FrameActivity) mContext).userData.userID);
                                            json.put("roomID",roomData.roomID);
                                            json.put("content",((FrameActivity) mContext).userData.name+"님이 참여하셨습니다!");
                                        }catch (Exception e){
                                            e.getStackTrace();
                                        }
                                        myRequest.makeQuery(json);
                                        Thread t=new Thread(myRequest);
                                        t.start();
                                    }
                                    else {
                                        //인터넷 연결이 안되어 있을 떄의 처리.
                                    }
                                }
                            });
                            alert.show();
                        }
                    }
                });
                break;
            case DIALOG_MODE_CHECK:
                findViewById(R.id.dialog_detail_chating_join).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ChatingActivity.class);
                        intent.putExtra("userData", ((FrameActivity)mContext).userData);
                        intent.putExtra("roomData", roomData);
                        intent.putExtra("participated",findUserDataFromRoomData.participateUserData);
                        intent.putExtra("withNumber",findUserDataFromRoomData.withNumber);
                        mContext.startActivity(intent);
                    }
                });
                findViewById(R.id.dialog_detail_out).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog);
                        //alert.setTitle("방 나가기");
                        alert.setMessage("정말로 방을 나가시겠습니까?");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserData myUser=((FrameActivity)mContext).userData;
                                HttpRequest request;
                                if(roomData.roomType == RoomData.ROOM_TYPE_REALTIME){
                                    request=new HttpRequest(mContext, "http://plan-t.kr/outRoomRealTime.php?userID="+myUser.userID+"&roomID="+roomData.roomID);
                                    Thread t=new Thread(request);
                                    if(request.isInternetConnected(mContext) == true){
                                        t.start();
                                        try {
                                            t.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        return;
                                    }
                                }else {
                                    request=new HttpRequest(mContext, "http://plan-t.kr/outRoom.php?userID="+myUser.userID+"&roomID="+roomData.roomID);
                                    Thread t=new Thread(request);
                                    if(request.isInternetConnected(mContext) == true){
                                        t.start();
                                        try {
                                            t.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        return;
                                    }
                                }

                                HttpRequest myRequest=new HttpRequest(mContext, "http://plan-t.kr/chating/insertChating.php");
                                JSONObject json=new JSONObject();
                                try{
                                    json.put("userID",myUser.userID);
                                    json.put("roomID",roomData.roomID);
                                    json.put("content",myUser.name+"님이 나가셨습니다!");
                                }catch (Exception e){
                                    e.getStackTrace();
                                }
                                myRequest.makeQuery(json);
                                Thread t=new Thread(myRequest);
                                if(myRequest.isInternetConnected(mContext) == true) {
                                    t.start();
                                    try {
                                        t.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                 }

                                dialog.dismiss();     //닫기
                                dismiss();
                                ((ReservationCheckFragment)((FrameActivity)mContext).fragment).roomDataList.remove(roomData);
                                ((ReservationCheckFragment)((FrameActivity)mContext).fragment).reservation_listView_adapter.notifyDataSetChanged();
                            }
                        });
                        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                        alert.show();
                    }
                });

                break;
        }

        super.show();
    }

    private class RoomJoinPHP extends AsyncTask<String, Void, Void> {
        private static final int ROOM_JOIN_SUCCESS = 0;
        private static final int ROOM_JOIN_FAIL = 1;

        private final static String roomJoinURL = "http://plan-t.kr/roomJoin.php";
        private HttpRequest roomJoinRequest;

        @Override
        protected Void doInBackground(String... params) {
            String roomID = params[0];
            String userID = params[1];
            String number = params[2];

            roomJoinRequest = new HttpRequest(mContext, roomJoinURL + "?userID=" + userID + "&roomID=" + roomID + "&number=" + number);
            Thread t = new Thread(roomJoinRequest);
            if(roomJoinRequest.isInternetConnected(mContext)){
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            String[] lines = roomJoinRequest.requestResult.split(System.getProperty("line.separator"));

            if(Integer.parseInt(lines[0]) == ROOM_JOIN_SUCCESS){
                dismiss();
                ((FrameActivity) mContext).makeChange(3);
            } else if(Integer.parseInt(lines[0]) == ROOM_JOIN_FAIL){
                Toast.makeText(mContext, "해당 방의 최대 참여 가능 유저의 수를 초과합니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
