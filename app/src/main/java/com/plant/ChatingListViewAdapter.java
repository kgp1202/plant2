package com.plant;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by angks on 2016-08-16.
 */
public class ChatingListViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<JSONObject> myJsonObjectList;
    UserData myData;
    public ChatingListViewAdapter(Context myContext, UserData input3){
        myJsonObjectList=new ArrayList<JSONObject>();
        myData=input3;
        context=myContext;
    }
    public void add(JSONObject input){
        myJsonObjectList.add(input);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return myJsonObjectList.size();
    }
    @Override
    public Object getItem(int position) {
        return myJsonObjectList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyTextHolder holder;
        if(convertView==null){
            holder=new MyTextHolder(context);
            convertView=new LinearLayout(context);
            convertView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            convertView.setTag(holder);
        }
        else{
            holder=(MyTextHolder) convertView.getTag();
        }
        ((LinearLayout)convertView).removeAllViews();
        try{
            JSONObject temp=myJsonObjectList.get(position);
            holder.content.setText(temp.getString("content"));
            if(!temp.getString("userID").equals(myData.userID)){
                Glide.with(context).load(temp.getString("profile")).override(getPXfromDP(60),getPXfromDP(50)).into(holder.profile.profile);
                holder.profile.number.setText(temp.getInt("userNum")+"");
                holder.profile.name.setText(temp.getString("name"));
                ((LinearLayout) convertView).addView(holder.profile.container);
                ((LinearLayout) convertView).setGravity(Gravity.LEFT);
            }
            else
                ((LinearLayout) convertView).setGravity(Gravity.RIGHT);
            ((LinearLayout) convertView).addView(holder.content);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

    class MyTextHolder{
        ProfileRelativeLayout profile;
        TextView content;
        MyTextHolder(Context myContext){
            content=new TextView(myContext);
            content.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            content.setMaxWidth(getPXfromDP(200));
            content.setTextColor(Color.parseColor("#000000"));
            content.setTextSize(30);

            profile=new ProfileRelativeLayout(myContext);
        }
    };
    class ProfileRelativeLayout{
        RelativeLayout container;
        RelativeLayout container2;
        ImageView profile;
        TextView name;
        TextView number;
        ProfileRelativeLayout(Context myContext){
            container=new RelativeLayout(myContext);
            container.setLayoutParams(new RelativeLayout.LayoutParams(getPXfromDP(60),getPXfromDP(80)));


            //container2.setForeground(ContextCompat.getDrawable(context,R.drawable.dialog_detail_member_ring));
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            container2=(RelativeLayout) inflater.inflate(R.layout.chating_container2,null);
            profile=(ImageView) container2.findViewById(R.id.profile);
            number=(TextView)container2.findViewById(R.id.number);
            container2.setId(View.generateViewId());

            name=new TextView(myContext);
            RelativeLayout.LayoutParams p2=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,getPXfromDP(30));
            p2.addRule(RelativeLayout.BELOW,container2.getId());
            name.setLayoutParams(p2);
            name.setTextSize(20);
            name.setTextColor(Color.parseColor("#000000"));

            container.addView(container2);
            container.addView(name);
        }
    };

    int getPXfromDP(int value){
        Resources r = context.getResources();
        float px = value* r.getDisplayMetrics().density;
        return (int)px;
    }
}
