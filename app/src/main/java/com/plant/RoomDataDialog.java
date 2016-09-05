package com.plant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

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
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView topText1 = (TextView) findViewById(R.id.dialog_detail_top_text1);
        TextView topText2 = (TextView) findViewById(R.id.dialog_detail_top_text2);

        dialogViewPager = (ViewPager) findViewById(R.id.dialog_detail_viewpager);
        mViewPagerAdapter = new DialogViewPagerAdapter(mContext, dialogViewPager, this);
        dialogViewPager.setAdapter(mViewPagerAdapter);


        ImageView dialogDetailCancelBtn = (ImageView) findViewById(R.id.dialog_detail_cancel_btn);
        dialogDetailCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        switch(mode){
            case DIALOG_MODE_JOIN:
                topText1.setText("이 매칭에");
                topText2.setText("참여하시겠습니까?");

                break;
            case DIALOG_MODE_CHECK:
                topText1.setText("방 정보");
                topText2.setText("Room Infomation");

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

    public void openChatingActivity(){
        Intent intent = new Intent(mContext, ChatingActivity.class);
        intent.putExtra("userData", ((FrameActivity)mContext).userData);
        intent.putExtra("roomData",roomData);
        intent.putExtra("participated",findUserDataFromRoomData.participateUserData);
        intent.putExtra("withNumber",findUserDataFromRoomData.withNumber);
        dismiss();
        mContext.startActivity(intent);
    }

    public void show(final RoomData showedRoomData) {
        roomData = showedRoomData;
        dialogViewPager.setCurrentItem(0);

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
                            final BasicDialog basicDialog = new BasicDialog(mContext, BasicDialog.LIST_MODE);
                            basicDialog.title.setText("동행 인원을 선택해주세요.");
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int position = 0;
                                    switch(v.getId()){
                                        case R.id.dialog_detail_list_item1:
                                            position = 1;
                                            break;
                                        case R.id.dialog_detail_list_item2:
                                            position = 2;
                                            break;
                                        case R.id.dialog_detail_list_item3:
                                            position = 3;
                                            break;
                                    }
                                    RoomJoinPHP roomJoinPHP = new RoomJoinPHP();
                                    roomJoinPHP.execute("" + roomData.roomID, ((FrameActivity) mContext).userData.userID, "" + position);
                                    basicDialog.dismiss();
                                }
                            };
                            ((Button)basicDialog.findViewById(R.id.dialog_detail_list_item1)).setOnClickListener(clickListener);
                            ((Button)basicDialog.findViewById(R.id.dialog_detail_list_item2)).setOnClickListener(clickListener);
                            ((Button)basicDialog.findViewById(R.id.dialog_detail_list_item3)).setOnClickListener(clickListener);
                            ((Button)basicDialog.findViewById(R.id.dialog_detail_cancel_btn)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    basicDialog.dismiss();
                                }
                            });
                            basicDialog.show();
                        }
                    }
                });
                break;
            case DIALOG_MODE_CHECK:
                findViewById(R.id.dialog_detail_chating_join).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChatingActivity();
                    }
                });
                findViewById(R.id.dialog_detail_out).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final BasicDialog basicDialog = new BasicDialog(mContext, BasicDialog.TEXT_MODE);
                        basicDialog.title.setVisibility(View.GONE);
                        basicDialog.content.setText("정말로 방을 나가시겠습니까?");
                        basicDialog.yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserData myUser=((FrameActivity)mContext).userData;
                                HttpRequest request;
                                if(roomData.roomType == RoomData.ROOM_TYPE_REALTIME){
                                    request=new HttpRequest("http://plan-t.kr/outRoomRealTime.php?userID="+myUser.userID+"&roomID="+roomData.roomID);
                                    if(HttpRequest.isInternetConnected(mContext)){
                                        Thread t = new Thread(request);
                                        t.run();
                                        try {
                                            t.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    } else return;
                                }else {
                                    HttpRequest request2 =new HttpRequest("http://plan-t.kr/outRoom.php?userID="+myUser.userID+"&roomID="+roomData.roomID);
                                    if(HttpRequest.isInternetConnected(mContext)){
                                        request2.start();
                                    } else {
                                        return;
                                    }
                                }

                                final HttpRequest myRequest=new HttpRequest("http://plan-t.kr/chating/insertChating.php");
                                JSONObject json=new JSONObject();
                                try{
                                    json.put("userID",myUser.userID);
                                    json.put("roomID",roomData.roomID);
                                    json.put("content",myUser.name+"님이 나가셨습니다!");
                                }catch (Exception e){
                                    e.getStackTrace();
                                }
                                myRequest.makeQuery(json);
                                if(HttpRequest.isInternetConnected(mContext)){
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            myRequest.run();
                                        }
                                    }.start();
                                }else return;

                                basicDialog.dismiss();     //닫기
                                dismiss();
                                ((FrameActivity)mContext).makeChange(3);

                            }
                        });
                        basicDialog.noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                basicDialog.dismiss();
                            }
                        });
                        basicDialog.show();
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

            roomJoinRequest = new HttpRequest( roomJoinURL + "?userID=" + userID + "&roomID=" + roomID + "&number=" + number);
            if(HttpRequest.isInternetConnected(mContext)){
                roomJoinRequest.start();
                try {
                    roomJoinRequest.join();
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
