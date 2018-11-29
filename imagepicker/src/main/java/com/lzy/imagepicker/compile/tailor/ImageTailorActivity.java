package com.lzy.imagepicker.compile.tailor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageBaseActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImageTailorActivity extends ImageBaseActivity implements View.OnClickListener, CropImageView.OnBitmapSaveCompleteListener {

    private CropImageView mCropImageView;
    private Bitmap mBitmap;
    private boolean mIsSaveRectangle;
    private int mOutputX;
    private int mOutputY;
    private ArrayList<ImageItem> mImageItems;
    private ImagePicker imagePicker;
    private RecyclerView recyclerView;
    List<TailorMode> datalist = new ArrayList<>();

    private ImageView buttonBack;//返回
    private ImageView buttonConfirm;//确定


    public void setData(int id, String name, int img, int imgb, boolean selectimg) {
        TailorMode tailorMode = new TailorMode();
        tailorMode.id = id;
        tailorMode.name = name;
        tailorMode.img = img;
        tailorMode.imgb = imgb;
        tailorMode.selectimg = selectimg;
        datalist.add(tailorMode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_tailor);
        String imagePath = getIntent().getStringExtra("camera_path");
        imagePicker = ImagePicker.getInstance();
        //获取需要的参数
        mOutputX = imagePicker.getOutPutX();
        mOutputY = imagePicker.getOutPutY();
        mIsSaveRectangle = imagePicker.isSaveRectangle();
        mImageItems = imagePicker.getSelectedImages();
        //初始化View
        mCropImageView = (CropImageView) findViewById(R.id.cv_crop_image);
        mCropImageView.setOnBitmapSaveCompleteListener(this);
        mCropImageView.setFocusStyle(imagePicker.getStyle());
        mCropImageView.setFocusWidth(imagePicker.getFocusWidth());
        mCropImageView.setFocusHeight(imagePicker.getFocusHeight());
        if (imagePicker.getCropIndex() == 1) {
            setData(2, "1:1", R.drawable.to1, R.drawable.to1b, true);
            getwh(4, 4);
        } else if (imagePicker.getCropIndex() == 3) {
            setData(3, "2:1", R.drawable.to1, R.drawable.to1b, true);
            getwh(8, 4);
        } else if (imagePicker.getCropIndex() == 6) {
            setData(6, "16:9", R.drawable.to1, R.drawable.to1b, true);
            getwh(16, 9);
        } else if (imagePicker.getCropIndex() == 8) {
            setData(8, "13:9", R.drawable.to1, R.drawable.to1b, true);
            getwh(13, 9);
        } else if (imagePicker.getCropIndex() == 9) {
            setData(9, "750:1334", R.drawable.to1, R.drawable.to1b, true);
            getwh(17, 10);
        } else {
            setData(1, "原始", R.drawable.yuanshi, R.drawable.yuanshib, true);
            setData(2, "1:1", R.drawable.to1, R.drawable.to1b, false);
            setData(3, "2:1", R.drawable.to1, R.drawable.to1b, false);
            setData(4, "4:3", R.drawable.to3, R.drawable.to3b, false);
            setData(5, "3:4", R.drawable.to4, R.drawable.to4b, false);
            setData(6, "16:9", R.drawable.to9, R.drawable.to9b, false);
            setData(7, "9:16", R.drawable.to16, R.drawable.to16b, false);
            setData(8, "13:9", R.drawable.to16, R.drawable.to16b, false);
            setData(9, "750:1334", R.drawable.to1, R.drawable.to1b, true);
        }

        buttonBack = (ImageView) findViewById(R.id.button_back);
        buttonConfirm = (ImageView) findViewById(R.id.button_confirm);
        buttonBack.setOnClickListener(this);
        buttonConfirm.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ImageTailorActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        SelectAdapter selectAdapter = new SelectAdapter(ImageTailorActivity.this, datalist);
        selectAdapter.setOnTailorModeChangeListener(new SelectAdapter.onTailorModeChangeListener() {
            @Override
            public void onTailorModeChanged(TailorMode tailorMode) {
                switch (tailorMode.id) {
                    case 1:
                        mCropImageView.setFocusWidHeight(750, 750);
                        break;
                    case 2:
                        getwh(4, 4);
                        break;
                    case 3:
                        getwh(8, 4);
                        break;
                    case 4:
                        getwh(8, 6);
                        break;
                    case 5:
                        getwh(6, 8);
                        break;
                    case 6:
                        getwh(16, 9);
                        break;
                    case 7:
                        getwh(9, 16);
                        break;
                    case 8:
                        getwh(13, 9);
                        break;
                    case 9:
                        getwh(17, 10);
                        break;
                }

            }
        });
        recyclerView.setAdapter(selectAdapter);
        //缩放图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeFile(imagePath, options);
        mCropImageView.setImageBitmap(mBitmap);

//        mCropImageView.setImageURI(Uri.fromFile(new File(imagePath)));
    }

    public void getwh(int ratio, int hratio) {
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        int widths = width / 10 * ratio;
        int heights = width / 10 * hratio;
        mCropImageView.setFocusWidHeight(widths, heights);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.button_confirm) {
            mCropImageView.saveBitmapToFile(imagePicker.getCropCacheFolder(this), mOutputX, mOutputY, mIsSaveRectangle);
        }
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
//        Toast.makeText(ImageCropActivity.this, "裁剪成功:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        //裁剪后替换掉返回数据的内容，但是不要改变全局中的选中数据
        // mImageItems.remove(0);
        mImageItems.clear();
        ImageItem imageItem = new ImageItem();
        imageItem.path = file.getAbsolutePath();
        mImageItems.add(imageItem);
        if (imagePicker.getCropIndex() == 1 || imagePicker.getCropIndex() == 3 || imagePicker.getCropIndex() == 6 || imagePicker.getCropIndex() == 8 || imagePicker.getCropIndex() == 9) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mImageItems);
            intent.putExtra(ImagePicker.EXTRA_CROP_INDEX, imagePicker.getCropIndex());
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, file.getAbsolutePath());
            intent.putExtra(ImagePicker.EXTRA_CROP_INDEX, imagePicker.getCropIndex());
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
            finish();
        }
    }

    @Override
    public void onBitmapSaveError(File file) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBitmap && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
