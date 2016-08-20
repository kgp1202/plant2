package com.plant;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import org.w3c.dom.Text;

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
        if(convertView==null) {
            holder = new MyTextHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = (LinearLayout) inflater.inflate(R.layout.chating_row, parent, false);
            holder.profileLayout = (LinearLayout) convertView.findViewById(R.id.others);
            holder.content=(TextView)convertView.findViewById(R.id.content);
            holder.profile=(ImageView)holder.profileLayout.findViewById(R.id.profile);
            holder.name=(TextView)holder.profileLayout.findViewById(R.id.name);
            holder.number=(TextView)holder.profileLayout.findViewById(R.id.number);
            holder.contentContainer=(LinearLayout)convertView.findViewById(R.id.contentContainer);
            holder.marginLayout=(LinearLayout)convertView.findViewById(R.id.marginLayout);
            holder.beforeID="";
            convertView.setTag(holder);
        }
        else{
            holder=(MyTextHolder) convertView.getTag();
        }
        try{
            JSONObject temp=myJsonObjectList.get(position);
            holder.content.setText(temp.getString("content"));
            if(position==0){
                holder.beforeID="";
            }
            else{
                holder.beforeID=myJsonObjectList.get(position-1).getString("userID");
            }
            if(!temp.getString("userID").equals(myData.userID)){
                holder.number.setText(temp.getInt("userNum")+"");
                holder.name.setText(temp.getString("name"));
                if(temp.getString("userID").equals(holder.beforeID)){
                    holder.number.setVisibility(View.INVISIBLE);
                    holder.name.setVisibility(View.INVISIBLE);
                    holder.profile.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)holder.marginLayout.getLayoutParams();
                    p.leftMargin=getPXfromDP(60);
                }
                else{
                    holder.number.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                    holder.profile.setVisibility(View.VISIBLE);
                    if(!temp.getString("profile").equals(""))
                        Glide.with(context).load(temp.getString("profile")).override(getPXfromDP(60),getPXfromDP(50)).into(holder.profile);
                    else
                        Glide.with(context).load(R.drawable.profile_thumbnail_default).override(getPXfromDP(60),getPXfromDP(50)).into(holder.profile);
                }
                holder.profileLayout.setVisibility(View.VISIBLE);
                ((LinearLayout) convertView).setGravity(Gravity.LEFT);
                holder.contentContainer.setBackground(ContextCompat.getDrawable(context,R.drawable.other_chating));
                holder.content.setTextColor(Color.BLACK);
            }
            else{
                ((LinearLayout) convertView).setGravity(Gravity.RIGHT);
                holder.profileLayout.setVisibility(View.INVISIBLE);
                holder.contentContainer.setBackground(ContextCompat.getDrawable(context,R.drawable.my_chating));
                holder.content.setTextColor(Color.WHITE);
                LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)holder.marginLayout.getLayoutParams();
                p.leftMargin=getPXfromDP(10);
            }
            holder.content.setText(temp.getString("content"));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

    class MyTextHolder{
        LinearLayout profileLayout;
        LinearLayout contentContainer;
        LinearLayout marginLayout;
        ImageView profile;
        TextView name;
        TextView number;
        TextView content;
        String beforeID;
    }
    int getPXfromDP(int value){
        Resources r = context.getResources();
        float px = value* r.getDisplayMetrics().density;
        return (int)px;
    }
}
