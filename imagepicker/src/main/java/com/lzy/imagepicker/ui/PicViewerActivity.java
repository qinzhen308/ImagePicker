package com.lzy.imagepicker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.adapter.ImagePageAdapter;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.compile.mosaics.ProMosaic;
import com.lzy.imagepicker.compile.tags.AddWatermarkActivity;
import com.lzy.imagepicker.compile.tailor.ImageTailorActivity;
import com.lzy.imagepicker.view.ViewPagerFixed;
import com.zero.magicshow.MagicShowManager;
import com.zero.magicshow.common.entity.MagicShowResultEntity;
import com.zero.magicshow.common.iface.ImageEditCallBack;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/15.
 * 查看图片
 */

public class PicViewerActivity extends ImageBaseActivity {


    private static final int IMAGE_PICKER1 = 1001;                   //马赛克图片
    private static final int IMAGE_PICKER2 = 1002;                   //裁剪图片
    private static final int IMAGE_PICKER3 = 1003;                   //滤镜图片
    private static final int IMAGE_PICKER4 = 1004;                   //贴纸图片
    private static final int IMAGE_PICKER5 = 1005;                   //贴字图片

    private ViewPagerFixed viewPager;
    private TextView tv_indicator;
    private ArrayList<ImageItem> urlList = new ArrayList<>();
    protected ImagePageAdapter mAdapter;

    private LinearLayout mTailorlay;//裁剪
    private LinearLayout mFilterlay;//滤镜
    private LinearLayout mMosaiclay;//马赛克
    private LinearLayout mTagslay;//贴纸
    private int mPosition = 0;

    private TextView mCancel;//取消
    private TextView mConfirm;//确定

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_viewer);
        ArrayList<ImageItem> images = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
        urlList.clear();
        urlList.addAll(images);

        viewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        tv_indicator = (TextView) findViewById(R.id.tv_indicator);
        mMosaiclay = (LinearLayout) findViewById(R.id.mosaiclay);

        mTagslay = (LinearLayout) findViewById(R.id.tagslay);
        mTailorlay = (LinearLayout) findViewById(R.id.tailorlay);
        mFilterlay = (LinearLayout) findViewById(R.id.filterlay);
        //屏蔽滤镜
        mFilterlay.setVisibility(View.GONE);
        mCancel = (TextView) findViewById(R.id.cancel);
        mConfirm = (TextView) findViewById(R.id.confirm);

        mAdapter = new ImagePageAdapter(this, urlList);
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
            }
        });
        viewPager.setAdapter(mAdapter);
        //viewPager.setCurrentItem(0, false);
        tv_indicator.setText(String.valueOf(1) + "/" + urlList.size());
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                tv_indicator.setText(String.valueOf(position + 1) + "/" + urlList.size());
            }
        });
        setOncli();
    }

    public void setOncli() {
        //马赛克
        mMosaiclay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ProMosaic.class);
                intent.putExtra("path", urlList.get(mPosition).path);
                startActivityForResult(intent, IMAGE_PICKER1);
            }
        });
        //裁剪
        mTailorlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将图片路径photoPath传到所要调试的模块
                Intent photoFrameIntent = new Intent(PicViewerActivity.this, ImageTailorActivity.class);
                photoFrameIntent.putExtra("camera_path", urlList.get(mPosition).path);
                startActivityForResult(photoFrameIntent, IMAGE_PICKER2);
            }
        });
        //贴纸
        mTagslay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将图片路径photoPath传到所要调试的模块
                Intent photoFrameIntent = new Intent(PicViewerActivity.this, AddWatermarkActivity.class);
                photoFrameIntent.putExtra("camera_path", urlList.get(mPosition).path);
                startActivityForResult(photoFrameIntent, IMAGE_PICKER4);
            }
        });
        //滤镜
        mFilterlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MagicShowManager.getInstance().openEdit(PicViewerActivity.this, urlList.get(mPosition).path, new ImageEditCallBack() {
                    @Override
                    public void onCompentFinished(MagicShowResultEntity magicShowResultEntity) {
                        Log.e("HongLi", "获取图片地址:" + magicShowResultEntity.getFilePath());
                        urlList.get(mPosition).path = magicShowResultEntity.getFilePath();
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
        //取消
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //确定
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, urlList);
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER1) {
                String path = data.getStringExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                urlList.get(mPosition).path = path;
                mAdapter.notifyDataSetChanged();
            }
            if (data != null && requestCode == IMAGE_PICKER2) {
                String cropIndex = (String) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (cropIndex.equals("3") || cropIndex.equals("6") || cropIndex.equals("8")) {
                    ArrayList<ImageItem> imageItems = (ArrayList<ImageItem>) data
                            .getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    urlList.get(mPosition).path = imageItems.get(0).path;
                    mAdapter.notifyDataSetChanged();
                } else {
                    String path = data.getStringExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    urlList.get(mPosition).path = path;
                    mAdapter.notifyDataSetChanged();
                }

            }
            if (data != null && requestCode == IMAGE_PICKER3) {
            }
            if (data != null && requestCode == IMAGE_PICKER4) {
                String path = data.getStringExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                urlList.get(mPosition).path = path;
                mAdapter.notifyDataSetChanged();
            }
            if (data != null && requestCode == IMAGE_PICKER5) {
            }

        }
    }

}
