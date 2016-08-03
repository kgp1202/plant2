package com.plant;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FrameActivity extends FragmentActivity implements View.OnClickListener, FragmentChangeListener, ActivityMakeDarker, View.OnTouchListener {
    QueueTask myQueueTask;

    /* view ****************************************/
    RelativeLayout mView;
    ImageView statusbar_home_btn;
    ImageView statusbar_realtime_btn;
    ImageView statusbar_conserve_btn;
    ImageView statusbar_conserve_confirm_btn;
    ImageView statusbar_more_btn;
    AnimationDrawable frameAnimation;
    RelativeLayout addedView;
    ImageView wheel;
    Fragment fragment;
    /************************************************/

    /***************** Fragment *********************/
    Fragment indexFragment = null;
    Fragment realTimeFragment = null;
    Fragment reservationFragment = null;
    Fragment reservationCheckFragment = null;
    Fragment moreFragment = null;
    /************************************************/

    //UserData와 RoomData
    public UserData userData;
    public ArrayList<RoomData> roomDataList;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NanumGothicBold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_frame);
        init();
    }

    /**
     * init method
     ******************************************************************************************/
    public void init() {
        //intent를 통해서 넘어온 데이터
        userData = (UserData) getIntent().getSerializableExtra("UserData");
        roomDataList = (ArrayList<RoomData>) getIntent().getSerializableExtra("RoomDataList");

        mView = (RelativeLayout) findViewById(R.id.mView);

        statusbar_home_btn = (ImageView) findViewById(R.id.statusbar_home_btn);
        statusbar_realtime_btn = (ImageView) findViewById(R.id.statusbar_realtime_btn);
        statusbar_conserve_btn = (ImageView) findViewById(R.id.statusbar_conserve_btn);
        statusbar_conserve_confirm_btn = (ImageView) findViewById(R.id.statusbar_conserve_confirm_btn);
        statusbar_more_btn = (ImageView) findViewById(R.id.statusbar_more_btn);

        statusbar_home_btn.setOnClickListener(this);
        statusbar_realtime_btn.setOnClickListener(this);
        statusbar_conserve_btn.setOnClickListener(this);
        statusbar_conserve_confirm_btn.setOnClickListener(this);
        statusbar_more_btn.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentReplace, new IndexFragment())
                .commit();
    }

    //클릭 되어졌던 이미지를 초기화
    public void initImage() {
        statusbar_home_btn.setImageResource(R.drawable.statusbar_home_btn);
        statusbar_realtime_btn.setImageResource(R.drawable.statusbar_realtime_btn);
        statusbar_conserve_btn.setImageResource(R.drawable.statusbar_conserve_btn);
        statusbar_conserve_confirm_btn.setImageResource(R.drawable.statusbar_conserve_confirm_btn);
        statusbar_more_btn.setImageResource(R.drawable.statusbar_more_btn);
    }
    /************************************************************************************************/

    /**
     * Override from implements
     ******************************************************************************************/
    @Override
    public void onClick(View v) {
        initImage();
        switch (v.getId()) {
            case R.id.statusbar_home_btn:
                statusbar_home_btn.setImageResource(R.drawable.statusbar_home_clicked_btn);
                if(indexFragment == null){
                    Log.d("Fragment Activity", "indexFragment make");
                    indexFragment = new IndexFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentReplace, indexFragment)
                        .commit();
                break;
            case R.id.statusbar_realtime_btn:
                statusbar_realtime_btn.setImageResource(R.drawable.statusbar_realtime_clicked_btn);
                if(realTimeFragment == null){
                    realTimeFragment = new RealTimeFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentReplace, realTimeFragment)
                        .commit();
                break;
            case R.id.statusbar_conserve_btn:
                statusbar_conserve_btn.setImageResource(R.drawable.statusbar_conserve_clicked_btn);
                if(reservationFragment == null){
                    reservationFragment = new ReservationFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentReplace, new ReservationFragment())
                        .commit();
                break;
            case R.id.statusbar_conserve_confirm_btn:
                statusbar_conserve_confirm_btn.setImageResource(R.drawable.statusbar_conserve_confirm_clicked_btn);
                if(reservationCheckFragment == null){
                    reservationCheckFragment = new ReservationCheckFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentReplace, reservationCheckFragment)
                        .commit();
                break;
            case R.id.statusbar_more_btn:
                statusbar_more_btn.setImageResource(R.drawable.statusbar_more_clicked_btn);
                if(moreFragment == null){
                    moreFragment = new MoreFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentReplace, moreFragment)
                        .commit();
                break;
        }
    }

    @Override
    public void makeChange(int number) {
        initImage();
        switch (number) {
            case 1:
                statusbar_realtime_btn.setImageResource(R.drawable.statusbar_realtime_clicked_btn);
                fragment = new RealTimeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentReplace, fragment)
                        .commit();
                break;
            case 2:
                statusbar_conserve_btn.setImageResource(R.drawable.statusbar_conserve_clicked_btn);
                fragment = new ReservationFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentReplace, fragment)
                        .commit();
                break;
            case 3:
                statusbar_conserve_confirm_btn.setImageResource(R.drawable.statusbar_conserve_confirm_clicked_btn);
                fragment = new ReservationCheckFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentReplace, fragment)
                        .commit();
                break;
            case 4:
                statusbar_conserve_btn.setImageResource(R.drawable.statusbar_conserve_clicked_btn);
                fragment = new ReservationMakeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentReplace, fragment)
                        .commit();
                break;
        }
    }

    @Override
    public void makeDarker(boolean input) {
        if (input) {//set view
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            addedView = (RelativeLayout) inflater.inflate(R.layout.queuing_layout, null);
            addedView.setOnTouchListener(this);
            addedView.setClickable(true);
            mView.addView(addedView);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addedView.setLayoutParams(layoutParams);
            wheel = (ImageView) addedView.findViewById(R.id.wheelImage);
            wheel.setBackgroundResource(R.drawable.wheel_list);
            frameAnimation = (AnimationDrawable) wheel.getBackground();
            frameAnimation.start();
            myQueueTask=new QueueTask(this);
            myQueueTask.execute();
        } else {
            frameAnimation.stop();
            mView.removeViewAt(1);
        }
    }

    float mThouchX;
    float baseX;
    boolean isMove=false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mThouchX = event.getX();
                baseX = v.getX();
                isMove=false;
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    if (event.getRawX() - baseX > 200) {
                        myQueueTask.cancel(true);
                    } else
                        v.setX(0);
                }
                else
                    v.setX(0);
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                if (event.getRawX() - mThouchX > 0) {
                    v.setX(v.getX() + event.getX() - mThouchX);
                }
                break;
        }
        return false;
    }
    /************************************************************************************************/
}