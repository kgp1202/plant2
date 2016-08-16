package com.plant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by angks on 2016-08-11.
 */
public class OtherTextView {
    ImageView profile;
    LinearLayout parent;
    TextView name;
    TextView myText;
    OtherTextView(Context myContext, String input,UserData use,String url){
        LayoutInflater inflater=(LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parent=(LinearLayout) inflater.inflate(R.layout.chating_other_view,null);
        myText=(TextView)parent.findViewById(R.id.contents);
        myText.setText(input);
        profile=(ImageView)parent.findViewById(R.id.dialog_detail_member_profile_img);
        Glide.with(myContext).load(use.profilePath).override(100,100).into(profile);
        name=(TextView)parent.findViewById(R.id.name);
        name.setText(use.name);
    }
    public LinearLayout getLayout(){
        return parent;
    }
}
