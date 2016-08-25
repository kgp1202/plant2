package com.plant;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

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

public class ReservationCheckFragment extends Fragment {
    View mainView;
    ListView reservation_listView;
    RoomListViewAdapter reservation_listView_adapter = new RoomListViewAdapter();

    ArrayList<RoomData> roomDataList = new ArrayList<RoomData>();

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(mainView);
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

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

    @Override
    public void onResume() {
        super.onResume();
        reservation_listView_adapter.notifyDataSetChanged();
    }

    public void init() {
        FindParticipateRoomData findParticipateRoomData = new FindParticipateRoomData();
        if(HttpRequest.isInternetConnected(getContext()))
            findParticipateRoomData.execute(((FrameActivity)getContext()).userData.userID);
        else {
            //인터넷 연결이 안되어 있을 떄의 처리.
        }

        reservation_listView = (ListView) mainView.findViewById(R.id.reservation_check_listView);
        reservation_listView.setAdapter(reservation_listView_adapter);
        reservation_listView_adapter.setList(getActivity(), roomDataList);

        reservation_listView.setOnItemClickListener(new RoomDataListViewOnItemClickListener(getContext(),
                RoomDataListViewOnItemClickListener.DIALOG_MODE_CHECK));
    }

    public class FindParticipateRoomData extends AsyncTask<String, Void, Void> {
        private String findParticipateURL = "http://plan-t.kr/findParticipateRoomData.php";
        private HttpRequest findParticipateRoomDataRequest;

        @Override
        protected Void doInBackground(String... userIDInput) {
            String userID = userIDInput[0];
            findParticipateRoomDataRequest = new HttpRequest(getContext(), findParticipateURL + "?userID=" + userID);
            Thread t = new Thread(findParticipateRoomDataRequest);
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            if(roomDataList != null){
                String lines[] = findParticipateRoomDataRequest.requestResult.split(System.getProperty("line.separator"));

                for(int i = 0; i < lines.length; i++){
                    if(lines[i].equals("")) break;
                    roomDataList.add(new Gson().fromJson(lines[i], RoomData.class));
                }

                if(roomDataList.size() == 0){
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.fragment_reservation_check_no_room, null);
                    RelativeLayout r = (RelativeLayout)mainView.findViewById(R.id.fragment_reservation_check_frame);
                    r.removeAllViews();
                    r.addView(v, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    //reservation_listView.setBackgroundResource(R.drawable.reservation_no_room);
                }else {
                    reservation_listView.setBackground(null);
                    reservation_listView_adapter.notifyDataSetChanged();
                }
                ((FrameActivity)getContext()).stopProgressBar();
            }
        }
    }
/************* Login.php로 연결 END ***********************/

}