package com.plant;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Kim on 2016-08-11.
 */
public class ProfileImageMaker {
    Context mContext;
    View view;

    public ProfileImageMaker(Context context){
        mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.dialog_detail_profile, null);
    }

    public void setImg(Bitmap bitmap){
        ((ImageView) view.findViewById(R.id.dialog_detail_profile_img)).setImageBitmap(bitmap);
    }

    public View getView(){
        return view;
    }
}
