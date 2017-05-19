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
 * Created by shixunliu on 8/5/17.
 */

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ModelViewHolder> {

    private List<File> fileList;
    private Context mContext;
    private OnItemClickListener clickListener;
    private View view;
    private AlertDialog.Builder builder;

    @Override
    public ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.single_model_layout;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ModelViewHolder holder, int position) {
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

    public class ModelViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextView;
        public final TextView mDelete;

        public ModelViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mTextView = (TextView) view.findViewById(R.id.model_name);
            mDelete = (TextView) view.findViewById(R.id.delete);
        }

        @OnClick(R.id.model_name)
        public void clickFile() {
            final File file = fileList.get(getLayoutPosition());
            isModelChosenDialog(file);
        }

        @OnClick(R.id.delete)
        public void clickDelete() {
            final File file = fileList.get(getLayoutPosition());
            isDeleteModelDialog(file);
        }

        private void isDeleteModelDialog(final File file) {
            builder=new AlertDialog.Builder(mContext);
            builder.setIcon(R.drawable.icon_warning);
            builder.setTitle("Delete Model");
            builder.setMessage("Do you want to delete " + file.getName() + " ?");

            //监听下方button点击事件
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fileList.remove(file);
                    RecursionDeleteFile(file);
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

        private void isModelChosenDialog(final File file) {
            builder=new AlertDialog.Builder(mContext);
            builder.setIcon(R.drawable.icon_selected);
            builder.setTitle("Model Selected");
            builder.setMessage("Do you want to select " + file.getName() + " model ?");

            //监听下方button点击事件
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (clickListener != null) {
                        clickListener.onClick(fileList, getLayoutPosition(), "model");
                    }
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

    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
}
