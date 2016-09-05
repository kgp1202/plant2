package com.plant;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Kim on 2016-08-31.
 */

public class BasicDialog extends Dialog {
    static final int TEXT_MODE = 1;
    static final int LIST_MODE = 2;

    Context mContext;
    TextView title;
    TextView content;
    Button yesButton;
    Button noButton;
    ListView contentListView;

    public BasicDialog(Context context, int mode) {
        super(context);;
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        switch(mode){
            case TEXT_MODE:
                setContentView(R.layout.dialog_basic);
                content = (TextView) findViewById(R.id.dialog_detail_content);

                break;
            case LIST_MODE:
                setContentView(R.layout.dialog_basic_list);

        }

        setCancelable(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        title = (TextView) findViewById(R.id.dialog_detail_title);
        yesButton = (Button) findViewById(R.id.dialog_detail_ok_btn);
        noButton = (Button) findViewById(R.id.dialog_detail_cancel_btn);
    }
}
