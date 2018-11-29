package com.lzy.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.lzy.imagepicker.bean.ImageFolder;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：图片选择的入口类
 * 修订历史：
 * ================================================
 */
public class ImagePicker {

    public static final String TAG = ImagePicker.class
            .getSimpleName();
    public static final int REQUEST_CODE_TAKE = 1001;
    public static final int REQUEST_CODE_CROP = 1002;
    public static final int REQUEST_CODE_PREVIEW = 1003;
    public static final int RESULT_CODE_ITEMS = 1004;
    public static final int RESULT_CODE_BACK = 1005;
    public static final int RESULT_CODE_COMPILE = 1006;

    public static final String EXTRA_RESULT_ITEMS = "extra_result_items";
    public static final String EXTRA_CROP_INDEX = "extra_crop_index";
    public static final String EXTRA_SELECTED_IMAGE_POSITION = "selected_image_position";
    public static final String EXTRA_IMAGE_ITEMS = "extra_image_items";

    private boolean multiMode = true;                          //图片选择模式
    private int selectLimit = 9;                             //最大选择图片数量
    private boolean crop = true;                          //裁剪
    private boolean showCamera = true;                          //显示相机
    private boolean isSaveRectangle = false;                         //裁剪后的图片是否是矩形，否者跟随裁剪框的形状
    private int outPutX = 800;                           //裁剪保存宽度
    private int outPutY = 800;                           //裁剪保存高度
    private int focusWidth = 280;                           //焦点框的宽度
    private int focusHeight = 280;                           //焦点框的高度
    private ImageLoader imageLoader;                                                   //图片加载器
    private CropImageView.Style style = CropImageView.Style.RECTANGLE; //裁剪框的形状
    private File cropCacheFolder;
    private File takeImageFile;
    public Bitmap cropBitmap;

    private ArrayList<ImageItem> mSelectedImages = new ArrayList<>();             //选中的图片集合
    private List<ImageFolder> mImageFolders;                                                 //所有的图片文件夹
    private int mCurrentImageFolderPosition = 0;                             //当前选中的文件夹位置 0表示所有图片
    private List<OnImageSelectedListener> mImageSelectedListeners;                                       // 图片选中的监听回调

    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/kcwc/Camera/");

    private static ImagePicker mInstance;

    public int getCropIndex() {
        return cropIndex;
    }
    public void setCropIndex(int cropIndex) {
        this.cropIndex = cropIndex;
    }
    //设置具体的裁剪比例
    private int cropIndex = -1;

    private ImagePicker() {
    }

    public static ImagePicker getInstance() {
        if (mInstance == null) {
            synchronized (ImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new ImagePicker();
                }
            }
        }
        return mInstance;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    public int getSelectLimit() {
        return selectLimit;
    }

    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    public boolean isCrop() {
        return crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public boolean isSaveRectangle() {
        return isSaveRectangle;
    }

    public void setSaveRectangle(boolean isSaveRectangle) {
        this.isSaveRectangle = isSaveRectangle;
    }

    public int getOutPutX() {
        return outPutX;
    }

    public void setOutPutX(int outPutX) {
        this.outPutX = outPutX;
    }

    public int getOutPutY() {
        return outPutY;
    }

    public void setOutPutY(int outPutY) {
        this.outPutY = outPutY;
    }

    public int getFocusWidth() {
        return focusWidth;
    }

    public void setFocusWidth(int focusWidth) {
        this.focusWidth = focusWidth;
    }

    public int getFocusHeight() {
        return focusHeight;
    }

    public void setFocusHeight(int focusHeight) {
        this.focusHeight = focusHeight;
    }

    public File getTakeImageFile() {
        return takeImageFile;
    }

    public File getCropCacheFolder(Context context) {
        if (cropCacheFolder == null) {
            cropCacheFolder = new File(context.getCacheDir() + "/ImagePicker/cropTemp/");
        }
        return cropCacheFolder;
    }

    public void setCropCacheFolder(File cropCacheFolder) {
        this.cropCacheFolder = cropCacheFolder;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public CropImageView.Style getStyle() {
        return style;
    }

    public void setStyle(CropImageView.Style style) {
        this.style = style;
    }

    public List<ImageFolder> getImageFolders() {
        return mImageFolders;
    }

    public void setImageFolders(List<ImageFolder> imageFolders) {
        mImageFolders = imageFolders;
    }

    public int getCurrentImageFolderPosition() {
        return mCurrentImageFolderPosition;
    }

    public void setCurrentImageFolderPosition(int mCurrentSelectedImageSetPosition) {
        mCurrentImageFolderPosition = mCurrentSelectedImageSetPosition;
    }

    public ArrayList<ImageItem> getCurrentImageFolderItems() {
        ArrayList<ImageItem> images = mImageFolders.get(mCurrentImageFolderPosition).images;
        if (getSelectImageCount() > 0) {
            for (ImageItem item : mSelectedImages) {

                for (ImageItem data : images) {
                    if (data.path.equals(item.path)) {
                        data.SelectBoolean = item.SelectBoolean;
                        data.SelectNumber = item.SelectNumber;
                        break;
                    }
                }

            }
        }
        return images;
    }

    public boolean isSelect(ImageItem item) {
        return mSelectedImages.contains(item);
    }

    public ImageItem getSelectItem(String path) {
        for (ImageItem item : mSelectedImages) {
            if (item.path.equals(path)) {
                return item;
            }
        }
        return null;
    }

    public int getSelectImageCount() {
        if (mSelectedImages == null) {
            return 0;
        }
        return mSelectedImages.size();
    }

    public ArrayList<ImageItem> getSelectedImages() {
        return mSelectedImages;
    }

    public void clearSelectedImages() {
        if (mSelectedImages != null)
            mSelectedImages.clear();
    }

    public void clear() {
        if (mImageSelectedListeners != null) {
            mImageSelectedListeners.clear();
            mImageSelectedListeners = null;
        }
        if (mImageFolders != null) {
            mImageFolders.clear();
            mImageFolders = null;
        }
        if (mSelectedImages != null) {
            mSelectedImages.clear();
        }
        mCurrentImageFolderPosition = 0;
    }

    /**
     * 拍照的方法
     */
    public void takePicture(Activity activity, int requestCode) {
        //        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
        //            if (Utils.existSDCard())
        //                takeImageFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
        //            else takeImageFile = Environment.getDataDirectory();
        //            takeImageFile = createFile(takeImageFile, "IMG_", ".jpg");
        //            if (takeImageFile != null) {
        //                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
        //                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
        //                // 如果没有指定uri，则data就返回有数据！
        //                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takeImageFile));
        //            }
        //        }

        try {
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            // 给新照的照片文件命名
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {

                if (Utils.existSDCard()) {
                    takeImageFile = createFile(PHOTO_DIR, "IMG_", ".jpg");
                } else {
                    takeImageFile = createFile(Environment.getDataDirectory(), "IMG_", "jpg");
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //                intent.addFlags(
                    //                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Uri photoURI = FileProvider.getUriForFile(activity,
                            "com.tgf.kcwc.fileprovider", takeImageFile);

                    /* 这句要记得写：这是申请权限，之前因为没有添加这个，打开裁剪页面时，一直提示“无法修改低于50*50像素的图片”，
                     开始还以为是图片的问题呢，结果发现是因为没有添加FLAG_GRANT_READ_URI_PERMISSION。
                     如果关联了源码，点开FileProvider的getUriForFile()看看（下面有），注释就写着需要添加权限。
                     */
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    // intent.setDataAndType(photoURI, "image/*");
                } else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takeImageFile));
                }
                activity.startActivityForResult(intent, requestCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory())
            folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }


    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String name) {
        if (!folder.exists() || !folder.isDirectory())
            folder.mkdirs();
        return new File(folder, name);
    }

    /**
     * 扫描图片
     */
    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 图片选中的监听
     */
    public interface OnImageSelectedListener {
        void onImageSelected(int position, ImageItem item, boolean isAdd);
    }

    public void addOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null)
            mImageSelectedListeners = new ArrayList<>();
        mImageSelectedListeners.add(l);
    }

    public void removeOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null)
            return;
        mImageSelectedListeners.remove(l);
    }

    public void addSelectedImageItem(int position, ImageItem item, boolean isAdd) {
        if (isAdd) {
            item.SelectBoolean = true;
            item.SelectNumber = mSelectedImages.size() + 1;
            item.position = position;
            mSelectedImages.add(item);
        } else {
            item.SelectBoolean = false;
            item.SelectNumber = -1;
            item.position = -1;
            mSelectedImages.remove(item);
            for (int i = 0; i < mSelectedImages.size(); i++) {
                mSelectedImages.get(i).SelectNumber = i + 1;
            }
        }
        notifyImageSelectedChanged(position, item, isAdd);
    }

    private void notifyImageSelectedChanged(int position, ImageItem item, boolean isAdd) {
        if (mImageSelectedListeners == null)
            return;
        for (OnImageSelectedListener l : mImageSelectedListeners) {
            l.onImageSelected(position, item, isAdd);
        }
    }
}