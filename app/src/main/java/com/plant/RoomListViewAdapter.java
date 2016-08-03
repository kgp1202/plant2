package com.plant;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Kim on 2016-07-15.
 */
public class RoomListViewAdapter extends BaseAdapter {

    private ArrayList<RoomData> list = new ArrayList<RoomData>();

    public void setList(ArrayList<RoomData> inputList){
        list = inputList;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.reservation_list_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.room_data);
        textView.setText(list.get(position).getRoomDataJSONString());

        return convertView;
    }
}
