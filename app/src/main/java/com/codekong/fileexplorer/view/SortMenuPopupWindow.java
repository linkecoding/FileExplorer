package com.codekong.fileexplorer.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.codekong.fileexplorer.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by szh on 2017/2/13.
 * 打开选择菜单PopupWindow
 */

public class SortMenuPopupWindow extends PopupWindow implements View.OnKeyListener {
    private Context mContext;
    private View rootView;
    private Fragment mFragment;
    private OnSortItemClickListener onSortItemClickListener;

    public SortMenuPopupWindow(Fragment fragment) {
        mFragment = fragment;
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        rootView = inflater.inflate(R.layout.sort_popup_window_menu, null);
        setContentView(rootView);
        ButterKnife.bind(this, rootView);
        //设置高度和宽度。
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);

        //设置动画效果
        this.setAnimationStyle(R.style.PopupWindowAnimStyle);

        //当单击Back键或者其他地方使其消失、需要设置这个属性。
        rootView.setOnKeyListener(this);
        rootView.setFocusable(true);
        rootView.setFocusableInTouchMode(true);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);
    }

    //点back键消失
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.isShowing()) {
            this.dismiss();
            return true;
        }
        return false;
    }

    public void setOnSortItemClickListener(OnSortItemClickListener listener) {
        this.onSortItemClickListener = listener;
    }

    @OnClick({R.id.id_close_sort_menu, R.id.id_sort_name, R.id.id_sort_desc, R.id.id_sort_asc, R.id.id_sort_midify_date})
    public void onClick(View view) {
        String path = "";
        if (mFragment.getView() != null){
            TextView pathTv = mFragment.getView().findViewById(R.id.id_now_file_path_tv);
            path = pathTv.getText().toString();
        }
        switch (view.getId()) {
            case R.id.id_close_sort_menu:
                onSortItemClickListener.closeMenu();
                break;
            case R.id.id_sort_name:
                onSortItemClickListener.sortByName(path);
                break;
            case R.id.id_sort_desc:
                onSortItemClickListener.sortBySizeDesc(path);
                break;
            case R.id.id_sort_asc:
                onSortItemClickListener.sortBySizeAsc(path);
                break;
            case R.id.id_sort_midify_date:
                onSortItemClickListener.sortByModifyDate(path);
                break;
            default:
        }
    }


    public interface OnSortItemClickListener {
        void closeMenu();
        void sortByName(String path);
        void sortBySizeDesc(String path);
        void sortBySizeAsc(String path);
        void sortByModifyDate(String path);
    }
}
