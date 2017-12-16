package com.codekong.fileexplorer.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by szh on 2017/2/9.]
 * 常量表
 */

public class Constant {
    /*************************文件排序方式(文件夹在前,文件在后)****************************/
    //按文件名和文件大小排序时，文件夹按照数字在前，字母在后的顺序先排序，再按文件的具体要求排序
    //文件名
    public static final int SORT_BY_FILE_NAME = 1;
    //文件大小(从小到大)
    public static final int SORT_BY_FILE_SIZE_ASC = 2;
    //文件大小(从大到小)
    public static final int SORT_BY_FILE_SIZE_DESC = 3;
    //文件类型(将文件后缀相同的排列到一起，后缀按数字字母顺序排列)
    public static final int SORT_BY_FILE_TYPE = 4;
    //文件修改时间
    public static final int SORT_BY_FILE_MIDIFY_TIME = 4;
    /*************************文件排序方式****************************/

    /*************************文件分类****************************/
    public static final String[] FILE_CATEGORY_NAME = {"视频", "文档", "图片", "音乐", "安装包", "压缩包"};

    public static final String[] FILE_CATEGORY_ICON = {"ic_video", "ic_document", "ic_picture", "ic_music",
    "ic_apk", "ic_zip"};
    /*************************文件分类****************************/

    //每种类型的文件包含的后缀
    public static final Map<String, Set<String>> CATEGORY_SUFFIX;

    static {
        //初始化赋值
        CATEGORY_SUFFIX = new HashMap<>(FILE_CATEGORY_ICON.length);
        Set<String> set = new HashSet<>();
        set.add("mp4");
        set.add("avi");
        set.add("wmv");
        set.add("flv");
        CATEGORY_SUFFIX.put("video", set);

        set.add("txt");
        set.add("pdf");
        set.add("doc");
        set.add("docx");
        set.add("xls");
        set.add("xlsx");
        CATEGORY_SUFFIX.put("document", set);

        set = new HashSet<>();
        set.add("jpg");
        set.add("jpeg");
        set.add("png");
        set.add("bmp");
        set.add("gif");
        CATEGORY_SUFFIX.put("picture", set);

        set = new HashSet<>();
        set.add("mp3");
        set.add("ogg");
        CATEGORY_SUFFIX.put("music", set);

        set = new HashSet<>();
        set.add("apk");
        CATEGORY_SUFFIX.put("apk", set);

        set = new HashSet<>();
        set.add("zip");
        set.add("rar");
        set.add("7z");
        CATEGORY_SUFFIX.put("zip", set);
    }

}
