package com.lzy.imagepicker.compile.tailor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.imagepicker.R;

import java.util.List;

/**
 * Created by why8222 on 2016/3/17.
 */
public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.FilterHolder> {

    private List<TailorMode> data;
    private Context context;
    private int selected = 0;

    public SelectAdapter(Context context, List<TailorMode> filters) {
        this.data = filters;
        this.context = context;
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_image_tailor_item,
                parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.name = (TextView) view.findViewById(R.id.name);
        viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
        viewHolder.layout = (LinearLayout) view.findViewById(R.id.layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, final int position) {
        holder.name.setText(data.get(position).name);
        holder.icon.setVisibility(View.VISIBLE);
        if (data.get(position).selectimg) {
            holder.icon.setImageResource(data.get(position).imgb);
            holder.name.setTextColor(context.getResources().getColor(R.color.select_textyellow));
        } else {
            holder.icon.setImageResource(data.get(position).img);
            holder.name.setTextColor(context.getResources().getColor(R.color.white));
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTailorModeChangeListener.onTailorModeChanged(data.get(position));
                for (int i=0;i<data.size();i++){
                   if (data.get(i).id==data.get(position).id){
                       data.get(i).selectimg=true;
                   }else {
                       data.get(i).selectimg=false;
                   }
                }
                notifyDataSetChanged();
            }
        });
    }

    public void refreshList() {
        selected = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        LinearLayout layout;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onTailorModeChangeListener {
        void onTailorModeChanged(TailorMode tailorMode);
    }

    private onTailorModeChangeListener onTailorModeChangeListener;

    public void setOnTailorModeChangeListener(onTailorModeChangeListener onTailorModeChangeListener) {
        this.onTailorModeChangeListener = onTailorModeChangeListener;
    }
}
