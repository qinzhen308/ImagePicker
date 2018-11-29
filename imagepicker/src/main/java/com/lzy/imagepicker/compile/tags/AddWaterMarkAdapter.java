package com.lzy.imagepicker.compile.tags;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.imagepicker.R;
import com.lzy.imagepicker.compile.tags.bean.AddWaterMarkBean;

import java.util.List;

/**
 * Created by why8222 on 2016/3/17.
 */
public class AddWaterMarkAdapter extends RecyclerView.Adapter<AddWaterMarkAdapter.FilterHolder> {

    private List<AddWaterMarkBean> dataList;
    private Context context;
    private int selected = 0;

    public AddWaterMarkAdapter(Context context, List<AddWaterMarkBean> List) {
        this.dataList = List;
        this.context = context;
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.addwater_item_layout,
                parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.thumbImage = (ImageView) view
                .findViewById(R.id.filter_thumb_image);
        viewHolder.filterName = (TextView) view
                .findViewById(R.id.filter_thumb_name);
        viewHolder.filterRoot = (FrameLayout) view
                .findViewById(R.id.filter_root);
        viewHolder.thumbSelected = (FrameLayout) view
                .findViewById(R.id.filter_thumb_selected);
        viewHolder.thumbSelected_bg = view.
                findViewById(R.id.filter_thumb_selected_bg);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, final int position) {
        holder.thumbImage.setImageResource(dataList.get(position).icon);
        holder.filterName.setText(dataList.get(position).name);
        //holder.filterName.setBackgroundColor(context.getResources().getColor(FilterTypeHelper.FilterType2Color(filters[position])));
        if (dataList.get(position).isSelect) {
            holder.thumbSelected.setVisibility(View.VISIBLE);
            holder.thumbSelected_bg.setBackgroundColor(context.getResources().getColor(com.lzy.imagepicker.R.color.black));
            holder.thumbSelected_bg.setAlpha(0.3f);
        } else {
            holder.thumbSelected.setVisibility(View.GONE);
        }

        holder.filterRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int lastSelected = selected;
                selected = position;
                onFilterChangeListener.onFilterChanged(dataList.get(position));
                for (int i = 0; i < dataList.size(); i++) {
                    if (i==position){
                        dataList.get(i).isSelect=true;
                    }else {
                        dataList.get(i).isSelect=false;
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
        return dataList == null ? 0 : dataList.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;
        TextView filterName;
        FrameLayout thumbSelected;
        FrameLayout filterRoot;
        View thumbSelected_bg;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onFilterChangeListener {
        void onFilterChanged(AddWaterMarkBean addWaterMarkBean);
    }

    private onFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener) {
        this.onFilterChangeListener = onFilterChangeListener;
    }

}
