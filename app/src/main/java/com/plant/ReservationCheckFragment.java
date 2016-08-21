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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

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
        findParticipateRoomData.execute(((FrameActivity)getContext()).userData.userID);

        reservation_listView = (ListView) mainView.findViewById(R.id.reservation_check_listView);
        reservation_listView.setAdapter(reservation_listView_adapter);
        //roomDataList = ((FrameActivity) getActivity()).reservationCheckListCache;
        reservation_listView_adapter.setList(getActivity(), roomDataList);

        reservation_listView.setOnItemClickListener(new RoomDataListViewOnItemClickListener(getContext(),
                RoomDataListViewOnItemClickListener.DIALOG_MODE_CHECK));
    }

    public class FindParticipateRoomData extends AsyncTask<String, Void, ArrayList<RoomData>> {
        private String findParticipateURL = "http://plan-t.kr/findParticipateRoomData.php";
        private Context mContext;

        @Override
        protected ArrayList<RoomData> doInBackground(String... userIDInput) {
            ArrayList<RoomData> resultArrayList = new ArrayList<RoomData>();
            String userID = userIDInput[0];
            StringBuilder jsonResult = new StringBuilder();
            try {
                URL url = new URL(findParticipateURL + "?userID=" + userID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while ( true ) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        jsonResult.append(line + "\n");
                        resultArrayList.add(new Gson().fromJson(line, RoomData.class));
                    }
                    br.close();
                }
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return resultArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<RoomData> resultRoomData) {
            if(roomDataList != null){
                roomDataList.addAll(resultRoomData);
                reservation_listView_adapter.notifyDataSetChanged();
            }
        }
    }
/************* Login.php로 연결 END ***********************/

}