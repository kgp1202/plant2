package com.plant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
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
    Context mContext;

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
    /************************************************/

    //UserData와 RoomData
    public UserData userData;
    //public ArrayList<RoomData> reservationCheckListCache;
    //public ArrayList<RoomData> reservationListCache;

    public int makeRoomCount = 0;

    public int currentFragmentNumber;

    FragmentManager fragmentManager;
    Fragment fragment = null;

    boolean isDoBackground = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NanumGothicBold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window=this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#77361a"));
        }
        setContentView(R.layout.activity_frame);
        init();
    }

    /**
     * init method
     ******************************************************************************************/
    public void init() {
        //intent를 통해서 넘어온 데이터
        userData = (UserData) getIntent().getSerializableExtra("UserData");
       // reservationCheckListCache = (ArrayList<RoomData>) getIntent().getSerializableExtra("RoomDataList");

        mView = (RelativeLayout) findViewById(R.id.mView);

        LinearLayout activity_frame_menubar = (LinearLayout) findViewById(R.id.activity_frame_menubar);
        setupUI(activity_frame_menubar);

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

        fragmentManager = getSupportFragmentManager();
        fragment = new IndexFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentReplace, fragment)
                .commit();

        currentFragmentNumber = 0;
    }

    //클릭 되어졌던 이미지를 초기화
    public void initImage() {
        switch(currentFragmentNumber){
            case 0: statusbar_home_btn.setImageResource(R.drawable.statusbar_home_btn);
                break;
            case 1: statusbar_realtime_btn.setImageResource(R.drawable.statusbar_realtime_btn);
                break;
            case 2:
            case 4:statusbar_conserve_btn.setImageResource(R.drawable.statusbar_conserve_btn);
                break;
            case 3:statusbar_conserve_confirm_btn.setImageResource(R.drawable.statusbar_conserve_confirm_btn);
                break;
            case 5: statusbar_more_btn.setImageResource(R.drawable.statusbar_more_btn);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.statusbar_home_btn:
                makeChange(0);
                break;
            case R.id.statusbar_realtime_btn:
                makeChange(1);
                break;
            case R.id.statusbar_conserve_btn:
                makeChange(2);
                break;
            case R.id.statusbar_conserve_confirm_btn:
                makeChange(3);
                break;
            case R.id.statusbar_more_btn:
                makeChange(5);
                break;
        }
    }

    @Override
    public void makeChange(int number) {
        initImage();
        currentFragmentNumber = number;
        fragmentManager.beginTransaction().remove(fragment).commit();

        switch (number) {
            case 0:
                statusbar_home_btn.setImageResource(R.drawable.statusbar_home_clicked_btn);
                fragment = new IndexFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.fragmentReplace, fragment)
                        .commit();
                break;
            case 1:
                statusbar_realtime_btn.setImageResource(R.drawable.statusbar_realtime_clicked_btn);
                fragment = new RealTimeFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.fragmentReplace, fragment)
                        .commit();
                break;
            case 2:
                statusbar_conserve_btn.setImageResource(R.drawable.statusbar_conserve_clicked_btn);
                fragment = new ReservationFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.fragmentReplace, fragment)
                        .commit();
                break;
            case 3:
                statusbar_conserve_confirm_btn.setImageResource(R.drawable.statusbar_conserve_confirm_clicked_btn);
                fragment = new ReservationCheckFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.fragmentReplace, fragment)
                        .commit();
                break;
            case 4:
                statusbar_conserve_btn.setImageResource(R.drawable.statusbar_conserve_clicked_btn);
                fragment = new ReservationMakeFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.fragmentReplace, fragment)
                        .commit();
                break;
            case 5:
                statusbar_more_btn.setImageResource(R.drawable.statusbar_more_clicked_btn);
                fragment = new MoreFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.fragmentReplace, fragment)
                        .commit();
                break;
        }
    }

    public void getResultFromThread(RoomData tempR){
        //reservationCheckListCache.add(0, tempR);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        if(tempR.roomID==0){
            alert.setMessage("방 생성에 실패하셨습니다 ㅜ");
            alert.show();
        }
        else if(tempR.roomID==-1){
            alert.setMessage("실시간 매칭을 취소하셨습니다!");
            alert.show();
        }
        else{
            RoomDataDialog dialog=new RoomDataDialog(this,RoomDataDialog.DIALOG_MODE_CHECK);
            dialog.show(tempR);
        }
    }

    public void makeDarker(boolean input) {
        if(input) {
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

            myQueueTask = new QueueTask(mContext, this);
            myQueueTask.setRoomData(RealTimeFragment.realTimeRommData);
            myQueueTask.setUserData(userData);

            if(HttpRequest.isInternetConnected(mContext))
                myQueueTask.execute();
            else {
                //인터넷 연결이 안되어 있을 떄의 처리.
            }
        }else {
            frameAnimation.stop();
            mView.removeViewAt(1);
        }
    }

    float mThouchX;
    float baseX;
    boolean isMove=false;

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard((Activity) mContext);
                    return false;
                }
            });
        }
        else{
            switch(view.getId()){
                case R.id.search_editText:
                case R.id.destination_editText:
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            ((Activity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                            return false;
                        }
                    });
                    break;
                case R.id.comment_editText:
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            ((Activity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                            return false;
                        }
                    });
                    break;
            }
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mThouchX = event.getX();
                baseX = v.getX();
                isMove = false;
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    if (event.getRawX() - baseX > 300) {
                        Log.d("asd", event.getRawX() - baseX + "");
                        myQueueTask.cancel(true);
                    } else
                        v.setX(0);
                } else
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