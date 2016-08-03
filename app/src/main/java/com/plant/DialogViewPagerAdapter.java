package com.plant;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

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

    ImageView profileImageView = null;
    TextView profilePoint = null;
    TextView profileName = null;
    ImageView viewPagerStatus[] = new ImageView[3];

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

                LinearLayout profile[] = new LinearLayout[4];
                profile[0] = (LinearLayout) view.findViewById(R.id.dialog_detail_member_profile1);
                profile[1] = (LinearLayout) view.findViewById(R.id.dialog_detail_member_profile2);
                profile[2] = (LinearLayout) view.findViewById(R.id.dialog_detail_member_profile3);
                profile[3] = (LinearLayout) view.findViewById(R.id.dialog_detail_member_profile4);

                ImageView profileStatus[] = new ImageView[4];
                profileStatus[0] = (ImageView) view.findViewById(R.id.dialog_detail_member_profile1_status);
                profileStatus[1] = (ImageView) view.findViewById(R.id.dialog_detail_member_profile2_status);
                profileStatus[2] = (ImageView) view.findViewById(R.id.dialog_detail_member_profile3_status);
                profileStatus[3] = (ImageView) view.findViewById(R.id.dialog_detail_member_profile4_status);

                //dialog_detail_member에 들어갈 프로필을 inflate 해준다.
                while(!itemClickListener.userDataLodingComplete){ }    //데이터 로딩이 완료되면

                //profile을 설정.
                for(int i = 0; i < currentRoomData.userNum; i++) {
                    UserData tempUserData = itemClickListener.participateUserData.get(i);

                    View memberProfileView = null;

                    if (memberProfileView == null) {
                        memberProfileView = inflater.inflate(R.layout.dialog_detail_member_exist, null);

                        profileImageView = (ImageView) memberProfileView.findViewById(R.id.dialog_detail_member_profile_img);
                        profilePoint = (TextView) memberProfileView.findViewById(R.id.dialog_detail_member_profile_point);
                        profileName = (TextView) memberProfileView.findViewById(R.id.dialog_detail_member_profile_name);
                    }

                    /******************************* profile Img **********************************/
                    //로그인한 회원 본인일시
                    if(tempUserData.userID.equals(((FrameActivity)mContext).userData.userID)) {
                        String mCurrentPhotoPath = Environment.getExternalStorageDirectory().toString() +
                                "/save_folder" + "/" + tempUserData.getDecodedProfilePath();

                        File f = new File(mCurrentPhotoPath);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;

                        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
                        if(bmp == null) {
                            Log.d("bmp null", "a");
                        }
                        if(profileImageView == null){
                            Log.d("profileImageVIew", "a");
                        }
                        profileImageView.setImageBitmap(bmp);
                    }
                    //다른 회원의 경우
                    else {
                        Glide.with(mContext).load(tempUserData.profilePath).into(profileImageView);
                    }
                    /******************************* profile Img END ******************************/

                    profilePoint.setText("" + tempUserData.point);
                    profileName.setText(tempUserData.name);

                    profile[i].addView(memberProfileView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }

                //empty로 설정.
                for(int i = currentRoomData.userNum; i < currentRoomData.maxUserNum; i++){
                    profileStatus[i].setVisibility(View.VISIBLE);
                }

                //BLCOKED로 설정
                for(int i = currentRoomData.maxUserNum; i < 4; i++){
                    profileStatus[i].setVisibility(View.VISIBLE);
                    profileStatus[i].setImageResource(R.drawable.dialog_detail_member_blocked);
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
