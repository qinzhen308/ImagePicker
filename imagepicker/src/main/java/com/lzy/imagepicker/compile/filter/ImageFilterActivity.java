/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.lzy.imagepicker.compile.filter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lzy.imagepicker.R;


/**
 * 滤镜
 *
 * @author jarlen
 */
public class ImageFilterActivity extends Activity implements View.OnClickListener {
    private ImageView pictureShow;
    private String picturePath = null;
    private Bitmap pictureBitmap = null;

    private ImageButton backBtn, okBtn;

    private int scale = 2;

    private ImageView filterImg;
    private VerticalSeekBar mVerticalSeekBar;
    private TextView mSeekBarProgress;

    private NativeFilter nativeFilters = new NativeFilter();
    private int updateTimeTip = 5;

    private int srcWidth, srcHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_filter);

        initializateView();
        findDate();
    }

    private void initializateView() {
        pictureShow = (ImageView) findViewById(R.id.pictureShow);
        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.verticalSeekBar);
        mSeekBarProgress = (TextView) findViewById(R.id.verticalSeekBarProgressText);

        backBtn = (ImageButton) findViewById(R.id.btn_cancel);
        backBtn.setTag(1);
        backBtn.setOnClickListener(this);

        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        okBtn.setTag(2);

        mVerticalSeekBar.setMax(100);

        mVerticalSeekBar
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    int mProgress = 0;

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mSeekBarProgress.setText(mProgress + "%");

                        float degree = mProgress / 100.0f;

                        updatePicture(degree);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        mProgress = progress;

                    }
                });

        initFiltersView();
    }

    Bitmap newBitmap;

    private void findDate() {
        Intent filterIntent = getIntent();

        picturePath = filterIntent.getStringExtra("camera_path");
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 1;
        pictureBitmap = BitmapFactory.decodeFile(picturePath, option);

        newBitmap = pictureBitmap;
        srcWidth = pictureBitmap.getWidth();
        srcHeight = pictureBitmap.getHeight();

        Log.i("jarlen", "srcWidth = " + srcWidth + " srcHeight = " + srcHeight);

        pictureShow.setImageBitmap(newBitmap);
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();
        switch (tag) {
            case 1:
                Intent cancelData = new Intent();
                setResult(RESULT_CANCELED, cancelData);
                recycle();
                this.finish();

                break;
            case 2:

                FileUtils.writeImage(resultImg, picturePath, 100);
                Intent intent = new Intent();
                intent.putExtra("camera_path", picturePath);
                setResult(Activity.RESULT_OK, intent);
                recycle();
                this.finish();
                break;

            default:
                break;
        }

    }

    private int filterType = FilterType.FILTER4GRAY;
    Bitmap resultImg = null;

    private void updatePicture(float degree) {
        int[] dataResult = null;

        int[] pix = new int[srcWidth * srcHeight];
        newBitmap.getPixels(pix, 0, srcWidth, 0, 0, srcWidth, srcHeight);

        switch (filterType) {
            case FilterType.FILTER4GRAY:

                dataResult = nativeFilters.gray(pix, srcWidth, srcHeight,
                        degree);

                break;
            case FilterType.FILTER4MOSATIC:

                int mosatic = (int) (degree * 30);
                dataResult = nativeFilters.mosatic(pix, srcWidth, srcHeight,
                        mosatic);
                break;

            case FilterType.FILTER4LOMO:

                dataResult = nativeFilters.lomo(pix, srcWidth, srcHeight,
                        degree);
                break;
            case FilterType.FILTER4NOSTALGIC:

                dataResult = nativeFilters.nostalgic(pix, srcWidth,
                        srcHeight, degree);
                break;
            case FilterType.FILTER4COMICS:
                dataResult = nativeFilters.comics(pix, srcWidth, srcHeight,
                        degree);
                break;
            case FilterType.FILTER4BlackWhite:
/*                dataResult = nativeFilters.ToBlackWhite(pix, srcWidth,
                        srcHeight, degree);*/
                break;

            case FilterType.FILTER4NEGATIVE:
//                dataResult = nativeFilters.negative(pix, srcWidth, srcHeight,
//                        degree);
                break;
            case FilterType.FILTER4BROWN:
                dataResult = nativeFilters.brown(pix, srcWidth, srcHeight,
                        degree);
                break;

            case FilterType.FILTER4SKETCH_PENCIL:
                dataResult = nativeFilters.sketchPencil(pix, srcWidth,
                        srcHeight, degree);
                break;

            case FilterType.FILTER4OVEREXPOSURE:
//                dataResult = nativeFilters.ToOverExposure(pix, srcWidth,
//                        srcHeight, degree);
                break;
            case FilterType.FILTER4WHITELOG:
//                dataResult = nativeFilters.ToWhiteLOG(pix, srcWidth, srcHeight,
//                        FilterType.BeitaOfWhiteLOG, degree);
                break;

            // //

            case FilterType.FILTER4SOFTNESS:
//                dataResult = nativeFilters.ToSoftness(pix, srcWidth, srcHeight,
//                        degree);
                break;

            case FilterType.FILTER4NiHong:
//                dataResult = nativeFilters.ToNiHong(pix, srcWidth, srcHeight,
//                        degree);
                break;

            case FilterType.FILTER4SKETCH:
//                dataResult = nativeFilters.ToSketch(pix, srcWidth, srcHeight,
//                        degree);
                break;
            // case FilterType.FILTER4CARVING :
            // dataResult = nativeFilters.ToCarving(pix, w, h, degree);
            // break;
            case FilterType.FILTER4RELIEF:
//                dataResult = nativeFilters.ToRelief(pix, srcWidth, srcHeight,
//                        degree);
                break;
            // case FilterType.FILTER4RUIHUA :
            // dataResult = nativeFilters.ToRuiHua(pix, w, h, degree);
            // break;

            default:
                break;
        }

        resultImg = Bitmap.createBitmap(dataResult, srcWidth, srcHeight,
                Bitmap.Config.ARGB_8888);

        // FilterType filterType = new FilterType();
        // Bitmap resultImg = filterType.createPencli(newBitmap);

        pictureShow.setImageBitmap(resultImg);

    }

    private TextView filterWhite, filterGray, filterMosatic, filterLOMO,
            filterNostalgic, filterComics, filterBlackWhite, filterNegative,
            filterBrown, filterSketchPencil, filterOverExposure,
            filterSoftness, filterNiHong, filterSketch, filterCarving,
            filterSelief, filterRuiHua;

    private void initFiltersView() {
//        filterWhite = (TextView) findViewById(R.id.filterWhite);
//        filterWhite.setOnClickListener(filterOnClickListener);
        //  filterWhite.setTag(1);

        filterGray = (TextView) findViewById(R.id.filterGray);
        filterGray.setOnClickListener(filterOnClickListener);
        filterGray.setTag(2);

        filterMosatic = (TextView) findViewById(R.id.filterMosatic);
        filterMosatic.setOnClickListener(filterOnClickListener);
        filterMosatic.setTag(3);

        filterLOMO = (TextView) findViewById(R.id.filterLOMO);
        filterLOMO.setOnClickListener(filterOnClickListener);
        filterLOMO.setTag(4);

        filterNostalgic = (TextView) findViewById(R.id.filterNostalgic);
        filterNostalgic.setOnClickListener(filterOnClickListener);
        filterNostalgic.setTag(5);

        filterComics = (TextView) findViewById(R.id.filterComics);
        filterComics.setOnClickListener(filterOnClickListener);
        filterComics.setTag(6);

        filterBlackWhite = (TextView) findViewById(R.id.filterBlackWhite);
        filterBlackWhite.setOnClickListener(filterOnClickListener);
        filterBlackWhite.setTag(7);

        filterNegative = (TextView) findViewById(R.id.filterNegative);
        filterNegative.setOnClickListener(filterOnClickListener);
        filterNegative.setTag(8);

        filterBrown = (TextView) findViewById(R.id.filterBrown);
        filterBrown.setOnClickListener(filterOnClickListener);
        filterBrown.setTag(9);

        filterSketchPencil = (TextView) findViewById(R.id.filterSketchPencil);
        filterSketchPencil.setOnClickListener(filterOnClickListener);
        filterSketchPencil.setTag(10);

        filterOverExposure = (TextView) findViewById(R.id.filterOverExposure);
        filterOverExposure.setOnClickListener(filterOnClickListener);
        filterOverExposure.setTag(11);

        // ////

        filterSoftness = (TextView) findViewById(R.id.filterSoftness);
        filterSoftness.setOnClickListener(filterOnClickListener);
        filterSoftness.setTag(12);

        filterNiHong = (TextView) findViewById(R.id.filterNiHong);
        filterNiHong.setOnClickListener(filterOnClickListener);
        filterNiHong.setTag(13);

        filterSketch = (TextView) findViewById(R.id.filterSketch);
        filterSketch.setOnClickListener(filterOnClickListener);
        filterSketch.setTag(14);

        // filterCarving = (TextView) findViewById(R.id.filterCarving);
        // filterCarving.setOnClickListener(new FilterOnClickListener());
        //filterCarving.setTag(15);

        // filterSelief = (TextView) findViewById(R.id.filterSelief);
        // filterSelief.setOnClickListener(new FilterOnClickListener());
        // filterSelief.setTag(16);

        // filterRuiHua = (TextView) findViewById(R.id.filterRuiHua);
        // filterRuiHua.setOnClickListener(new FilterOnClickListener());
        //filterRuiHua.setTag(17);
    }

    private FilterOnClickListener filterOnClickListener = new FilterOnClickListener();

    private class FilterOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            // Log.i("jarlen", " view.getId = " + view.getId());
            int num= (int) view.getTag();
            switch (num) {
                case 1:
                    filterType = FilterType.FILTER4WHITELOG;
                    break;
                case 2:
                    filterType = FilterType.FILTER4GRAY;
                    break;
                case 3:
                    filterType = FilterType.FILTER4BlackWhite;
                    break;
                case 4:
                    filterType = FilterType.FILTER4MOSATIC;
                    break;
                case 5:
                    filterType = FilterType.FILTER4COMICS;
                    break;
                case 6:
                    filterType = FilterType.FILTER4BROWN;
                    break;
                case 7:
                    filterType = FilterType.FILTER4LOMO;
                    break;
                case 8:
                    filterType = FilterType.FILTER4NEGATIVE;
                    break;
                case 9:
                    filterType = FilterType.FILTER4NOSTALGIC;
                    break;
                case 10:
                    filterType = FilterType.FILTER4OVEREXPOSURE;
                    break;
                case 11:
                    filterType = FilterType.FILTER4SKETCH_PENCIL;
                    break;

                // +6 by jarlen 2014/11/9
                case 12:
                    filterType = FilterType.FILTER4SOFTNESS;
                    break;
                case 13:
                    filterType = FilterType.FILTER4NiHong;
                    break;
                case 14:
                    filterType = FilterType.FILTER4SKETCH;
                    break;
                // case 15 :
                // filterType = FilterType.FILTER4CARVING;
                // break;
                // case 16 :
                // filterType = FilterType.FILTER4RELIEF;
                // break;
                // case 17 :
                // filterType = FilterType.FILTER4RUIHUA;
                // break;

                default:
                    break;
            }

            updatePicture(1);
            mVerticalSeekBar.setProgress(100);
            mSeekBarProgress.setText(100 + "%");
            mVerticalSeekBar.setProgressAndThumb(mVerticalSeekBar.getMax());

        }

    }

    private void recycle() {
        if (pictureBitmap != null) {
            pictureBitmap.recycle();
            pictureBitmap = null;
        }

        if (newBitmap != null) {
            newBitmap.recycle();
            newBitmap = null;
        }

    }

}
