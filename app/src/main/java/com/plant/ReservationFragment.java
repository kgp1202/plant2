package com.plant;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.zip.Inflater;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.AbsListViewOverScrollDecorAdapter;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;

public class ReservationFragment extends Fragment implements AbsListView.OnScrollListener{
    Context mContext;

    View mainView;
    ListView listView;
    EditText searchEditText;
    LinearLayout parentLayout;

    SwipeRefreshLayout swipeRefreshLayout;

    RoomListViewAdapter listViewAdapter = new RoomListViewAdapter();
    ArrayList<RoomData> roomDataList = new ArrayList<RoomData>();

    FragmentChangeListener mCallback;

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(mainView);
        roomDataList = null;
        searchEditText = null;
        listViewAdapter = null;
        listView = null;
    }

    private void unbindDrawables(View view){
        if (view.getBackground() != null){
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)){
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++){
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
        mCallback=(FragmentChangeListener)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_reservation, container, false);
        init();

        return mainView;
    }

    public void init() {
        swipeRefreshLayout = (SwipeRefreshLayout) mainView.findViewById(R.id.swipeRefreshLayout);

        ImageView btn = (ImageView) mainView.findViewById(R.id.reservationAddBtn);
        listView = (ListView) mainView.findViewById(R.id.reservationList);
        searchEditText = (EditText) mainView.findViewById(R.id.search_editText);
        parentLayout = (LinearLayout) mainView.findViewById(R.id.reserv_parent_layout2);

        listView.setOnItemClickListener(new RoomDataListViewOnItemClickListener(getContext(),
                RoomDataListViewOnItemClickListener.DIALOG_MODE_JOIN));


        //Get last items
        if(((FrameActivity)mContext).reservationListCache == null) {
            Log.d("null", "a");
            GetRoomDataPHP getRoomDataPHP = new GetRoomDataPHP();
            getRoomDataPHP.execute((long)0, (long)0);
        }else {
            roomDataList = (ArrayList<RoomData>) ((FrameActivity)mContext).reservationListCache.clone();
            refreshRoomDataList();
        }

        listViewAdapter.setList(getActivity(), roomDataList);
        listView.setAdapter(listViewAdapter);
        listView.setOnScrollListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.makeChange(4);
            }
        });

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    searchEditText.setText("");
                }
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("onEditorAction", "actionId" + actionId + " " + EditorInfo.IME_ACTION_SEARCH);
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        Log.d("onEditorAction", "a");
                        SearchPHP searchPHP = new SearchPHP();
                        searchPHP.execute(searchEditText.getText().toString());

                        parentLayout.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0,0);
                        return true;
                    default:
                        return false;
                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorOrange);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRoomDataList();
            }
        });
    }

    private void refreshRoomDataList(){
        RefreshRoomDataPHP refreshRoomDataPHP = new RefreshRoomDataPHP(swipeRefreshLayout);
        long toRoomID = roomDataList.get(0).roomID;
        Log.d("refresh start", " " + toRoomID);
        refreshRoomDataPHP.execute((long)0, toRoomID);
    }

    class RefreshRoomDataPHP extends GetRoomDataPHP{
        SwipeRefreshLayout mRefreshLayout;

        public RefreshRoomDataPHP(SwipeRefreshLayout swipeRefreshLayout){
            mRefreshLayout = swipeRefreshLayout;
        }

        @Override
        protected void onPostExecute(ArrayList<RoomData> resultRoomDataList) {
            for(int i = 0; i < resultRoomDataList.size(); i++){
                ((FrameActivity)mContext).reservationListCache.add(0, resultRoomDataList.get(i));
                Log.d("cache", " " + resultRoomDataList.get(i).roomID);
            }

            if(roomDataList != null){
                for(int i = 0; i < resultRoomDataList.size(); i++){
                    roomDataList.add(0, resultRoomDataList.get(i));
                    Log.d("roomDataList", " " + resultRoomDataList.get(i).roomID);
                }


                listViewAdapter.notifyDataSetChanged();
            }

            Log.d("refresh end", " ");
            mRefreshLayout.setRefreshing(false);
        }
    }

    /***************************** onScrollListner ************************************/
    boolean lastItemVisibleFlag = false;
    boolean firstItemVisibleFlag = false;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
        firstItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem == 1);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
        //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            GetRoomDataPHP getRoomDataPHP2 = new GetRoomDataPHP();
            long lastRoomID = roomDataList.get(roomDataList.size() - 1).roomID;
            getRoomDataPHP2.execute(lastRoomID, (long)0);
        }else if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag){
            Log.d("listview", "up");
        }
    }
    /***************************** onScrollListner end ************************************/

    /***************************** SearchPHP **********************************************/
    public class SearchPHP extends  AsyncTask<String, Void, Void>{
        public static final String SearchURL = "http://www.plan-t.kr/search.php";

        @Override
        protected Void doInBackground(String... params) {
            String destPoint = params[0];
            StringBuilder jsonResult = new StringBuilder();
            try {
                URL url = new URL(SearchURL + "?destPoint=" + destPoint);
                Log.d("SearchPHP destPoint", " " + destPoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                roomDataList.clear();
                if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while ( true ) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        jsonResult.append(line + "\n");
                        roomDataList.add(new Gson().fromJson(line, RoomData.class));
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for(int i = 0; i < roomDataList.size(); i++){
                Log.d("search result", " " + roomDataList.get(i).roomID);
            }

            listViewAdapter.notifyDataSetChanged();
            listView.setOnScrollListener(null);
        }
    }
    /***************************** SearchPHP end*******************************************/

    /***************************** GetRoomDataPHP*******************************************/
    //pageNum를 입력으로 받아서 해당 데이터를 roomDataList에 추가해준다.
    private class GetRoomDataPHP extends AsyncTask<Long, Void, ArrayList<RoomData>>{
        public static final String GetRoomDataURL = "http://www.plan-t.kr/getRoomData.php";

        @Override
        protected ArrayList<RoomData> doInBackground(Long... params) {
            long fromRoomID = params[0];
            long toRoomID = params[1];

            ArrayList<RoomData> resultRoomDataList = new ArrayList<RoomData>();
            try {
                Log.d("getRoomData", "?from=" + fromRoomID + "&to=" + toRoomID);
                URL url = new URL(GetRoomDataURL + "?from=" + fromRoomID + "&to=" + toRoomID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                //conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while ( true ) {
                        String line = br.readLine();
                        if (line == null)
                            break;

                        //url encode를 한글로 바꿈
                        RoomData roomData=new Gson().fromJson(line, RoomData.class);
                        URLDecoder decoder=new URLDecoder();
                        roomData.setDestPoint(decoder.decode(roomData.destPoint,"euc-kr"));
                        resultRoomDataList.add(roomData);
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

            return resultRoomDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<RoomData> resultRoomDataList) {
            if(resultRoomDataList == null){
                Log.d("fail", "a");
                return;
            }
            else {
                for(int i = 0; i < resultRoomDataList.size(); i++){
                    Log.d("GetRoomDataPHP result", "" + resultRoomDataList.get(i).roomID);
                }

                if((((FrameActivity) mContext).reservationListCache) == null){
                    ((FrameActivity) mContext).reservationListCache = new ArrayList<RoomData>();
                }
                if(roomDataList == null){
                    return;
                }
                ((FrameActivity) mContext).reservationListCache.addAll(resultRoomDataList);
                roomDataList.addAll(resultRoomDataList);
                listViewAdapter.notifyDataSetChanged();

                ((FrameActivity)mContext).isDoBackground = false;
            }
        }
    }
    /***************************** GetRoomDataPHP end***************************************/
}
