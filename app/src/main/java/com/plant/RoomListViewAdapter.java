package com.plant;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Kim on 2016-07-15.
 */
public class RoomListViewAdapter extends BaseAdapter {

    public Context myContext;
    private ArrayList<RoomData> list = new ArrayList<RoomData>();

    public void setList(Context con,ArrayList<RoomData> inputList){
        myContext=con;
        list = inputList;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        if(getCount() == 0){
            
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class Holder{
        TextView date;
        ImageView goalImage;
        ImageView reservationCnt;
        TextView destination;
        TextView startingPoint;
        ImageView returnImage;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder myHolder;
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.reservation_list_item, parent, false);

            myHolder = new Holder();

            myHolder.goalImage = (ImageView) convertView.findViewById(R.id.goalImage);
            myHolder.destination = (TextView) convertView.findViewById(R.id.destinationPoint);
            myHolder.startingPoint = (TextView) convertView.findViewById(R.id.startingPoint);
            myHolder.returnImage = (ImageView) convertView.findViewById(R.id.returnImage);
            myHolder.date = (TextView) convertView.findViewById(R.id.date);
            myHolder.reservationCnt = (ImageView) convertView.findViewById(R.id.reservationCnt);

            convertView.setTag(myHolder);
        }
        else{
            myHolder = (Holder)convertView.getTag();
        }
        RoomData temp=list.get(position);
        DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr =sdFormat.format(temp.startTime);
        myHolder.date.setText(dateStr);

        switch (temp.roomObject){
            case 0:
                myHolder.goalImage.setImageResource(R.drawable.certification);
                break;
            case 1:
                myHolder.goalImage.setImageResource(R.drawable.toeic);
                break;
            case 2:
                myHolder.goalImage.setImageResource(R.drawable.etc);
                break;
        }

        switch (temp.userNum){
            case 1:
                myHolder.reservationCnt.setImageResource(R.drawable.reservation_count_1);
                break;
            case 2:
                myHolder.reservationCnt.setImageResource(R.drawable.reservation_count_2);
                break;
            case 3:
                myHolder.reservationCnt.setImageResource(R.drawable.reservation_count_3);
                break;
            case 4:
                myHolder.reservationCnt.setImageResource(R.drawable.reservation_count_4);
                break;
        }

        if(temp.round)
            myHolder.returnImage.setImageResource(R.drawable.dialog_detail_round);
        else
            myHolder.returnImage.setImageResource(R.drawable.dialog_detail_non_round);

        if(temp.startingPoint==1) myHolder.startingPoint.setText("후문");
        else if(temp.startingPoint==3) myHolder.startingPoint.setText("정문");


        URLDecoder decoder=new URLDecoder();
        try {
            temp.setDestPoint(decoder.decode(temp.destPoint,"euc-kr"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        myHolder.destination.setText(temp.destPoint);

        return convertView;
    }
}
