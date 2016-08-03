package com.plant;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReservationCheckFragment extends Fragment{
    View mainView;
    ListView reservation_listView;
    RoomListViewAdapter reservation_listView_adapter = new RoomListViewAdapter();

    ArrayList<RoomData> roomDataList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_reservation_check, container, false);
        init();

        return mainView;
    }

    public void init(){
        reservation_listView = (ListView) mainView.findViewById(R.id.reservation_check_listView);

        reservation_listView.setAdapter(reservation_listView_adapter);
        roomDataList = ((FrameActivity)getActivity()).roomDataList;
        reservation_listView_adapter.setList(roomDataList);
        reservation_listView_adapter.notifyDataSetChanged();

        reservation_listView.setOnItemClickListener(new RoomDataListViewOnItemClickListener(getContext(),
                RoomDataListViewOnItemClickListener.DIALOG_MODE_CHECK));
    }
}
