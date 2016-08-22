package com.plant;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Kim on 2016-08-21.
 */
public class InternetFailDIalog extends Dialog implements View.OnClickListener{
    Context mContext;

    Button okButton;

    public InternetFailDIalog(Context context) {
        super(context);

        mContext = context;
        setContentView(R.layout.dialog_internet_fail);

        okButton = (Button)findViewById(R.id.dialog_internet_fail_ok_button);
        okButton.setOnClickListener(this);
    }

   public static boolean checkInternetConnection(Context mContext){
       ConnectivityManager cm =
               (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo netInfo = cm.getActiveNetworkInfo();
       return netInfo != null && netInfo.isConnected();
   }

    @Override
    public void onClick(View v) {
        Log.d("internet okButton", "onClick");
        dismiss();
    }
}
