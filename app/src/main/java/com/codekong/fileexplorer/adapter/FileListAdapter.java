package com.codekong.fileexplorer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codekong.fileexplorer.R;
import com.codekong.fileexplorer.util.FileUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by szh on 2017/2/8.
 */

public class FileListAdapter extends BaseAdapter {
    private Context mContext;
    private List<File> mFileList;

    public FileListAdapter(Context context, List<File> fileList) {
        this.mContext = context;
        this.mFileList = fileList;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public File getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file = mFileList.get(position);
        FileViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.file_item, null);
            viewHolder = new FileViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FileViewHolder) convertView.getTag();
        }

        if (file.isDirectory()) {
            viewHolder.fileSize.setText(R.string.str_folder);
            viewHolder.fileIcon.setImageResource(R.drawable.ic_folder);
            viewHolder.nextDir.setVisibility(View.VISIBLE);
        } else {
            viewHolder.fileSize.setText(FileUtils.getFileSize(file));
            viewHolder.fileIcon.setImageResource(R.drawable.ic_file);
            viewHolder.nextDir.setVisibility(View.GONE);
        }

        viewHolder.fileDate.setText(FileUtils.getFileDate(file));
        viewHolder.fileName.setText(file.getName());

        return convertView;
    }


    public void updateFileList(List<File> fileList) {
        this.mFileList = fileList;
        notifyDataSetChanged();
    }

    class FileViewHolder {
        @BindView(R.id.id_file_icon)
        ImageView fileIcon;
        @BindView(R.id.id_file_name)
        TextView fileName;
        @BindView(R.id.id_file_size)
        TextView fileSize;
        @BindView(R.id.id_file_date)
        TextView fileDate;
        @BindView(R.id.id_next_dir)
        ImageButton nextDir;

        FileViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
