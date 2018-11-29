package com.zero.magicshow.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zero.magicshow.R;
import com.zero.magicshow.adapter.FilterAdapter;
import com.zero.magicshow.common.base.BaseActivity;
import com.zero.magicshow.common.base.MagicBaseView;
import com.zero.magicshow.common.config.PathConfig;
import com.zero.magicshow.common.entity.MagicShowResultEntity;
import com.zero.magicshow.common.utils.BaseUtil;
import com.zero.magicshow.common.utils.Constants;
import com.zero.magicshow.common.utils.RxBus;
import com.zero.magicshow.common.utils.SavePictureTask;
import com.zero.magicshow.core.MagicEngine;
import com.zero.magicshow.core.filter.utils.MagicFilterType;
import com.zero.magicshow.core.widget.MagicImageView;
import com.zero.magicshow.view.edit.ImageEditFragment;
import com.zero.magicshow.view.edit.filter.ImageEditFilterView;
import com.zero.magicshow.view.edit.iface.ImageEditNavListener;
import com.zero.zerolib.util.AnimationUtils;
import com.zero.zerolib.util.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rx.functions.Action1;

/**
 * Created by zero on 2016/3/18.
 */
public class AlbumActivity extends BaseActivity {
    private ImageView btnBack, btnNext, btnModifyClose, btnModifySave;
    private TextView tvTitle;
    private MagicImageView magicImageView;
    private LinearLayout blockNavigation;
    private View blockModifyController, blockTopBar;
    private ProgressDialog progressDialog = null;

    private HashMap<String, Fragment> fragmentHashMap = new HashMap<>();
    public static final String IMAGE_EDIT_TYPE_BEAUTY = "beauty";//美颜
    public static final String IMAGE_EDIT_TYPE_ADJUST = "adjust";//编辑
    public static final String IMAGE_EDIT_TYPE_FILTER = "filter";//滤镜
    private Context context;
    private MagicEngine magicEngine;
    private String imageUrl;

    private RecyclerView recyclerView;
    private FilterAdapter filterAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_image);
        PathConfig.setTempCache(getCacheDir().getAbsolutePath() + "/ImagePicker/cropTemp");
        //PathConfig.setTempCache("/sdcard/DCIM");
        RxBus.getInstance().unregisterMain(Constants.RX_JAVA_TYPE_CAMERA_DISPOSE);
        RxBus.getInstance().registerMain(Constants.RX_JAVA_TYPE_CAMERA_DISPOSE, new Action1<MagicShowResultEntity>() {
            @Override
            public void call(MagicShowResultEntity magicShowResultEntity) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                doNextAction();
                RxBus.getInstance().unregisterMain(Constants.RX_JAVA_TYPE_CAMERA_SHOOT);
            }
        });
        init();
    }

    private void init() {
        initView();
        initData();
        initListener();
        blockTopBar.setVisibility(View.VISIBLE);
    }

    private void initView() {
        btnBack = (ImageView) findViewById(R.id.image_edit_back);
        btnNext = (ImageView) findViewById(R.id.image_edit_save);
        tvTitle = (TextView) findViewById(R.id.image_edit_title);
        magicImageView = (MagicImageView) findViewById(R.id.image_edit_magicimageview);
        blockNavigation = (LinearLayout) findViewById(R.id.image_edit_navigation);
        blockModifyController = findViewById(R.id.image_edit_modify_controller_block);
        blockTopBar = findViewById(R.id.image_edit_topbar);
        btnModifyClose = (ImageView) findViewById(R.id.image_edit_modify_controller_close);
        btnModifySave = (ImageView) findViewById(R.id.image_edit_modify_controller_save);
        recyclerView = (RecyclerView) findViewById(R.id.image_edit_filter_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        filterAdapter = new FilterAdapter(this, Constants.FILTER_TYPES);
        recyclerView.setAdapter(filterAdapter);
    }

    private void initData() {
        imageUrl = getIntent().getStringExtra(Constants.TRANSMIT_IMAGE_URL);
//        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = new MagicEngine.Builder().build(magicImageView);
        initFragments();
        magicImageView.setGLScaleType(MagicBaseView.ScaleType.CENTER_INSIDE);
        if (StringUtil.isEmpty(imageUrl)) {
            magicImageView.setImageBitmap(BaseUtil.getImageFromAssetsFile(context, "dark.jpg"));
        } else {
            magicImageView.setImageBitmap(BaseUtil.getBitmapBySimpleSize(imageUrl, BaseUtil.dipToPix(context, 640), BaseUtil.dipToPix(context, 640)));
        }
    }

    private void initListener() {
        btnBack.setOnClickListener(onClickListener);
        btnNext.setOnClickListener(onClickListener);
        btnModifyClose.setOnClickListener(onClickListener);
        btnModifySave.setOnClickListener(onClickListener);
        filterAdapter.setOnFilterChangeListener(onFilterChangeListener);
    }

    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener() {

        @Override
        public void onFilterChanged(MagicFilterType filterType) {
//            magicEngine.setFilter(filterType);
            MagicEngine.getInstance().setFilter(filterType);
        }
    };


    private void initFragments() {
        // ImageEditManager.initAdjustView(context,fragmentHashMap,blockNavigation,onHideListener,imageEditNavListener);
        //ImageEditManager.initBeautyView(context,fragmentHashMap,blockNavigation,onHideListener,imageEditNavListener);
        //ImageEditManager.initFilterView(context,fragmentHashMap,blockNavigation,onHideListener,imageEditNavListener);
        ImageEditFilterView filterView = new ImageEditFilterView();
        filterView.setOnHideListener(onHideListener);
        fragmentHashMap.put(AlbumActivity.IMAGE_EDIT_TYPE_FILTER, filterView);
        showFragment(fragmentHashMap.get("filter"));
        blockTopBar.setVisibility(View.VISIBLE);
        blockModifyController.setVisibility(View.GONE);
    }

    @Override
    protected void doFinishAction() {
        Fragment showFragment = getShowFragment();
        if (null != showFragment) {
            ((ImageEditFragment) showFragment).doFinishAction();
            return;
        }
        super.doFinishAction();
    }

    private Fragment getShowFragment() {
        Iterator<Map.Entry<String, Fragment>> iterator = fragmentHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Fragment> item = iterator.next();
            if (item.getValue().isVisible()) {
                return item.getValue();
            }
        }
        return null;
    }

    private void doClickModifyCloseAction() {
        Fragment showFragment = getShowFragment();
        if (null != showFragment) {
            ((ImageEditFragment) showFragment).doFinishAction();
        }
    }

    private void doClickModifySaveAction() {
        Fragment showFragment = getShowFragment();
        if (null != showFragment) {
            ((ImageEditFragment) showFragment).doSaveConfigeAction();
        }
    }

    private void doBackAction() {
        doFinishAction();
    }

    private void doNextAction() {
        MagicEngine.getInstance().savePicture(BaseUtil.getRandomTempImageFile(), new SavePictureTask.OnPictureSaveListener() {
            @Override
            public void onSaved(String result) {
                MagicShowResultEntity magicShowResultEntity = new MagicShowResultEntity();
                magicShowResultEntity.setFilePath(result);
                RxBus.getInstance().post(magicShowResultEntity, Constants.RX_JAVA_TYPE_IMAGE_EDIT);
                doFinishAction();
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnModifyClose) {
                doClickModifyCloseAction();
            } else if (v == btnModifySave) {
                doClickModifySaveAction();
            } else if (v == btnBack) {
                doBackAction();
            } else if (v == btnNext) {
                doClickModifySaveAction();
                // 开启图片和处理线程
                ImageThread thread = new ImageThread();
                thread.start();
            }
        }
    };

    private ImageEditFragment.onHideListener onHideListener = new ImageEditFragment.onHideListener() {

        @Override
        public void onAfterHide() {
            Log.e("HongLi", "in onAfterHide");
            Fragment showFragment = getShowFragment();
            if (null != showFragment) {
                hiddenFragment(showFragment);
            }
        }
    };

    private ImageEditNavListener imageEditNavListener = new ImageEditNavListener() {
        @Override
        public void onClick(View view, String type) {
            showFragment(fragmentHashMap.get(type));
        }
    };

    private void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.bottom_sliding_in, R.anim.bottom_sliding_out);
        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.image_edit_fragment_container, fragment).show(fragment).commit();
        } else {
            fragmentTransaction.show(fragment).commit();
        }
        blockModifyController.setVisibility(View.GONE);
        blockTopBar.setVisibility(View.VISIBLE);
        AnimationUtils.doSlidingInFromBottom(blockModifyController, blockModifyController.getHeight(), false);
        //com.zero.magicshow.common.utils.BaseUtil.fadeOutView(blockTopBar);
    }

    private void hiddenFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.bottom_sliding_in, R.anim.bottom_sliding_out);
        fragmentTransaction.hide(fragment).commit();
        AnimationUtils.doSlidingOutFromBottom(blockModifyController, blockModifyController.getHeight(), false, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                blockModifyController.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        // com.zero.magicshow.common.utils.BaseUtil.fadeInView(blockTopBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregisterMain(Constants.RX_JAVA_TYPE_IMAGE_EDIT);
        RxBus.getInstance().unregisterMain(Constants.RX_JAVA_TYPE_CAMERA_DISPOSE);
    }

    private class ImageThread extends Thread {
        @SuppressWarnings("deprecation")
        public void run() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            handler.sendEmptyMessage(1);
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    progressDialog = ProgressDialog.show(AlbumActivity.this, "保存图片", "处理中..");
                    break;
            }
        }
    };
}
