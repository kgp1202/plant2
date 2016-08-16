package com.plant;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kim on 2016-07-26.
 */
class DialogViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private int currentItemNumber;
    private ViewPager mViewPager;
    private RoomDataListViewOnItemClickListener itemClickListener;

    private RoomData currentRoomData;

    FrameLayout profileFrame = null;
    ImageView viewPagerStatus[] = new ImageView[3];
    ImageView profileImg[] = new ImageView[4];

    public DialogViewPagerAdapter(Context context, ViewPager viewPager, Dialog dialog){
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewPager = viewPager;

        viewPagerStatus[0] = (ImageView) dialog.findViewById(R.id.dialog_detail_viewpager_status1);
        viewPagerStatus[1] = (ImageView) dialog.findViewById(R.id.dialog_detail_viewpager_status2);
        viewPagerStatus[2] = (ImageView) dialog.findViewById(R.id.dialog_detail_viewpager_status3);
    }

    public void setRoomData(RoomData input){
        currentRoomData = input;
    }

    public void setListViewListener(RoomDataListViewOnItemClickListener listener){
        itemClickListener = listener;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        switch(position){
            case 0:     //base info
                view = inflater.inflate(R.layout.dialog_detail_info, null);
                switch(currentRoomData.startingPoint){
                    case RoomData.STARTING_POINT_BACK:
                        ((TextView) view.findViewById(R.id.dialog_detail_start_point_tv)).setText("인하대 후문");
                        break;
                    case RoomData.STARTING_POINT_FRONT:
                        ((TextView) view.findViewById(R.id.dialog_detail_start_point_tv)).setText("인하대 정문");
                        break;
                    case RoomData.STARTING_POINT_JUAN:
                        ((TextView) view.findViewById(R.id.dialog_detail_start_point_tv)).setText("주안역");
                        break;
                }
                ((TextView) view.findViewById(R.id.dialog_detail_dest_point_tv)).setText(currentRoomData.getDestPointToOutputForm());

                ((TextView) view.findViewById(R.id.dialog_detail_bottom_userNumber)).setText(currentRoomData.userNum + " / " + currentRoomData.maxUserNum);
                switch (currentRoomData.roomObject){
                    case RoomData.ROOM_OBJECT_CERTIFICATE:
                        ((TextView) view.findViewById(R.id.dialog_detail_bottom_object)).setText("자격증");
                        break;
                    case RoomData.ROOM_OBJECT_ENGLISH:
                        ((TextView) view.findViewById(R.id.dialog_detail_bottom_object)).setText("영어");
                        break;
                    case RoomData.ROOM_OBJECT_ETC:
                        ((TextView) view.findViewById(R.id.dialog_detail_bottom_object)).setText("기타");
                        break;
                }
                ((TextView) view.findViewById(R.id.dialog_detail_bottom_startTime)).setText(currentRoomData.getRoomTimeData());
                break;
            case 1:     //members
                view = inflater.inflate(R.layout.dialog_detail_member, null);

                profileFrame = (FrameLayout) view.findViewById(R.id.dialog_detail_member_profile_frame);

                LinearLayout profile[] = new LinearLayout[4];
                profile[0] = (LinearLayout) view.findViewById(R.id.dialog_detail_member_profile1);
                profile[1] = (LinearLayout) view.findViewById(R.id.dialog_detail_member_profile2);
                profile[2] = (LinearLayout) view.findViewById(R.id.dialog_detail_member_profile3);
                profile[3] = (LinearLayout) view.findViewById(R.id.dialog_detail_member_profile4);

                //dialog_detail_member에 들어갈 프로필을 inflate 해준다.
                while(!itemClickListener.userDataLodingComplete){ }    //데이터 로딩이 완료되면

                //profile을 설정.
                for(int i = 0; i < itemClickListener.participateUserData.size(); i++) {
                        View memberProfileView = inflater.inflate(R.layout.dialog_detail_member_exist, null);

                        TextView number=(TextView)memberProfileView.findViewById(R.id.number);
                        TextView profilePoint = (TextView) memberProfileView.findViewById(R.id.dialog_detail_member_profile_point);
                        TextView profileName = (TextView) memberProfileView.findViewById(R.id.dialog_detail_member_profile_name);
                        profileImg[i] = (ImageView) memberProfileView.findViewById(R.id.dialog_detail_member_profile_img);

                        UserData tempUserData = itemClickListener.participateUserData.get(i);

                        profilePoint.setText("" + tempUserData.point);
                        profileName.setText(tempUserData.name);

                        profile[i].addView(memberProfileView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        if(itemClickListener.withNumber.get(i)>= 1){
                            //동행인원의 프로필
                            Glide.with(mContext).load(tempUserData.profilePath).override(100,100).into(profileImg[i]);
                            number.setText((itemClickListener.withNumber.get(i))+"");
                        }else {
                            Glide.with(mContext).load(tempUserData.profilePath).override(100,100).into(profileImg[i]);
                            number.setText("");
                        }
                }

                //empty로 설정.
                for(int i = currentRoomData.userNum; i < currentRoomData.maxUserNum; i++){
                    profile[i].setBackgroundResource(R.drawable.dialog_detail_member_empty);
                }

                //BLCOKED로 설정
                for(int i = currentRoomData.maxUserNum; i < 4; i++){
                    profile[i].setBackgroundResource(R.drawable.dialog_detail_member_blocked);
                }

                break;
            case 2:     //comment
                view = inflater.inflate(R.layout.dialog_detail_comment, null);
                ((TextView)view.findViewById(R.id.dialog_detail_comment_text)).setText(" " + currentRoomData.comment);
                break;
        }
        mViewPager.addView(view, position);
        return view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mViewPager.removeView((View)object);
    }
    @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
    @Override public Parcelable saveState() { return null; }

    @Override
    public void finishUpdate(ViewGroup container) {
        if(currentItemNumber != mViewPager.getCurrentItem()){
            viewPagerStatus[currentItemNumber].setImageResource(R.drawable.dialog_detail_circle);
            currentItemNumber = mViewPager.getCurrentItem();
            viewPagerStatus[currentItemNumber].setImageResource(R.drawable.dialog_detail_selected_circle);
        }
    }
}
