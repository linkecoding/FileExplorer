package com.codekong.fileexplorer.bean;

/**
 * Created by szh on 2017/2/13.
 * 分类Bean
 */

public class Category {
    //每种类别的图标名称
    private String mCategoryIcon;
    //每种类别的名称
    private String mCategoryName;
    //每种类别下的项目数目
    private String mCategoryNums;

    public String getCategoryIcon() {
        return mCategoryIcon;
    }

    public void setCategoryIcon(String mCategoryIcon) {
        this.mCategoryIcon = mCategoryIcon;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String mCategoryName) {
        this.mCategoryName = mCategoryName;
    }

    public String getCategoryNums() {
        return mCategoryNums;
    }

    public void setCategoryNums(String mCategoryNums) {
        this.mCategoryNums = mCategoryNums;
    }
}
