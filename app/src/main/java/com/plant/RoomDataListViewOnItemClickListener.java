package com.plant;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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
public class RoomDataListViewOnItemClickListener implements AdapterView.OnItemClickListener, View.OnClickListener{
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

    public boolean userDataLodingComplete;
    public ArrayList<UserData> participateUserData;

    public RoomData clickedItem;

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
        dialogDetailJoinBtn.setOnClickListener(this);

        switch(mode){
            case DIALOG_MODE_JOIN:

                break;
            case DIALOG_MODE_CHECK:
                dialogDetailTopImg.setImageResource(R.drawable.dialog_detail_check_head);
                dialogDetailJoinBtn.setImageResource(R.drawable.dialog_detail_check_out);
                break;
        }
    }

    //join버튼과 나가기 버튼
    @Override
    public void onClick(View v) {
        switch(mode){
            case DIALOG_MODE_JOIN:
                Log.d("onClicked", "a");
                //이미 참여 중인지 여부 판단
                boolean isJoining = false;
                for(int i = 0; i < participateUserData.size(); i++){
                    if(participateUserData.get(i).userID.equals(((FrameActivity)mContext).userData.userID)){
                        isJoining = true;
                        break;
                    }
                }

                if(isJoining){
                    Log.d("test", "1");
                    Toast.makeText(mContext, "이 방에는 이미 참여중입니다!", Toast.LENGTH_SHORT).show();
                } else if(clickedItem.userNum == clickedItem.maxUserNum){
                    Log.d("test", "2");
                    Toast.makeText(mContext, "이 방은 정원이 가득 찼습니다!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("test", "3");
                    //roomJoin.php를 통해서 데이터베이스 업데이트.
                    RoomJoinPHP roomJoinPHP = new RoomJoinPHP(clickedItem, ((FrameActivity)mContext).userData);
                    roomJoinPHP.execute();

                    //회원이 가지고 있는 정보도 없데이트
                    //FragmentActivity에 있는 RoomdataList를 업데이트
                    ((FrameActivity)mContext).roomDataList.add(clickedItem);

                    //UI도 업데이트



                    //참여중이라면 버튼 이미지 변경.

                }
                break;
            case DIALOG_MODE_CHECK:
                Log.d("RoomDataListViewOnItem", "out!");
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

        dialog.show();
    }

    private class RoomJoinPHP extends AsyncTask<Void, Void, Void>{
        private final static String roomJoinURL = "http://plan-t.kr/roomJoin.php";

        RoomData joinRoomData;
        UserData joinUserData;

        public RoomJoinPHP(RoomData roomData, UserData userData){
            joinRoomData = roomData;
            joinUserData = userData;
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringBuilder jsonResult = new StringBuilder();
            try {
                URL url = new URL(roomJoinURL + "?userID=" + joinUserData.userID + "&roomID=" + joinRoomData.roomID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
                        jsonResult.append(line + "\n");
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

            Log.d("result", jsonResult.toString());
            return null;
        }
    }
}

