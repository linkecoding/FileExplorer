package com.codekong.fileexplorer.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.codekong.fileexplorer.R;
import com.codekong.fileexplorer.adapter.SwitchViewPagerAdapter;
import com.codekong.fileexplorer.fragment.FileListFragment;
import com.codekong.fileexplorer.util.FileUtils;
import com.codekong.fileexplorer.util.ViewUtils;
import com.codekong.fileexplorer.view.OperationMenuPopupWindow;
import com.codekong.fileexplorer.view.SortMenuPopupWindow;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity{
    private static final String TAG = "MainActivity";
    @BindView(R.id.id_more_operation)
    ImageView mMoreOperation;
    @BindView(R.id.id_switch_tab_layout)
    TabLayout mSwitchTabLayout;
    @BindView(R.id.id_switch_view_pager)
    ViewPager mSwitchViewPager;
    private Unbinder mUnbinder;

    private SwitchViewPagerAdapter mSwitchViewPagerAdapter;
    //上一次按返回键的时间
    private long mLastBackPressedTime = 0;
    //当前被选中的Fragment的position
    private int mCurrentFragmentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //设置状态栏的白底黑字
        ViewUtils.MIUISetStatusBarLightMode(getWindow(), true);
        mSwitchViewPagerAdapter = new SwitchViewPagerAdapter(this, getSupportFragmentManager());
        mSwitchViewPager.setAdapter(mSwitchViewPagerAdapter);
        mSwitchTabLayout.setupWithViewPager(mSwitchViewPager);

        mSwitchViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentFragmentPosition = position;
                if (position == 1) {
                    mMoreOperation.setVisibility(View.VISIBLE);
                } else if (position == 0) {
                    mMoreOperation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragmentPosition == 1) {
            //当前在手机文件管理页面的根目录
            if (FileUtils.getNowPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                //退出应用
                quitApplication();
            } else {
                //返回上级目录
                FileListFragment.mNowPathStack.pop();
                FragmentPagerAdapter fragmentPagerAdapter = (FragmentPagerAdapter) mSwitchViewPager.getAdapter();
                FileListFragment fileListFragment = (FileListFragment) fragmentPagerAdapter.instantiateItem(mSwitchViewPager, 1);
                fileListFragment.showChange(FileUtils.getNowStackPathString(FileListFragment.mNowPathStack));
            }
        } else {
            //退出应用
            quitApplication();
        }
    }

    /**
     * 双击退出应用
     */
    private void quitApplication() {
        if (System.currentTimeMillis() - mLastBackPressedTime > 2000) {
            Toast.makeText(this, R.string.str_again_click_exit, Toast.LENGTH_SHORT).show();
            mLastBackPressedTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @OnClick(R.id.id_more_operation)
    public void onClick() {
        final OperationMenuPopupWindow operationMenuPopupWindow = new OperationMenuPopupWindow(this.getSupportFragmentManager().getFragments().get(mCurrentFragmentPosition));
        operationMenuPopupWindow.showAtLocation(findViewById(R.id.id_main_activity), Gravity.TOP, 0, 0);
        operationMenuPopupWindow.setOnWindowItemClickListener(new OperationMenuPopupWindow.OnWindowItemClickListener() {
            @Override
            public void closeMenu() {
                MainActivity.this.closeMenu(operationMenuPopupWindow);
            }

            @Override
            public void sort(String path) {
                MainActivity.this.closeMenu(operationMenuPopupWindow);
                showSortMethodMenu();
            }

            @Override
            public void newFolder(final String path) {
                MainActivity.this.closeMenu(operationMenuPopupWindow);
                final View view = (LinearLayout) getLayoutInflater().inflate(R.layout.input_layout, null);
                //新文件名输入框
                final EditText et = view.findViewById(R.id.id_input_ed);
                //自定义弹出框标题
                final TextView titleTv = new TextView(MainActivity.this);
                titleTv.setText(MainActivity.this.getString(R.string.str_new_folder));
                titleTv.setTextSize(16);
                titleTv.setGravity(Gravity.CENTER_HORIZONTAL);
                new AlertDialog.Builder(MainActivity.this)
                        .setView(view)
                        .setCancelable(false)
                        .setPositiveButton(MainActivity.this.getString(R.string.str_new_create), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!TextUtils.isEmpty(et.getText())) {
                                    File file = new File(path, et.getText().toString());
                                    if (!file.exists()) {
                                        if (file.mkdirs()) {
                                            //创建文件夹成功,刷新目录显示
                                            MainActivity.this.refreshDirectory(path, true);
                                            Toast.makeText(MainActivity.this, getString(R.string.str_folder_create_success), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, getString(R.string.str_folder_create_failed), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        //文件夹已经存在
                                        Toast.makeText(MainActivity.this, getString(R.string.str_folder_exist), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton(MainActivity.this.getString(R.string.str_cancel), null)
                        .show();
            }

            @Override
            public void showHideFolder(String path, boolean showHideFile) {
                MainActivity.this.closeMenu(operationMenuPopupWindow);
                refreshDirectory(path, !showHideFile);
            }
        });
    }

    /**
     * 展示排序方式菜单
     */
    private void showSortMethodMenu() {
        final SortMenuPopupWindow sortMenuPopupWindow = new SortMenuPopupWindow(this.getSupportFragmentManager().getFragments().get(mCurrentFragmentPosition));
        sortMenuPopupWindow.showAtLocation(findViewById(R.id.id_main_activity), Gravity.TOP, 0, 0);
        sortMenuPopupWindow.setOnSortItemClickListener(new SortMenuPopupWindow.OnSortItemClickListener() {
            @Override
            public void closeMenu() {
               MainActivity.this.closeMenu(sortMenuPopupWindow);
            }

            @Override
            public void sortByName(String path) {
                MainActivity.this.closeMenu(sortMenuPopupWindow);
                MainActivity.this.refreshDirectory(path, true);
            }

            @Override
            public void sortBySizeDesc(String path) {
                MainActivity.this.closeMenu(sortMenuPopupWindow);
                //从大到小排序
                MainActivity.this.refreshDirectory(FileUtils.filterSortFileBySize(path, true), false);
            }

            @Override
            public void sortBySizeAsc(String path) {
                MainActivity.this.closeMenu(sortMenuPopupWindow);
                //从大到小排序
                MainActivity.this.refreshDirectory(FileUtils.filterSortFileBySize(path, false), false);
            }

            @Override
            public void sortByModifyDate(String path) {
                MainActivity.this.closeMenu(sortMenuPopupWindow);
                //从大到小排序
                MainActivity.this.refreshDirectory(FileUtils.filterSortFileByLastModifiedTime(path), false);
            }
        });
    }

    /**
     * 关闭隐藏下拉菜单
     */
    private void closeMenu(PopupWindow popupWindow) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 刷新目录显示
     * @param path
     * @param showHideFile
     */
    private void refreshDirectory(String path, boolean showHideFile){
        Fragment fragment = MainActivity.this.getSupportFragmentManager()
                .getFragments().get(mCurrentFragmentPosition);
        if (fragment instanceof FileListFragment) {
            FileListFragment fileListFragment = (FileListFragment) fragment;
            fileListFragment.showChange(FileUtils.filterSortFileByName(path, showHideFile));
        }
    }

    /**
     * 刷新目录显示
     * @param fileArray
     * @param showHideFile
     */
    private void refreshDirectory(File[] fileArray, boolean showHideFile){
        Fragment fragment = MainActivity.this.getSupportFragmentManager()
                .getFragments().get(mCurrentFragmentPosition);
        if (fragment instanceof FileListFragment) {
            FileListFragment fileListFragment = (FileListFragment) fragment;
            fileListFragment.showChange(fileArray);
        }
    }
}
