package com.plant;

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

    public void show(final RoomData showedRoomData) {
        roomData = showedRoomData;

        //참여하고 있는 userData를 얻어온다.
        final FindUserDataFromRoomData findUserDataFromRoomData = new FindUserDataFromRoomData();
        findUserDataFromRoomData.execute(Long.toString(roomData.roomID));

        mViewPagerAdapter.setRoomData(roomData);
        mViewPagerAdapter.setFindUserDataFromRoomData(findUserDataFromRoomData);

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
                            Dialog roomJoinDialog = new Dialog(mContext);
                            roomJoinDialog.setContentView(R.layout.dialog_detail_roomjoin);
                            roomJoinDialog.setCancelable(true);

                            Button button1 = (Button) roomJoinDialog.findViewById(R.id.dialog_detail_roomjoin_one);
                            Button button2 = (Button) roomJoinDialog.findViewById(R.id.dialog_detail_roomjoin_two);
                            Button button3 = (Button) roomJoinDialog.findViewById(R.id.dialog_detail_roomjoin_three);

                            RoomJoinDialogListener roomJoinDialogListener = new RoomJoinDialogListener(roomJoinDialog);
                            button1.setOnClickListener(roomJoinDialogListener);
                            button2.setOnClickListener(roomJoinDialogListener);
                            button3.setOnClickListener(roomJoinDialogListener);

                            roomJoinDialog.show();
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
                        if(roomData.roomType == RoomData.ROOM_TYPE_REALTIME){

                        }else {

                        }
                    }
                });

                break;
        }

        super.show();
    }

    private class RoomJoinDialogListener implements View.OnClickListener {
        Dialog roomJoinDialog;

        public RoomJoinDialogListener(Dialog roomJoinDialog){
            this.roomJoinDialog = roomJoinDialog;
        }

        @Override
        public void onClick(View v) {
            int number = 0;
            switch(v.getId()){
                case R.id.dialog_detail_roomjoin_one:
                    number = 1;
                    break;
                case R.id.dialog_detail_roomjoin_two:
                    number = 2;
                    break;
                case R.id.dialog_detail_roomjoin_three:
                    number = 3;
                    break;
            }

            //roomJoin.php를 통해서 데이터베이스 업데이트.
            RoomJoinPHP roomJoinPHP = new RoomJoinPHP();
            roomJoinPHP.execute("" + roomData.roomID, ((FrameActivity)mContext).userData.userID, "" + number);

            roomJoinDialog.dismiss();
        }
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
            if(roomJoinRequest.isInternetConnected()){
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;

//            int result = 0;
//            //StringBuilder jsonResult = new StringBuilder();
//            try {
//                URL urlTemp = new URL(roomJoinURL + "?userID=" + joinUserData.userID + "&roomID=" + joinRoomData.roomID + "&number=" + number);
//                HttpURLConnection conn = (HttpURLConnection) urlTemp.openConnection();
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setRequestMethod("GET");
//                conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
//
//                if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//                    while ( true ) {
//                        String line = br.readLine();
//                        if (line == null)
//                            break;
//                        result = Integer.parseInt(line);
//                    }
//                    br.close();
//                }
//                conn.disconnect();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//
//            return result;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            if(Integer.parseInt(roomJoinRequest.requestResult) == ROOM_JOIN_SUCCESS){
                dismiss();
                ((FrameActivity) mContext).makeChange(3);
            } else if(Integer.parseInt(roomJoinRequest.requestResult) == ROOM_JOIN_FAIL){
                Toast.makeText(mContext, "해당 방의 최대 참여 가능 유저의 수를 초과합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
