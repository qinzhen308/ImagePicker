package com.lzy.imagepicker.compile.tags;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.compile.tags.bean.AddWaterMarkBean;
import com.lzy.imagepicker.compile.tags.operate.ImageObject;
import com.lzy.imagepicker.compile.tags.operate.OperateUtils;
import com.lzy.imagepicker.compile.tags.operate.OperateView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 添加水印
 *
 * @author jarlen
 */
public class AddWatermarkActivity extends Activity implements View.OnClickListener {

    private LinearLayout content_layout;
    private OperateView operateView;
    private String camera_path;
    private String mPath = null;
    OperateUtils operateUtils;
    private RecyclerView recyclerView;

    int watermark[] = {R.drawable.watermarkchunvzuo, R.drawable.comment,
            R.drawable.gouda, R.drawable.guaishushu, R.drawable.haoxingzuop,
            R.drawable.wanhuaile};

    String watermarkname[] = {"处女座", "神回复", "求勾搭", "怪蜀黍", "好星座", "玩坏了"};

    private ImageView buttonBack;//返回
    private ImageView buttonConfirm;//确定

    List<AddWaterMarkBean> mDataList = new ArrayList<>();
    AddWaterMarkAdapter mAddWaterMarkAdapter = null;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addwatermark);

        for (int i = 0; i < watermark.length; i++) {
            AddWaterMarkBean addWaterMarkBean = new AddWaterMarkBean();
            addWaterMarkBean.name = watermarkname[i];
            addWaterMarkBean.icon = watermark[i];
            mDataList.add(addWaterMarkBean);
        }
        Intent intent = getIntent();
        camera_path = intent.getStringExtra("camera_path");
        operateUtils = new OperateUtils(this);
        initView();
        // 延迟每次延迟10 毫秒 隔1秒执行一次
        timer.schedule(task, 10, 1000);
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (content_layout.getWidth() != 0) {
                    Log.i("LinearLayoutW", content_layout.getWidth() + "");
                    Log.i("LinearLayoutH", content_layout.getHeight() + "");
                    // 取消定时器
                    timer.cancel();
                    fillContent();
                }
            }
        }
    };

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void initView() {
        content_layout = (LinearLayout) findViewById(R.id.mainLayout);


        buttonBack = (ImageView) findViewById(R.id.button_back);
        buttonConfirm = (ImageView) findViewById(R.id.button_confirm);
        buttonBack.setOnClickListener(this);
        buttonConfirm.setOnClickListener(this);
        buttonConfirm.setTag(13);
        buttonBack.setTag(14);

        recyclerView = (RecyclerView) findViewById(R.id.image_edit_filter_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAddWaterMarkAdapter = new AddWaterMarkAdapter(AddWatermarkActivity.this, mDataList);
        mAddWaterMarkAdapter.setOnFilterChangeListener(new AddWaterMarkAdapter.onFilterChangeListener() {
            @Override
            public void onFilterChanged(AddWaterMarkBean addWaterMarkBean) {
                addpic(addWaterMarkBean.icon);
            }
        });
        recyclerView.setAdapter(mAddWaterMarkAdapter);

    }

    private void fillContent() {
        Bitmap resizeBmp = BitmapFactory.decodeFile(camera_path);
        operateView = new OperateView(AddWatermarkActivity.this, resizeBmp);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                resizeBmp.getWidth(), resizeBmp.getHeight());
        operateView.setLayoutParams(layoutParams);
        content_layout.addView(operateView);
        operateView.setMultiAdd(true); // 设置此参数，可以添加多个图片
    }

    private void btnSave() {
        operateView.save();
        Bitmap bmp = getBitmapByView(operateView);

        if (bmp != null) {
            mPath = saveBitmap(bmp, getCacheDir() + "/ImagePicker/cropTemp/");
            Intent okData = new Intent();
            okData.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mPath);
            setResult(ImagePicker.RESULT_CODE_ITEMS, okData);
            this.finish();
        }
    }

    // 将模板View的图片转化为Bitmap
    public Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    // 将生成的图片保存到内存中
    public String saveBitmap(Bitmap bitmap, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(name);
            if (!dir.exists())
                dir.mkdirs();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
            String filename = name + dateFormat.format(new Date(System.currentTimeMillis())) + ".jpg";

            File file = new File(filename);
            FileOutputStream out;

            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void addpic(int position) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), position);
        // ImageObject imgObject = operateUtils.getImageObject(bmp);
        ImageObject imgObject = operateUtils.getImageObject(bmp, operateView,
                5, 150, 100);
        operateView.addItem(imgObject);
    }

/*    int watermark[] = {R.drawable.watermarkchunvzuo, R.drawable.comment,
            R.drawable.gouda, R.drawable.guaishushu, R.drawable.haoxingzuop,
            R.drawable.wanhuaile, R.drawable.xiangsi, R.drawable.xingzuokong,
            R.drawable.xinnian, R.drawable.zaoan, R.drawable.zuile,
            R.drawable.zuo,R.drawable.zui};*/

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();
        switch (tag) {
          /*  case 1:
                addpic(watermark[0]);
                break;
            case 2:
                addpic(watermark[1]);
                break;
*//*            case R.id.qiugouda :
                addpic(watermark[2]);
                break;*//*
            case 3:
                addpic(watermark[3]);
                break;
            case 4:
                addpic(watermark[4]);
                break;
            case 5:
                addpic(watermark[5]);
                break;
            case 6:
                addpic(watermark[6]);
                break;
            case 7:
                addpic(watermark[7]);
                break;
            case 8:
                addpic(watermark[8]);
                break;
            case 9:
                addpic(watermark[9]);
                break;
            case 10:
                addpic(watermark[10]);
                break;
            case 11:
                addpic(watermark[11]);
                break;
            case 12:
                addpic(watermark[12]);
                break;*/
            case 13:
                btnSave();
                break;
            case 14:
                finish();
                break;
            default:
                break;
        }

    }
}
