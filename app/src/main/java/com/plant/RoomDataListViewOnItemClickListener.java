package com.plant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kim on 2016-07-27.
 */
public class RoomDataListViewOnItemClickListener implements AdapterView.OnItemClickListener{
    Context mContext;
    int mode;

    Dialog dialog = null;
    ViewPager dialogViewPager;
    DialogViewPagerAdapter mViewPagerAdapter;

    private ImageView dialogDetailTopImg;
    private ImageView dialogDetailCancelBtn;
    private ImageView dialogDetailJoinBtn;

    public static final int DIALOG_MODE_JOIN = 1;
    public static final int DIALOG_MODE_CHECK = 2;

    public boolean userDataLodingComplete = false;
    public ArrayList<UserData> participateUserData;

    public RoomData clickedItem;
    public ArrayList<Bitmap> clickedItemProfileImg;
    public ArrayList<Integer> withNumber;

    public RoomDataListViewOnItemClickListener(Context context, int dialog_mode){
        mContext = context;
        mode = dialog_mode;

        /********************** Dialog 만들기 ******************************/
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_detail);
        dialog.setCancelable(true);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        dialogViewPager = (ViewPager) dialog.findViewById(R.id.dialog_detail_viewpager);
        mViewPagerAdapter = new DialogViewPagerAdapter(mContext, dialogViewPager, dialog);
        dialogViewPager.setAdapter(mViewPagerAdapter);

        dialogDetailTopImg = (ImageView) dialog.findViewById(R.id.dialog_detail_top_img);
        dialogDetailCancelBtn = (ImageView) dialog.findViewById(R.id.dialog_detail_cancel_btn);
        dialogDetailCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogDetailJoinBtn = (ImageView) dialog.findViewById(R.id.dialog_detail_join_btn);

        switch(mode){
            case DIALOG_MODE_JOIN:

                break;
            case DIALOG_MODE_CHECK:
                dialogDetailTopImg.setImageResource(R.drawable.dialog_detail_check_head);

                LinearLayout dialog_detail_bottom_layout = (LinearLayout) dialog.findViewById(R.id.dialog_detail_bottom_layout);
                dialog_detail_bottom_layout.removeAllViews();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout test = (LinearLayout) inflater.inflate(R.layout.dialog_detail_out_button, null);
                dialog_detail_bottom_layout.addView(test);

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((ReservationCheckFragment)((FrameActivity)mContext).fragment).reservation_listView_adapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clickedItem = (RoomData)parent.getAdapter().getItem(position);

        //참여하고 있는 userData를 얻어온다.
        FindUserDataFromRoomData findUserDataFromRoomData = new FindUserDataFromRoomData(this);
        findUserDataFromRoomData.execute(Long.toString(clickedItem.roomID));
        userDataLodingComplete = false;

        mViewPagerAdapter.setListViewListener(this);
        mViewPagerAdapter.setRoomData(clickedItem);

        switch(mode){
            case DIALOG_MODE_JOIN:
                dialog.findViewById(R.id.dialog_detail_join_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //이미 참여 중인지 여부 판단
                        boolean isJoining = false;
                        for(int i = 0; i < participateUserData.size(); i++){
                            if(participateUserData.get(i).userID.equals(((FrameActivity)mContext).userData.userID)){
                                isJoining = true;
                                break;
                            }
                        }

                        if(isJoining){
                            Toast.makeText(mContext, "이 방에는 이미 참여중입니다!", Toast.LENGTH_SHORT).show();
                        } else if(clickedItem.userNum == clickedItem.maxUserNum){
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
                dialog.findViewById(R.id.dialog_detail_chating_join).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ChatingActivity.class);
                        intent.putExtra("userData", ((FrameActivity)mContext).userData);
                        intent.putExtra("roomData", clickedItem);
                        intent.putExtra("participated",participateUserData);
                        intent.putExtra("withNumber",withNumber);
                        mContext.startActivity(intent);
                    }
                });
                dialog.findViewById(R.id.dialog_detail_out).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("out", "a");
                    }
                });

                break;
        }

        dialog.show();
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
            RoomJoinPHP roomJoinPHP = new RoomJoinPHP(clickedItem, ((FrameActivity)mContext).userData, number);
            roomJoinPHP.execute();

            roomJoinDialog.dismiss();
        }
    }

    private class RoomJoinPHP extends AsyncTask<Void, Void, Integer>{
        private static final int ROOM_JOIN_SUCCESS = 0;
        private static final int ROOM_JOIN_FAIL = 1;

        private final static String roomJoinURL = "http://plan-t.kr/roomJoin.php";

        RoomData joinRoomData;
        UserData joinUserData;
        int number;

        public RoomJoinPHP(RoomData roomData, UserData userData, int withNumber){
            joinRoomData = roomData;
            joinUserData = userData;
            number = withNumber;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 0;
            //StringBuilder jsonResult = new StringBuilder();
            try {
                URL urlTemp = new URL(roomJoinURL + "?userID=" + joinUserData.userID + "&roomID=" + joinRoomData.roomID + "&number=" + number);
                HttpURLConnection conn = (HttpURLConnection) urlTemp.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while ( true ) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        result = Integer.parseInt(line);
                    }
                    br.close();
                }
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            userDataLodingComplete = true;
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer == ROOM_JOIN_SUCCESS){
                dialog.dismiss();

                //회원이 가지고 있는 정보도 없데이트
                //FragmentActivity에 있는 RoomdataList를 업데이트
                clickedItem.userNum += number;
                ((FrameActivity)mContext).reservationCheckListCache.add(clickedItem);

                ((FrameActivity) mContext).makeChange(3);
            } else if(integer == ROOM_JOIN_FAIL){
                Toast.makeText(mContext, "해당 방의 최대 참여 가능 유저의 수를 초과합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RoomOutPHP{

    }
}
