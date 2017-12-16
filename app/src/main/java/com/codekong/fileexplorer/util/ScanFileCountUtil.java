package com.codekong.fileexplorer.util;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by 尚振鸿 on 17-12-16. 14:26
 * mail:szh@codekong.cn
 * 扫描文件并统计工具类
 */

public class ScanFileCountUtil {
    //扫描根目录
    private String mFilePath;

    //各个分类所对应的文件后缀
    private Map<String, Set<String>> mCategorySuffix;
    //最终的统计结果
    private ConcurrentHashMap<String, Integer> mCountResult;
    //用于存储文件目录便于层次遍历
    private ConcurrentLinkedQueue<File> mFileConcurrentLinkedQueue;
    private Handler mHandler = null;

    public void scanCountFile() {
        if (mFilePath == null) {
            return;
        }
        final File file = new File(mFilePath);

        //非目录或者目录不存在直接返回
        if (!file.exists() || file.isFile()) {
            return;
        }
        //初始化每个类别的数目为0
        for (String category : mCategorySuffix.keySet()) {
            //将最后统计结果的key设置为类别
            mCountResult.put(category, 0);
        }

        //获取到根目录下的文件和文件夹
        final File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                //过滤掉隐藏文件
                return !file.getName().startsWith(".");
            }
        });

        List<Runnable> runnableList = new ArrayList<>();
        //创建信号量(最多同时有10个线程可以访问)
        final Semaphore semaphore = new Semaphore(100);
        for (File f : files) {
            if (f.isDirectory()) {
                //把目录添加进队列
                mFileConcurrentLinkedQueue.offer(f);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        countFile();
                    }
                };
                runnableList.add(runnable);
            } else {
                //找到该文件所属的类别
                for (Map.Entry<String, Set<String>> entry : mCategorySuffix.entrySet()) {
                    //获取文件后缀
                    String suffix = f.getName().substring(f.getName().indexOf(".") + 1).toLowerCase();
                    //找到了
                    if (entry.getValue().contains(suffix)) {
                        mCountResult.put(entry.getKey(), mCountResult.get(entry.getKey()) + 1);
                        break;
                    }
                }
            }
        }

        //固定数目线程池(最大线程数目为cpu核心数,多余线程放在等待队列中)
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (Runnable runnable : runnableList) {
            executorService.submit(runnable);
        }
        executorService.shutdown();
        //等待线程池中的所有线程运行完成
        while (true) {
            if (executorService.isTerminated()) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //传递统计数据给UI界面
        Message msg = Message.obtain();
        msg.obj = mCountResult;
        mHandler.sendMessage(msg);
    }

    /**
     * 统计各类型文件数目
     */
    private void countFile() {
        //对目录进行层次遍历
        while (!mFileConcurrentLinkedQueue.isEmpty()) {
            //队头出队列
            final File tmpFile = mFileConcurrentLinkedQueue.poll();
            final File[] fileArray = tmpFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    //过滤掉隐藏文件
                    return !file.getName().startsWith(".");
                }
            });

            for (File f : fileArray) {
                if (f.isDirectory()) {
                    //把目录添加进队列
                    mFileConcurrentLinkedQueue.offer(f);
                } else {
                    //找到该文件所属的类别
                    for (Map.Entry<String, Set<String>> entry : mCategorySuffix.entrySet()) {
                        //获取文件后缀
                        String suffix = f.getName().substring(f.getName().indexOf(".") + 1).toLowerCase();
                        //找到了
                        if (entry.getValue().contains(suffix)) {
                            mCountResult.put(entry.getKey(), mCountResult.get(entry.getKey()) + 1);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static class Builder {
        private Handler mHandler;
        private String mFilePath;
        //各个分类所对应的文件后缀
        private Map<String, Set<String>> mCategorySuffix;

        public Builder(Handler handler) {
            this.mHandler = handler;
        }

        public Builder setFilePath(String filePath) {
            this.mFilePath = filePath;
            return this;
        }

        public Builder setCategorySuffix(Map<String, Set<String>> categorySuffix) {
            this.mCategorySuffix = categorySuffix;
            return this;
        }

        private void applyConfig(ScanFileCountUtil scanFileCountUtil) {
            scanFileCountUtil.mFilePath = mFilePath;
            scanFileCountUtil.mCategorySuffix = mCategorySuffix;
            scanFileCountUtil.mHandler = mHandler;
            scanFileCountUtil.mCountResult = new ConcurrentHashMap<String, Integer>(mCategorySuffix.size());
            scanFileCountUtil.mFileConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
        }

        public ScanFileCountUtil create() {
            ScanFileCountUtil scanFileCountUtil = new ScanFileCountUtil();
            applyConfig(scanFileCountUtil);
            return scanFileCountUtil;
        }
    }
}
