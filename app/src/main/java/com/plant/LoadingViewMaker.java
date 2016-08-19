package com.plant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Kim on 2016-08-18.
 */
public class LoadingViewMaker {
    public boolean isLoading = false;

    private Context mContext;
    private RelativeLayout addedView;
    private RelativeLayout mParent;

    public LoadingViewMaker(Context context){
        mContext = context;

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addedView = (RelativeLayout) inflater.inflate(R.layout.loading_view, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addedView.setLayoutParams(layoutParams);
    }

    public void makeLoading(RelativeLayout parent){
        isLoading = true;
        mParent = parent;

        addedView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mParent.addView(addedView);
    }

    public void completeLoading(){
        mParent.removeView(addedView);
    }
}
