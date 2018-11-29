package com.lzy.imagepicker.compile.mosaics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.compile.mosaics.view.MosaicView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ProMosaic extends Activity implements OnClickListener {
    public static final String TAG = "ProMosaic";
    private static final int REQ_PICK_IMAGE = 1984;

    private MosaicView mvImage;
    private ImageView diamonds;//方格效果
    private ImageView groundglass;//毛玻璃效果
    private ImageView atpresent;//选择的效果

    private ImageView buttonOne;//画笔大小1
    private ImageView buttonTwo;//画笔大小2
    private ImageView buttonThree;//画笔大小3
    private ImageView buttonFour;//画笔大小4

    List<ImageView> ImageViewData = new ArrayList<>();
    private ImageView buttonRevocation;//撤回
    private ImageView buttonBack;//返回
    private ImageView buttonConfirm;//确定

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pro_mosaic);

        String path = getIntent().getStringExtra("path");
        mvImage = (MosaicView) findViewById(R.id.iv_content);

        diamonds = (ImageView) findViewById(R.id.diamonds);
        groundglass = (ImageView) findViewById(R.id.groundglass);
        atpresent = (ImageView) findViewById(R.id.atpresent);

        buttonOne = (ImageView) findViewById(R.id.button_one);
        buttonTwo = (ImageView) findViewById(R.id.button_two);
        buttonThree = (ImageView) findViewById(R.id.button_three);
        buttonFour = (ImageView) findViewById(R.id.button_four);

        buttonOne.setTag(1);
        buttonTwo.setTag(2);
        buttonThree.setTag(3);
        buttonFour.setTag(4);
        ImageViewData.clear();
        ImageViewData.add(buttonOne);
        ImageViewData.add(buttonTwo);
        ImageViewData.add(buttonThree);
        ImageViewData.add(buttonFour);

        buttonRevocation = (ImageView) findViewById(R.id.button_revocation);
        buttonBack = (ImageView) findViewById(R.id.button_back);
        buttonConfirm = (ImageView) findViewById(R.id.button_confirm);

        diamonds.setOnClickListener(this);
        groundglass.setOnClickListener(this);
        buttonOne.setOnClickListener(this);
        buttonTwo.setOnClickListener(this);
        buttonThree.setOnClickListener(this);
        buttonFour.setOnClickListener(this);
        buttonRevocation.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        buttonConfirm.setOnClickListener(this);

        if (!TextUtils.isEmpty(path)) {
            mvImage.setSrcPath(path); //设置图片地址
            mvImage.setEffect(MosaicView.Effect.GRID);//方格效果
            // mvImage.setEffect(MosaicView.Effect.BLUR); //毛玻璃效果

            //纯色
     /*       mvImage.setMosaicColor(0xFF4D4D4D);
            mvImage.setEffect(MosaicView.Effect.COLOR);*/

            mvImage.setMode(MosaicView.Mode.PATH); //手指滑动
            //  mvImage.setMode(MosaicView.Mode.GRID); //矩形
            /*----清空数据开始涂鸦-----*/
            mvImage.clear();
            mvImage.setErase(false);
            //mvImage.setErase(true); //设置橡皮差
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(diamonds)) {
            mvImage.setEffect(MosaicView.Effect.GRID);//方格效果
            atpresent.setImageResource(R.drawable.button_mosaicfang);
        } else if (v.equals(groundglass)) {
            atpresent.setImageResource(R.drawable.button_mosaic);
            mvImage.setEffect(MosaicView.Effect.BLUR); //毛玻璃效果
        } else if (v.equals(buttonOne)) {
            mvImage.setmPathWidth(5);
            change(1);
        } else if (v.equals(buttonTwo)) {
            mvImage.setmPathWidth(10);
            change(2);
        } else if (v.equals(buttonThree)) {
            mvImage.setmPathWidth(15);
            change(3);
        } else if (v.equals(buttonFour)) {
            mvImage.setmPathWidth(20);
            change(4);
        } else if (v.equals(buttonRevocation)) {
            mvImage.clear();
            mvImage.setErase(false);
        } else if (v.equals(buttonBack)) {
            finish();
        } else if (v.equals(buttonConfirm)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
            String filename = dateFormat.format(new Date(System.currentTimeMillis()));
            boolean succced = mvImage.save(ProMosaic.this.getCacheDir() + "/ImagePicker/cropTemp/" + filename + ".jpg");
            if (succced) {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, ProMosaic.this.getCacheDir() + "/ImagePicker/cropTemp/" + filename + ".jpg");
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                finish();
            }
        }
    }

    public void change(int num) {

        for (ImageView data : ImageViewData) {
            int sedata = (int) data.getTag();
            if (sedata == num) {
                data.setImageResource(R.drawable.xinhao2b);
            } else {
                data.setImageResource(R.drawable.xinghao1a);
            }

        }

    }
}
