package com.codekong.fileexplorer.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.codekong.fileexplorer.R;
import com.codekong.fileexplorer.adapter.CommonAdapter;
import com.codekong.fileexplorer.adapter.ViewHolder;
import com.codekong.fileexplorer.bean.Category;
import com.codekong.fileexplorer.config.Constant;
import com.codekong.fileexplorer.util.ScanFileCountUtil;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by szh on 2017/2/9.
 * 文件类别Fragment
 */

public class FileCategoryFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_EXTERNAL_STORAGE = 0X01;

    @BindView(R.id.id_start_search)
    TextView mStartSearchBtn;
    @BindView(R.id.id_category_grid_view)
    GridView mCategoryGridView;
    @BindView(R.id.id_loading_framelayout)
    FrameLayout mLoadingFrameLayout;
    @BindView(R.id.id_file_category_pulltorefresh)
    PullToRefreshLayout mPullToRefreshLayout;

    //存放分类数据
    private List<Category> mCategoryData = new ArrayList<>();

    //文件扫描结束的处理
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            mCategoryData.clear();
            mPullToRefreshLayout.finishRefresh();
            Map<String, Integer> countRes = (Map<String, Integer>) msg.obj;
            for (int i = 0; i < Constant.FILE_CATEGORY_ICON.length; i++) {
                Category category = new Category();
                category.setCategoryIcon(Constant.FILE_CATEGORY_ICON[i]);
                category.setCategoryName(Constant.FILE_CATEGORY_NAME[i]);
                category.setCategoryNums(countRes.get(Constant.FILE_CATEGORY_ICON[i].substring(3)) + "项");
                mCategoryData.add(category);
            }
            mLoadingFrameLayout.setVisibility(View.GONE);
            setData();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_category, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void loadData() {
        if (!EasyPermissions.hasPermissions(FileCategoryFragment.this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            EasyPermissions.requestPermissions(this,"需要读取文件目录",
                    REQUEST_CODE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else {
            //已经授权
            initEvent();
            //扫描文件
            scanFile();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mPullToRefreshLayout.setCanLoadMore(false);
        mPullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                scanFile();
            }

            @Override
            public void loadMore() {

            }
        });
    }

    /**
     * 设置数据
     */
    private void setData() {
       CommonAdapter<Category> mAdapter = new CommonAdapter<Category>(this.getContext(), mCategoryData, R.layout.category_item) {
            @Override
            public void convert(ViewHolder helper, Category item) {
                helper.setImageResource(R.id.id_category_icon, getResId(item.getCategoryIcon()));
                helper.setText(R.id.id_category_name, item.getCategoryName());
                helper.setText(R.id.id_category_nums, item.getCategoryNums());
            }
        };
        mCategoryGridView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public int getResId(String iconName){
        return getContext().getResources().getIdentifier(iconName, "drawable", getContext().getPackageName());
    }

    /**
     * 扫描文件
     */
    private void scanFile(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return;
        }
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        //单一线程线程池
        ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();
        singleExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                ScanFileCountUtil scanFileCountUtil = new ScanFileCountUtil
                        .Builder(mHandler)
                        .setFilePath(path)
                        .setCategorySuffix(Constant.CATEGORY_SUFFIX)
                        .create();
                scanFileCountUtil.scanCountFile();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        initEvent();
        //扫描文件
        scanFile();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,
                Collections.singletonList(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
