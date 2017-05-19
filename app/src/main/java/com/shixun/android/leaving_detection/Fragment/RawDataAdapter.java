package com.shixun.android.leaving_detection.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shixun.android.leaving_detection.R;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shixunliu on 1/5/17.
 */

public class RawDataAdapter extends RecyclerView.Adapter<RawDataAdapter.FileViewHolder>{

    private List<File> fileList;
    private Context mContext;
    private OnItemClickListener clickListener;
    private View view;
    private AlertDialog.Builder builder;

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.single_file_layout;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(layoutIdForListItem, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        File file = fileList.get(position);
        holder.mTextView.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        if (fileList == null) {
            return 0;
        } else {
            return fileList.size();
        }
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static interface OnItemClickListener {
        void onClick(List<File> fileList, int position, String category);
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextView;
        public final TextView mTrain;
        public final TextView mDelete;

        public FileViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mTextView = (TextView) view.findViewById(R.id.file_name);
            mTrain = (TextView) view.findViewById(R.id.start_train);
            mDelete = (TextView) view.findViewById(R.id.delete);
        }

        @OnClick(R.id.file_name)
        public void clickFile() {
            if (clickListener != null) {
                clickListener.onClick(fileList, getLayoutPosition(), "file");
            }
        }

        @OnClick(R.id.start_train)
        public void startTrain() {
            if (clickListener != null) {
                clickListener.onClick(fileList, getLayoutPosition(), "train");
            }
        }

        @OnClick(R.id.delete)
        public void clickDelete() {
            File file = fileList.get(getLayoutPosition());
            showSimpleDialog(file);
        }

        private void showSimpleDialog(final File file) {
            builder=new AlertDialog.Builder(mContext);
            builder.setIcon(R.drawable.icon_warning);
            builder.setTitle("Delete Raw Data");
            builder.setMessage("Do you want to delete " + file.getName() + " ?");

            //监听下方button点击事件
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fileList.remove(file);
                    file.delete();
                    notifyItemRemoved(getLayoutPosition());//Attention!
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            //设置对话框是可取消的
            builder.setCancelable(false);
            AlertDialog dialog=builder.create();
            dialog.show();
        }
    }
}
