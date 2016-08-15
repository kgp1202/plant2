package com.plant;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by angks on 2016-08-11.
 */
public class MyTextView {
    LinearLayout parent;
    TextView myText;
    MyTextView(Context myContext,String input){
        LayoutInflater inflater=(LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parent=(LinearLayout) inflater.inflate(R.layout.chating_my_view,null);
        myText=(TextView)parent.findViewById(R.id.contents);
        myText.setText(input);
    }
    public LinearLayout getLayout(){
        return parent;
    }
}
