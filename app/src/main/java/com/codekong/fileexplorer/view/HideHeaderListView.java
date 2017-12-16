package com.codekong.fileexplorer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.codekong.fileexplorer.R;

/**
 * Created by szh on 2017/2/9.
 */

public class HideHeaderListView extends ListView {
    private int mLastY;
    private int mOffsetY;
    private LayoutInflater mInflater;
    public HideHeaderListView(Context context) {
        this(context, null);
    }

    public HideHeaderListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HideHeaderListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
        View headerView = mInflater.inflate(R.layout.item_header, null);
        addHeaderView(headerView);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mOffsetY = (int) (ev.getY() - mLastY);
                break;
            default:
        }
        return false;
//        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
