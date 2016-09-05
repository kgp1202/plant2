package com.plant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

/**
 * Created by Kim on 2016-07-27.
 */
public class RoomDataListViewOnItemClickListener implements AdapterView.OnItemClickListener{
    Context mContext;
    int mode;

    RoomDataDialog dialog = null;

    public static final int DIALOG_MODE_JOIN = 1;
    public static final int DIALOG_MODE_CHECK = 2;

    public RoomData clickedItem;

    public RoomDataListViewOnItemClickListener(Context context, int dialog_mode){
        mContext = context;
        mode = dialog_mode;

        /********************** Dialog 만들기 ******************************/
        dialog = new RoomDataDialog(mContext, dialog_mode);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clickedItem = (RoomData)parent.getAdapter().getItem(position);
        dialog.show(clickedItem);
    }
}
