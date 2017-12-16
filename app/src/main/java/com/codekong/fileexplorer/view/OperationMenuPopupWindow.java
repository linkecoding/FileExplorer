package com.codekong.fileexplorer.view;

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

public class OperationMenuPopupWindow extends PopupWindow implements View.OnKeyListener {
    private static final String TAG = "OperationMenuPopupWindo";
    private Fragment mFragment;
    private View rootView;
    private OnWindowItemClickListener onWindowItemClickListener;
    //是否正在显示隐藏文件
    private static boolean showHideFile = false;

    public OperationMenuPopupWindow(Fragment fragment) {
        mFragment = fragment;
        LayoutInflater inflater = LayoutInflater.from(mFragment.getContext());
        rootView = inflater.inflate(R.layout.operation_popup_window_menu, null);
        TextView textView = rootView.findViewById(R.id.id_action_show_hide_folder);
        if (showHideFile){
            //目前隐藏文件是显示的
            textView.setText(fragment.getResources().getString(R.string.str_not_show_hidden_file));
        }else{
            textView.setText(fragment.getResources().getString(R.string.str_show_hide_file));
        }
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

    public void setOnWindowItemClickListener(OnWindowItemClickListener listener) {
        this.onWindowItemClickListener = listener;
    }

    @OnClick({R.id.id_close_menu, R.id.id_action_sort, R.id.id_action_new_folder, R.id.id_action_show_hide_folder})
    public void onClick(View view) {
        String path = "";
        if (mFragment.getView() != null){
            TextView pathTv = mFragment.getView().findViewById(R.id.id_now_file_path_tv);
            path = pathTv.getText().toString();
        }
        switch (view.getId()) {
            case R.id.id_close_menu:
                onWindowItemClickListener.closeMenu();
                break;
            case R.id.id_action_sort:
                onWindowItemClickListener.sort(path);
                break;
            case R.id.id_action_new_folder:
                onWindowItemClickListener.newFolder(path);
                break;
            case R.id.id_action_show_hide_folder:
                TextView textView = (TextView) view;
                if (showHideFile){
                    textView.setText(R.string.str_not_show_hidden_file);
                }else{
                    textView.setText(R.string.str_show_hide_file);
                }
                showHideFile = !showHideFile;
                onWindowItemClickListener.showHideFolder(path, showHideFile);
                break;
            default:
        }
    }

    public interface OnWindowItemClickListener {
        void closeMenu();

        void sort(String path);

        void newFolder(String path);

        void showHideFolder(String path, boolean showHideFile);
    }
}
