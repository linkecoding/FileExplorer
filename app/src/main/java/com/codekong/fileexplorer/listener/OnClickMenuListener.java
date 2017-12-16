package com.codekong.fileexplorer.listener;

import android.view.View;

import com.codekong.fileexplorer.R;

/**
 * Created by szh on 2017/2/10.
 */

public class OnClickMenuListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        switch ((int)view.getTag()){
            case (R.layout.operation_popup_window_menu + 111):
                break;
        }
    }
}
