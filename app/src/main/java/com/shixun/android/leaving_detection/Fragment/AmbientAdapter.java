package com.shixun.android.leaving_detection.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shixun.android.leaving_detection.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shixunliu on 1/5/17.
 */

public class AmbientAdapter extends RecyclerView.Adapter<AmbientAdapter.AmbientViewHolder>{

    private List<String> mAmbientList;
    private Context mContext;
    private View view;
    private AlertDialog.Builder builder;

    @Override
    public AmbientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.single_ambient_set;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(layoutIdForListItem, parent, false);
        return new AmbientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AmbientViewHolder holder, int position) {
        String ambientName = mAmbientList.get(position);
        holder.mTextView.setText(ambientName);
        holder.mNumber.setText("1");
    }

    @Override
    public int getItemCount() {
        if (mAmbientList == null) {
            return 0;
        } else {
            return mAmbientList.size();
        }
    }

    public void setAmbientList(List<String> ambientList) {
        this.mAmbientList = ambientList;
        notifyItemInserted(mAmbientList.size() - 1);
    }

    public void addAmbientItem(String ambient) {
        this.mAmbientList.add(ambient);
        notifyItemInserted(mAmbientList.size() - 1);
    }

    public class AmbientViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextView;
        public final ImageView mMinus;
        public final ImageView mPlus;
        public final TextView mNumber;

        public AmbientViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mTextView = (TextView) view.findViewById(R.id.ambient_name);
            mMinus = (ImageView) view.findViewById(R.id.minus);
            mPlus = (ImageView) view.findViewById(R.id.plus);
            mNumber = (TextView) view.findViewById(R.id.ambient_number);
        }

        @OnClick(R.id.plus)
        public void plus() {
            int currentNum = Integer.parseInt(mNumber.getText().toString());
            mNumber.setText(String.valueOf(currentNum + 1));
        }

        @OnClick(R.id.minus)
        public void minus() {
            int currentNum = Integer.parseInt(mNumber.getText().toString());
            if(currentNum == 1) {
                showSimpleDialog(getAdapterPosition());
            } else {
                mNumber.setText(String.valueOf(currentNum - 1));
            }
        }

        private void showSimpleDialog(final int index) {
            builder=new AlertDialog.Builder(mContext);
            builder.setIcon(R.drawable.icon_warning);
            String ambient = mAmbientList.get(index);
            builder.setTitle("Delete Ambient");
            builder.setMessage("Do you want to delete " + ambient + " ?");

            //监听下方button点击事件
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mAmbientList.remove(index);
                    notifyItemRemoved(getLayoutPosition());//Attention!
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mNumber.setText("1");
                }
            });

            //设置对话框是可取消的
            builder.setCancelable(false);
            AlertDialog dialog=builder.create();
            dialog.show();
        }
    }
}
