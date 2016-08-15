package com.plant;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Kim on 2016-05-22.
*/

public class IndexFragment extends Fragment implements View.OnTouchListener{
    /*view*******************************************/
    ImageView index_realtime_btn;
    ImageView index_conserve_btn;
    ImageView index_conserve_confirm_btn;
    Animation scale_touch_anim;
    View touched_view;
    View mainView;
    /************************************************/

    /*functional************************************/
    FragmentChangeListener mCallback;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("indexFragment", "destroy");
        unbindDrawables(mainView);
    }

    private void unbindDrawables(View view){
        if (view.getBackground() != null){
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)){
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++){
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    /**********************************************/




    public IndexFragment(){}

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallback=(FragmentChangeListener)context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_index, container, false);
        init(rootView);
        mainView = rootView;
        return rootView;
    }

    public void init(View v){
        index_realtime_btn = (ImageView) v.findViewById(R.id.index_realtime_btn);
        index_conserve_btn = (ImageView) v.findViewById(R.id.index_conserve_btn);
        index_conserve_confirm_btn = (ImageView) v.findViewById(R.id.index_conserve_confirm_btn);

        index_realtime_btn.setOnTouchListener(this);
        index_conserve_btn.setOnTouchListener(this);
        index_conserve_confirm_btn.setOnTouchListener(this);

        scale_touch_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_touch);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            touched_view = v;
            v.startAnimation(scale_touch_anim);
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            touched_view.clearAnimation();
            switch(touched_view.getId()){
                case R.id.index_realtime_btn:
                    mCallback.makeChange(1);
                    break;
                case R.id.index_conserve_btn:
                    mCallback.makeChange(2);
                    break;
                case R.id.index_conserve_confirm_btn:
                    mCallback.makeChange(3);
                    break;
            }
        }
        return false;
    }
}
