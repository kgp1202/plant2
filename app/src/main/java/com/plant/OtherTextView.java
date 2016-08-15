package com.plant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by angks on 2016-08-11.
 */
public class OtherTextView {
    View profile;
    LinearLayout parent;
    TextView myText;
    OtherTextView(Context myContext, String input){
        LayoutInflater inflater=(LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parent=(LinearLayout) inflater.inflate(R.layout.chating_other_view,null);
        myText=(TextView)parent.findViewById(R.id.contents);
        myText.setText(input);
        profile=parent.findViewById(R.id.profile);
    }
    public LinearLayout getLayout(){
        return parent;
    }
}
