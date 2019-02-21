package com.meili.moon.imagepicker.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.ScanImageSource;
import com.meili.moon.imagepicker.adapter.FolderListAdapter;
import com.meili.moon.imagepicker.adapter.ImageListAdapter;
import com.meili.moon.imagepicker.ibean.impl.ImageBean;
import com.meili.moon.imagepicker.ibean.impl.ImageFolderBean;
import com.meili.moon.imagepicker.util.ImagePickerUtil;

import java.util.ArrayList;
import java.util.List;

import static com.meili.moon.imagepicker.util.ConstantsUtil.CODE_REQUEST_LOCAL_PHOTO;
import static com.meili.moon.imagepicker.util.ConstantsUtil.CODE_RESULT_IMG_PREVIEW_LIST;
import static com.meili.moon.imagepicker.util.ConstantsUtil.CODE_RESULT_LOCAL_PHOTO;
import static com.meili.moon.imagepicker.util.ConstantsUtil.LOCATION_DEAL_URL;

public class MLImageListActivity extends ImageBaseActivity implements ScanImageSource.OnImageLoadedListener {
    private final static String TAG = MLImageListActivity.class.getSimpleName();

    private final static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 101;

    protected TextView toolbarCenterTitle;
    protected TextView toolbarRightTitle;
    private RecyclerView rvImageList;

    private List<ImageBean> imageBeanList = new ArrayList<>();
    private ImageListAdapter imageListAdapter;

    private LinearLayout layoutImageFileList;
    private TextView tvImageFileList;
    private LinearLayout layoutImagePreview;
    private TextView tvImagePreview;

    private RelativeLayout layoutFolderList;
    private View tvFolderList;
    private RelativeLayout layoutRvFolderList;
    private RecyclerView rvFolderList;
    private List<ImageFolderBean> imageFolderBeanList = new ArrayList<>();
    private FolderListAdapter folderListAdapter;

    private RelativeLayout layoutImageListBottom;

    private int rvFolderHeight = 0;

    private int folderPosition = 0;

    //预览图片页面请求
    public final static int CODE_REQUEST_IMG_PREVIEW_LIST = 3000;
    //多选图片列表返回
    public final static String RESULT_IMG_LIST = "result_img_list";

    private ScanImageSource mScanImageSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        requestMediaPermission();
        initView();
        initData();
    }

    private void requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            } else {
                operateData();
            }
        } else {
            operateData();
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.img_picker_name));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.go_back_left_arrow);

        layoutImageListBottom = findViewById(R.id.layoutImageListBottom);

        layoutImageListBottom.setBackgroundColor(MNImagePicker.getInstance().getPickerCommonConfig().getToolbarColor());

        toolbarCenterTitle = findViewById(R.id.toolbarCenterTitle);
        toolbarRightTitle = findViewById(R.id.toolbarRightTitle);

        toolbarRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currSize = MNImagePicker.getInstance().getImageList().size();
                if (currSize > 0) {
                    if (MNImagePicker.getInstance().imageListChangeListener != null) {
                        MNImagePicker.getInstance().imageListChangeListener.onImageListChangeListener(MNImagePicker.getInstance().getImageList());
                        MNImagePicker.getInstance().imageListChangeListener = null;
                    }

                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MNImagePicker.getInstance().getImageList());
                    setResult(RESULT_OK, intent);
                    ImagePickerUtil.clearConfig();
                    finish();
                } else {
                    Toast.makeText(MLImageListActivity.this, getString(R.string.picture_no_choose), Toast.LENGTH_SHORT).show();
                }


            }
        });

        layoutImageFileList = findViewById(R.id.layoutImageFileList);
        tvImageFileList = findViewById(R.id.tvImageFileList);
        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        tvImagePreview = findViewById(R.id.tvImagePreview);

        layoutFolderList = findViewById(R.id.layoutFolderList);
        layoutFolderList.setVisibility(View.INVISIBLE);

        layoutFolderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFolderList();
            }
        });

        tvFolderList = findViewById(R.id.tvFolderList);
        rvFolderList = findViewById(R.id.rvFolderList);
        layoutRvFolderList = findViewById(R.id.layoutRvFolderList);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, (int) dip2px(this, 120), 0, 0);
        layoutRvFolderList.setLayoutParams(layoutParams);
        layoutFolderList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rvFolderHeight = layoutFolderList.getHeight();
                if (rvFolderHeight > 0) {
                    layoutFolderList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        rvImageList = findViewById(R.id.rvImageList);


        if (MNImagePicker.getInstance().getChooseType().isSingleType()) {
            toolbarRightTitle.setVisibility(View.GONE);
            layoutImagePreview.setVisibility(View.GONE);
        } else {
            toolbarRightTitle.setVisibility(View.VISIBLE);
            layoutImagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePickerUtil.clearConfig();
                finish();
            }
        });

        layoutImageFileList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutFolderList.isShown()) {
                    closeFolderList();
                } else {
                    openFolderList();
                }
            }
        });

        layoutImagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17/11/16 打开图片预览列表
                int currSize = MNImagePicker.getInstance().getImageList().size();
                if (currSize > 0) {
                    Intent intent = new Intent(MLImageListActivity.this, MLImagePreviewActivity.class);
                    intent.putParcelableArrayListExtra(RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MNImagePicker.getInstance().getImageList());
                    intent.putExtra("position", 0);
                    startActivityForResult(intent, CODE_REQUEST_IMG_PREVIEW_LIST);
                } else {
                    Toast.makeText(MLImageListActivity.this, getString(R.string.picture_no_choose), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageListAdapter = new ImageListAdapter(this, imageBeanList);
        rvImageList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        rvImageList.setAdapter(imageListAdapter);
        imageListAdapter.setOnChooseImgChangeListener(new ImageListAdapter.OnChooseImgChangeListener() {
            @Override
            public void onChooseImgChangeListener() {
                int currSize = MNImagePicker.getInstance().getImageList().size();
                if (currSize > 0) {
                    int configMaxSize = MNImagePicker.getInstance().getConfig().getPhotoMaxSize();
                    if (configMaxSize > 0) {
                        toolbarRightTitle.setText(getString(R.string.send) + "（" + currSize + "/" + configMaxSize + "）");
                    } else {
                        int commonMaxSize = MNImagePicker.getInstance().getPickerCommonConfig().getPhotoMaxSize();
                        if (commonMaxSize > 0) {
                            toolbarRightTitle.setText(getString(R.string.send) + "（" + currSize + "/" + commonMaxSize + "）");
                        } else {
                            toolbarRightTitle.setText(getString(R.string.send) + "（" + currSize + "）");
                        }
                    }
                    tvImagePreview.setText("（" + currSize + "）" + getString(R.string.preview));
                } else {
                    toolbarRightTitle.setText(getString(R.string.send));
                    tvImagePreview.setText(getString(R.string.preview));
                }
            }
        });

        folderListAdapter = new FolderListAdapter(this, imageFolderBeanList);
        rvFolderList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        rvFolderList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvFolderList.setAdapter(folderListAdapter);
        folderListAdapter.setOnItemClickListener(new FolderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ImageFolderBean imageFolderBean, int position) {
                if (imageFolderBean != null) {
                    tvImageFileList.setText(imageFolderBean.getFolderName());

                    folderPosition = position;

                    for (int i = 0; i < imageFolderBeanList.size(); i++) {
                        if (i == position) {
                            imageFolderBeanList.get(i).setChecked(true);
                        } else {
                            imageFolderBeanList.get(i).setChecked(false);
                        }
                    }
                    folderListAdapter.notifyDataSetChanged();


                    imageListAdapter.refreshData((List<ImageBean>) imageFolderBean.getImageBeanList());
                    closeFolderList();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    operateData();
                } else {
                    Toast.makeText(MLImageListActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ImagePickerUtil.clearConfig();
                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (float) (dipValue * scale + 0.5f);
    }

    private void openFolderList() {
        layoutFolderList.setVisibility(View.VISIBLE);

        ObjectAnimator moveTopAnimator = ObjectAnimator.ofFloat(rvFolderList, "translationY", dip2px(MLImageListActivity.this, rvFolderHeight + 165), 0);
        moveTopAnimator.setDuration(400);
        moveTopAnimator.start();

        tvFolderList.setBackgroundColor(Color.parseColor("#000000"));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 0.8f);
        tvFolderList.startAnimation(alphaAnimation);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(400);
    }

    private void closeFolderList() {
        ObjectAnimator moveTopAnimator = ObjectAnimator.ofFloat(rvFolderList, "translationY", 0, dip2px(MLImageListActivity.this, rvFolderHeight + 165));
        moveTopAnimator.setDuration(400);
        moveTopAnimator.start();

        tvFolderList.setBackgroundColor(Color.parseColor("#000000"));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.8f, 0f);
        tvFolderList.startAnimation(alphaAnimation);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(400);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutFolderList.setVisibility(View.GONE);
            }
        }, 400);
    }

    private void operateData() {
        mScanImageSource = new ScanImageSource(this, null, this);
    }

    // TODO: 17/11/17 初始加载数据只进入一次
    @Override
    public void onImageLoaded(List<ImageFolderBean> imageFolderBeanList) {
        if (imageFolderBeanList.size() == 0) {
            // TODO: 17/11/16 文件夹为0
        } else {
            this.imageFolderBeanList = imageFolderBeanList;
            rvFolderList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
            rvFolderList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            //fixme 记录被打开文件夹
            imageFolderBeanList.get(folderPosition).setChecked(true);
            folderListAdapter.refreshData(imageFolderBeanList);
            Log.d(TAG, "imageFolderBeanListSize:" + imageFolderBeanList.size());
            Log.d(TAG, "imageFolderBeanList:" + imageFolderBeanList.get(0).toString());
            rvImageList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
            imageListAdapter.refreshData((List<ImageBean>) imageFolderBeanList.get(folderPosition).getImageBeanList());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageListAdapter.notifyDataSetChanged();
        int currSize = MNImagePicker.getInstance().getImageList().size();
        if (currSize > 0) {
            int configMaxSize = MNImagePicker.getInstance().getConfig().getPhotoMaxSize();
            if (configMaxSize > 0) {
                toolbarRightTitle.setText(getString(R.string.send) + "（" + currSize + "/" + configMaxSize + "）");
            } else {
                int commonMaxSize = MNImagePicker.getInstance().getPickerCommonConfig().getPhotoMaxSize();
                if (commonMaxSize > 0) {
                    toolbarRightTitle.setText(getString(R.string.send) + "（" + currSize + "/" + commonMaxSize + "）");
                } else {
                    toolbarRightTitle.setText(getString(R.string.send) + "（" + currSize + "）");
                }
            }
            tvImagePreview.setText("（" + currSize + "）" + getString(R.string.preview));
        } else {
            toolbarRightTitle.setText(getString(R.string.send));
            tvImagePreview.setText(getString(R.string.preview));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        //返回清空选中结果
        ImagePickerUtil.clearConfig();
        MNImagePicker.getInstance().getImageList().clear();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_REQUEST_IMG_PREVIEW_LIST) {
            if (resultCode == CODE_RESULT_IMG_PREVIEW_LIST) {
                if (MNImagePicker.getInstance().imageListChangeListener != null) {
                    MNImagePicker.getInstance().imageListChangeListener.onImageListChangeListener(MNImagePicker.getInstance().getImageList());
                    MNImagePicker.getInstance().imageListChangeListener = null;
                }

                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MNImagePicker.getInstance().getImageList());
                setResult(RESULT_OK, intent);
                ImagePickerUtil.clearConfig();
                finish();
            }
        } else if (requestCode == CODE_REQUEST_LOCAL_PHOTO) {
            if (resultCode == CODE_RESULT_LOCAL_PHOTO) {
                Intent intent = new Intent();
                intent.putExtra(LOCATION_DEAL_URL, data.getStringExtra(LOCATION_DEAL_URL));
                setResult(CODE_RESULT_LOCAL_PHOTO, intent);
                ImagePickerUtil.clearConfig();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImagePickerUtil.clearConfig();
        MNImagePicker.getInstance().getImageList().clear();

        MNImagePicker.getInstance().getImageLoadFrame().clearMemoryCache(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing() && mScanImageSource != null) {
            mScanImageSource.recycle();
            mScanImageSource = null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
