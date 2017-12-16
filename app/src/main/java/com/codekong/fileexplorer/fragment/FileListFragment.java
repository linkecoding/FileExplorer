package com.codekong.fileexplorer.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codekong.fileexplorer.R;
import com.codekong.fileexplorer.adapter.FileListAdapter;
import com.codekong.fileexplorer.util.FileUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by szh on 2017/2/9.
 * 文件列表Fragment
 */

public class FileListFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.id_start_search)
    TextView mStartSearch;
    @BindView(R.id.id_now_file_path_tv)
    TextView mNowFilePathTv;
    @BindView(R.id.id_file_list_view)
    ListView mFileListView;
    @BindView(R.id.id_empty_view)
    TextView mEmptyView;
    @BindView(R.id.id_file_list_pulltorefresh)
    PullToRefreshLayout mPullToRefreshLayout;

    private Unbinder mUnbinder;
    //文件列表数组
    private File[] mFilesArray;
    //文件列表List
    private ArrayList<File> mFileList = new ArrayList<>();
    //文件ListView适配器
    private FileListAdapter mFileListAdapter;
    //文件根路径
    private String mRootPath;
    //当前的文件路径堆栈
    public static Stack<String> mNowPathStack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void loadData() {
        initEvent();
        initData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mFileListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != 0){
                    disablePullToRefresh(mPullToRefreshLayout, false);
                }else{
                    disablePullToRefresh(mPullToRefreshLayout, true);
                }
            }
        });

        mPullToRefreshLayout.setCanLoadMore(false);
        mPullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                initData();
                mPullToRefreshLayout.finishRefresh();
            }

            @Override
            public void loadMore() {

            }
        });
    }


    /**
     * 初始化数据并显示
     */
    private void initData() {
        String fileNowPath = "";
        //对文件进行过滤和排序
        mFilesArray = FileUtils.filterSortFileByName(Environment.getExternalStorageDirectory().getPath(), true);
        mFileList.clear();
        mFileList.addAll(Arrays.asList(mFilesArray));
        mRootPath = Environment.getExternalStorageDirectory().getPath();
        mNowPathStack = new Stack<>();
        mNowPathStack.push(mRootPath);
        fileNowPath = FileUtils.getNowStackPathString(mNowPathStack);
        //设置文件路径显示
        mNowFilePathTv.setText(fileNowPath);
        mFileListAdapter = new FileListAdapter(this.getContext(), mFileList);
        mFileListView.setAdapter(mFileListAdapter);
        mFileListView.setOnItemClickListener(this);
        //设置没有item显示时默认显示的图标
        mFileListView.setEmptyView(mEmptyView);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        File file = mFileList.get(position);
        String fileName = file.getName();
        if (file.isFile()) {
            //如果是File则打开
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri data = Uri.fromFile(file);
            int index = fileName.lastIndexOf(".");
            String suffix = fileName.substring(index + 1);
            String type = MimeTypeMap.getSingleton().getExtensionFromMimeType(suffix);
            if (type == null) {
                Toast.makeText(getContext(), R.string.str_no_program_can_open_the_file, Toast.LENGTH_SHORT).show();
                return;
            }
            intent.setDataAndType(data, type);
            startActivity(intent);
        } else {
            //是目录则进入下级目录
            mNowPathStack.push("/" + fileName);
            showChange(FileUtils.getNowStackPathString(mNowPathStack));
        }
    }

    /**
     * 改变数据源，刷新列表
     */
    public void showChange(String path) {
        mNowFilePathTv.setText(path);
        mFilesArray = FileUtils.filterSortFileByName(path, true);
        mFileList.clear();
        mFileList.addAll(Arrays.asList(mFilesArray));
        mFileListAdapter.updateFileList(mFileList);
    }

    /**
     * 改变数据源，刷新列表
     */
    public void showChange(File[] fileArray) {
        mFilesArray = fileArray;
        mFileList.clear();
        mFileList.addAll(Arrays.asList(mFilesArray));
        mFileListAdapter.updateFileList(mFileList);
    }

    @OnClick(R.id.id_now_file_path_tv)
    public void onViewClicked() {
        //点击返回上级目录
        if (!mNowPathStack.empty()){
            if (mNowPathStack.peek().equals(Environment.getExternalStorageDirectory().getPath())){
                return;
            }
            mNowPathStack.pop();
        }
        showChange(FileUtils.getNowStackPathString(mNowPathStack));
    }

    /**
     * 通过反射禁用下拉刷新
     * @param disable
     */
    private void disablePullToRefresh(PullToRefreshLayout pullToRefreshLayout, boolean disable){
        Class<PullToRefreshLayout> pullToRefreshClass = PullToRefreshLayout.class;
        try {
            Field canRefreshField = pullToRefreshClass.getDeclaredField("canRefresh");
            canRefreshField.setAccessible(true);
            canRefreshField.set(pullToRefreshLayout, disable);
            canRefreshField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
