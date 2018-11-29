package com.example.paulz.imagepicker.dragview;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.paulz.imagepicker.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;


/**
 * Created by Paul Z on 2018/7/20.
 * 可设置图片个数的图片的适配器
 */

public class SizePicAdapter extends RecyclerView.Adapter<SizePicAdapter.ViewHolder> {

    private List<String> datas;
    private Context mContext;
    private LayoutInflater mLiLayoutInflater;
    private boolean isDeleteEnable=true;

    public SizePicAdapter(List<String> datas, Context context) {
        this.datas = datas;
        this.mContext = context;
        this.mLiLayoutInflater = LayoutInflater.from(mContext);
    }

    private int maxSize=1;

    public void setMaxSize(int maxSize){
        this.maxSize=maxSize;
    }

    public void setDeleteEnable(boolean isDeleteEnable){
        this.isDeleteEnable=isDeleteEnable;
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public SizePicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLiLayoutInflater.inflate(R.layout.item_linear, parent, false));
    }

    @Override
    public void onBindViewHolder(final SizePicAdapter.ViewHolder holder, final int position) {
        if (datas.size() < maxSize && position == datas.size()) {
            holder.img.setImageResource(R.mipmap.btn_tianjia);
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.img.setImageURI(Uri.fromFile(new File(datas.get(position))));
            if(isDeleteEnable){
                holder.delete.setVisibility(View.VISIBLE);
            }else {
                holder.delete.setVisibility(View.GONE);
            }
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteClickListener != null) {
                        mDeleteClickListener.delete(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (datas.size() >= maxSize) {
            return maxSize;
        } else {
            return datas == null ? 0 : datas.size() + 1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView img;
        ImageView delete;
        LinearLayout ll_item;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
        }
    }

    DeleteClickListener mDeleteClickListener;

    public void setDeleteClickListener(DeleteClickListener mDeleteClickListener) {
        this.mDeleteClickListener = mDeleteClickListener;
    }

    public interface DeleteClickListener {
        void delete(int num);
    }
}
