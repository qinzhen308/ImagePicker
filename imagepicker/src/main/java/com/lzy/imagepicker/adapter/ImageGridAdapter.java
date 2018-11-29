package com.lzy.imagepicker.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.Utils;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageBaseActivity;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.SuperCheckBox;
import com.lzy.imagepicker.view.TipView;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机

    private ImagePicker imagePicker;
    private Activity mActivity;
    private ArrayList<ImageItem> images;       //当前需要显示的所有的图片数据
    private ArrayList<ImageItem> mSelectedImages; //全局保存的已经选中的图片数据
    private boolean isShowCamera;         //是否显示拍照按钮
    private int mImageSize;               //每个条目的大小
    private OnImageItemClickListener listener;   //图片被点击的监听

    public ImageGridAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;

        mImageSize = Utils.getImageItemWidth(mActivity);
        imagePicker = ImagePicker.getInstance();
        isShowCamera = imagePicker.isShowCamera();
        mSelectedImages = imagePicker.getSelectedImages();
    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return isShowCamera ? images.size() + 1 : images.size();
    }

    @Override
    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) return null;
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE_CAMERA) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.adapter_camera_item, parent, false);
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
            convertView.setTag(null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((ImageBaseActivity) mActivity).checkPermission(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                    } else {
                        imagePicker.takePicture(mActivity, ImagePicker.REQUEST_CODE_TAKE);
                    }
                }
            });
        } else {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.adapter_image_list_item, parent, false);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ImageItem imageItem = getItem(position);

            holder.ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageItemClick(holder.rootView, imageItem, position);
                }
            });
/*            holder.cbCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectLimit = imagePicker.getSelectLimit();
                    if (holder.cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                        Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                        holder.cbCheck.setChecked(false);
                        holder.mask.setVisibility(View.GONE);
                    } else {
                        imagePicker.addSelectedImageItem(position, imageItem, holder.cbCheck.isChecked());
                        holder.mask.setVisibility(View.VISIBLE);
                    }
                }
            });*/

            holder.none.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectLimit = imagePicker.getSelectLimit();
                    if (mSelectedImages.size() >= selectLimit) {
                        // Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                        setHint();
                        // holder.cbCheck.setChecked(false);
                        holder.mask.setVisibility(View.GONE);
                    } else {
                        imagePicker.addSelectedImageItem(position, imageItem, true);
                        holder.mask.setVisibility(View.VISIBLE);
                    }
                }
            });
            holder.alreadyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagePicker.addSelectedImageItem(position, imageItem, false);
                    holder.cbCheck.setChecked(false);
                    holder.mask.setVisibility(View.GONE);
                }
            });

            //根据是否多选，显示或隐藏checkbox
            if (imagePicker.isMultiMode()) {
                holder.cbCheck.setVisibility(View.GONE);
                holder.none.setVisibility(View.VISIBLE);
                holder.alreadyLayout.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImages.contains(imageItem);
                if (checked) {
                    ImageItem selectedImagesItem = getSelectedImagesItem(imageItem.path);
                    imageItem.SelectBoolean = selectedImagesItem.SelectBoolean;
                    imageItem.SelectNumber = selectedImagesItem.SelectNumber;
                    holder.mask.setVisibility(View.VISIBLE);
                    holder.cbCheck.setChecked(true);
                    holder.none.setVisibility(View.GONE);
                    holder.alreadyLayout.setVisibility(View.VISIBLE);
                    if (imageItem.SelectNumber != -1) {
                        holder.already.setText(imageItem.SelectNumber);
                    } else {
                        holder.already.setText(0);
                    }
                } else {
                    holder.mask.setVisibility(View.GONE);
                    holder.cbCheck.setChecked(false);
                    holder.none.setVisibility(View.VISIBLE);
                    holder.alreadyLayout.setVisibility(View.GONE);
                }
            } else {
                holder.cbCheck.setVisibility(View.GONE);
                holder.none.setVisibility(View.GONE);
                holder.alreadyLayout.setVisibility(View.GONE);
            }
            imagePicker.getImageLoader().displayImage(mActivity, imageItem.path, holder.ivThumb, mImageSize, mImageSize); //显示图片
        }
        return convertView;
    }

    public ImageItem getSelectedImagesItem(String path) {
        for (ImageItem data : mSelectedImages) {
            if (data.path.equals(path)) {
                return data;
            }
        }
        return null;
    }

    private class ViewHolder {
        public View rootView;
        public ImageView ivThumb;
        public View mask;
        public SuperCheckBox cbCheck;
        public LinearLayout alreadyLayout;
        public ImageView none;
        public TipView already;

        public ViewHolder(View view) {
            rootView = view;
            ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            none = (ImageView) view.findViewById(R.id.weixuan);
            already = (TipView) view.findViewById(R.id.xuanzhe);
            mask = view.findViewById(R.id.mask);
            cbCheck = (SuperCheckBox) view.findViewById(R.id.cb_check);
            alreadyLayout = (LinearLayout) view.findViewById(R.id.xuanzhelayout);
        }
    }

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, ImageItem imageItem, int position);
    }

    public void setHint() {
        String string = mActivity.getString(R.string.select_limit, imagePicker.getSelectLimit());
        View hinttext = View.inflate(mActivity, R.layout.dialog_hint, null);
        TextView hint = (TextView) hinttext.findViewById(R.id.title);
        hint.setText(string);
        final AlertDialog alertDialog = new AlertDialog.Builder(mActivity, R.style.dialog).setView(hinttext).show();
        WindowManager.LayoutParams wmParams = alertDialog.getWindow().getAttributes();
        //wmParams.format = PixelFormat.TRANSPARENT;  //内容全透明
        wmParams.format = PixelFormat.TRANSLUCENT;  //内容半透明
        wmParams.alpha = 0.7f;   // 调节透明度，1.0最大
        alertDialog.getWindow().setAttributes(wmParams);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        }, 1000);

    }
}