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
import android.text.Editable;
import android.text.Layout;
import android.text.method.KeyListener;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class ReservationFragment extends Fragment implements AbsListView.OnScrollListener{

    View mainView;
    ListView listView;
    EditText searchEditText;
    LinearLayout parentLayout;

    RoomListViewAdapter listViewAdapter = new RoomListViewAdapter();
    ArrayList<RoomData> roomDataList = new ArrayList<RoomData>();
    int downLoadedNum = 0;

    FragmentChangeListener mCallback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
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

    public void init(){
        ImageView btn=(ImageView)mainView.findViewById(R.id.reservationAddBtn);
        listView = (ListView) mainView.findViewById(R.id.reservationList);
        searchEditText = (EditText) mainView.findViewById(R.id.search_editText);
        parentLayout = (LinearLayout) mainView.findViewById(R.id.reserv_parent_layout2);

        listView.setAdapter(listViewAdapter);
        listViewAdapter.setList(roomDataList);
        listView.setOnScrollListener(this);

        listView.setOnItemClickListener(new RoomDataListViewOnItemClickListener(getContext(),
                RoomDataListViewOnItemClickListener.DIALOG_MODE_JOIN));

        GetRoomDataPHP getRoomDataPHP = new GetRoomDataPHP();
        getRoomDataPHP.execute(0);

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
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        Log.d("Search", "search");
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
    }

    /***************************** onScrollListner ************************************/
    boolean lastItemVisibleFlag = false;
    boolean flag_loading = false;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
        //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            GetRoomDataPHP getRoomDataPHP2 = new GetRoomDataPHP();
            getRoomDataPHP2.execute(++downLoadedNum);
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
            //Log.d("result", jsonResult.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listViewAdapter.notifyDataSetChanged();
            listView.setOnScrollListener(null);
        }
    }
    /***************************** SearchPHP end*******************************************/

    /***************************** GetRoomDataPHP*******************************************/
    //pageNum를 입력으로 받아서 해당 데이터를 roomDataList에 추가해준다.
    public class GetRoomDataPHP extends AsyncTask<Integer, Void, Void>{
        public static final String GetRoomDataURL = "http://www.plan-t.kr/getRoomData.php";

        @Override
        protected Void doInBackground(Integer... params) {
            int pageNum = params[0];

            StringBuilder jsonResult = new StringBuilder();
            try {
                URL url = new URL(GetRoomDataURL + "?pageNum=" + pageNum);
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

            //Log.d("result", jsonResult.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listViewAdapter.notifyDataSetChanged();
            listView.setOnScrollListener(ReservationFragment.this);
        }
    }
    /***************************** GetRoomDataPHP end***************************************/
}
