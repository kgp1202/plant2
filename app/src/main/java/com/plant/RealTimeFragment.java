package com.plant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by angks on 2016-05-22.
 */
public class RealTimeFragment extends Fragment implements View.OnTouchListener {
    /*another*********************************/
    public static RoomData realTimeRommData=new RoomData();
    ActivityMakeDarker mCallBack;
    public boolean isQueuing = false;
    /**************************************/

    /*View*********************************/
    ImageView withNumImages[] = new ImageView[3];
    ImageView maxNumImages[] = new ImageView[3];
    ImageView juanBtn;
    ImageView schoolBtn;
    RelativeLayout sendBtn;
    View mainView;
    Context myContext;

    /**************************************/


    /*init*********************************/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = context;
        mCallBack = (ActivityMakeDarker) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_realtime, container, false);
        initInstance();
        for (int i = 0; i <= 3; i++) initData(i);
        return mainView;
    }

    public void initInstance() {
        (withNumImages[0] = (ImageView) mainView.findViewById(R.id.realTimeMatchingWith1)).setOnClickListener(withNumOnClickListener);
        (withNumImages[1] = (ImageView) mainView.findViewById(R.id.realTimeMatchingWith2)).setOnClickListener(withNumOnClickListener);
        (withNumImages[2] = (ImageView) mainView.findViewById(R.id.realTimeMatchingWith3)).setOnClickListener(withNumOnClickListener);

        (maxNumImages[1] = (ImageView) mainView.findViewById(R.id.realTimeMax2)).setOnClickListener(maxNumOnclickListener);
        (maxNumImages[2] = (ImageView) mainView.findViewById(R.id.realTimeMax3)).setOnClickListener(maxNumOnclickListener);

        (juanBtn = (ImageView) mainView.findViewById(R.id.realtimeJuanBtn)).setOnClickListener(DestOnClickListener);
        (schoolBtn = (ImageView) mainView.findViewById(R.id.realtimeSchoolBtn)).setOnClickListener(DestOnClickListener);
        (sendBtn = (RelativeLayout) mainView.findViewById(R.id.realtimeSendBtn)).setOnTouchListener(this);

    }

    public void initData(int caseNum) {
        int i;
        switch (caseNum) {
            case 0:
                realTimeRommData = new RoomData();
                break;
            case 1:
                for (i = 0; i < 3; i++) withNumImages[i].setTag(false);
                withNumImages[0].setImageResource(R.drawable.matching_with1);
                withNumImages[1].setImageResource(R.drawable.matching_with2);
                withNumImages[2].setImageResource(R.drawable.matching_with3);
                //withNumImages[2].setTag(0);
                break;
            case 2:
                for (i = 1; i < 3; i++) maxNumImages[i].setTag(0);
                maxNumImages[1].setImageResource(R.drawable.realtime_max_2);
                maxNumImages[2].setImageResource(R.drawable.realtime_max_3);
                break;
            case 3:
                juanBtn.setTag(false);
                schoolBtn.setTag(false);
                juanBtn.setImageResource(R.drawable.realtime_juan_btn);
                schoolBtn.setImageResource(R.drawable.realtime_school_btn);
                break;
        }
    }
    /**************************************/

    /*function*****************************/
    public void makeUnableMaxNumImages(int input) {
        switch (input) {
            case 2:
                maxNumImages[1].setImageResource(R.drawable.realtime_max_2_u);
                maxNumImages[1].setTag(1);
        }
    }

    public boolean checkValidation() {
        if (realTimeRommData.destPoint.equals("") || realTimeRommData.maxUserNum == 0 || realTimeRommData.userNum == 0 || realTimeRommData.startingPoint == 0)
            return false;
        return true;
    }
    /**************************************/

     /*Listener*****************************/
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isQueuing) return false;
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext, android.R.style.Theme_Material_Dialog);
        if (checkValidation()) {
            String dest = "";
            dest=realTimeRommData.destPoint;
            realTimeRommData.roomType=1;
            if(((int) maxNumImages[2].getTag()) == 2 && ((int) maxNumImages[1].getTag()) == 2){
                builder.setTitle("확인!")
                        .setMessage(realTimeRommData.userNum + "명이서 3명 또는 4명과 함께 택시를 타고 " + dest + " 가는 것 맞나요?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCallBack.makeDarker(true);
                            }
                        })
                        .setNegativeButton("아뇨", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

            }
            else {
                builder.setTitle("확인!")
                        .setMessage(realTimeRommData.userNum + "명이서 " + realTimeRommData.maxUserNum + "명과 함께 택시를 타고 " + dest + " 가는 것 맞나요?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCallBack.makeDarker(true);
                            }
                        })
                        .setNegativeButton("아뇨", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
            }
        } else {
            builder.setTitle("확인")
                    .setMessage("부족한 정보를 입력해 주세요!")
                    .setCancelable(true)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    View.OnClickListener DestOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isQueuing) return;
            boolean get = (boolean) v.getTag();
            switch (v.getId()) {
                case R.id.realtimeJuanBtn:
                    if (!get) {
                        initData(3);
                        juanBtn.setImageResource(R.drawable.realtime_juan_btn_s);
                        juanBtn.setTag(true);
                        realTimeRommData.destPoint = "주안으로";
                        realTimeRommData.startingPoint = 2;
                    }
                    break;
                case R.id.realtimeSchoolBtn:
                    if (!get) {
                        initData(3);
                        schoolBtn.setImageResource(R.drawable.realtime_school_btn_s);
                        schoolBtn.setTag(true);
                        realTimeRommData.destPoint ="후문으로";
                        realTimeRommData.startingPoint = 1;
                    }
                    break;
            }
        }
    };
    View.OnClickListener withNumOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isQueuing) return;
            boolean get = (boolean) v.getTag();
            switch (v.getId()) {
                case R.id.realTimeMatchingWith1:
                    if (get) break;
                    else {
                        initData(1);
                        initData(2);
                        v.setTag(true);
                        withNumImages[0].setImageResource(R.drawable.matching_with1_s);
                        realTimeRommData.userNum = 1;
                        realTimeRommData.maxUserNum = 0;
                    }
                    break;
                case R.id.realTimeMatchingWith2:
                    if (get) break;
                    else {
                        initData(1);
                        initData(2);
                        v.setTag(true);
                        withNumImages[1].setImageResource(R.drawable.matching_with2_s);
                        makeUnableMaxNumImages(1);
                        realTimeRommData.userNum = 2;
                        realTimeRommData.maxUserNum = 0;
                    }
                    break;
                case R.id.realTimeMatchingWith3:
                    if (get) break;
                    else {
                        initData(1);
                        initData(2);
                        v.setTag(true);
                        withNumImages[2].setImageResource(R.drawable.matching_with3_s);
                        makeUnableMaxNumImages(2);
                        realTimeRommData.userNum = 3;
                        realTimeRommData.maxUserNum = 0;
                    }
                    break;
            }
        }
    };
    View.OnClickListener maxNumOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            realTimeRommData.maxUserNum=0;
            if (isQueuing) return;
            switch (v.getId()) {
                case R.id.realTimeMax2:
                    if ((boolean)withNumImages[2].getTag() == false) {
                        if (((int) maxNumImages[1].getTag()) == 2) {
                            maxNumImages[1].setImageResource(R.drawable.realtime_max_2);
                            maxNumImages[1].setTag(0);
                        }else{
                            maxNumImages[1].setImageResource(R.drawable.realtime_max_2_s);
                            maxNumImages[1].setTag(2);
                        }
                    }
                    break;
                case R.id.realTimeMax3:
                    if (((int) maxNumImages[2].getTag()) == 2) {
                        maxNumImages[2].setImageResource(R.drawable.realtime_max_3);
                        maxNumImages[2].setTag(0);
                    }
                    else{
                        maxNumImages[2].setImageResource(R.drawable.realtime_max_3_s);
                        maxNumImages[2].setTag(2);
                    }
                    break;
            }
            if(((int) maxNumImages[2].getTag()) == 2 && ((int) maxNumImages[1].getTag()) == 2){
                realTimeRommData.maxUserNum=7;
            }
            else if(((int) maxNumImages[2].getTag()) == 2)
                realTimeRommData.maxUserNum=4;
            else if(((int) maxNumImages[1].getTag()) == 2)
                realTimeRommData.maxUserNum=3;
            Log.d("realTimeRoomData",""+realTimeRommData.maxUserNum);
        }
    };
    /**************************************/
}