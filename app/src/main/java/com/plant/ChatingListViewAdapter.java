package com.plant;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;

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
                Glide.with(context).load(temp.getString("profile")).override(60,50).into(holder.profile.profile);
                holder.profile.number.setText(temp.getInt("userNum")+"");
                holder.profile.name.setText(temp.getString("name"));
                ((LinearLayout) convertView).addView(holder.profile.container);
                ((LinearLayout) convertView).setGravity(Gravity.LEFT);
            }
            ((LinearLayout) convertView).addView(holder.content);
            ((LinearLayout) convertView).setGravity(Gravity.RIGHT);
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
            content.setMaxWidth(200);
            content.setTextSize(30);

            profile=new ProfileRelativeLayout(myContext);
        }
    };
    class ProfileRelativeLayout{
        RelativeLayout container;
        ImageView profile;
        TextView name;
        TextView number;
        ProfileRelativeLayout(Context myContext){
            container=new RelativeLayout(myContext);
            container.setLayoutParams(new RelativeLayout.LayoutParams(60,80));

            profile=new ImageView(myContext);
            profile.setLayoutParams(new RelativeLayout.LayoutParams(60,50));
            profile.setScaleType(ImageView.ScaleType.CENTER);

            number=new TextView(myContext);
            RelativeLayout.LayoutParams p=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.CENTER_HORIZONTAL);
            p.addRule(RelativeLayout.CENTER_VERTICAL);
            number.setLayoutParams(p);
            number.setTextSize(30);
            number.setTextColor(Color.parseColor("#fd8107"));
            number.setId(View.generateViewId());
            number=new TextView(myContext);

            name=new TextView(myContext);
            RelativeLayout.LayoutParams p2=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,30);
            p2.addRule(RelativeLayout.BELOW,profile.getId());
            name.setLayoutParams(p2);
            name.setTextSize(20);
            number.setTextColor(Color.parseColor("#000000"));

            container.addView(profile);
            container.addView(number);
            container.addView(name);
        }
    };
}
