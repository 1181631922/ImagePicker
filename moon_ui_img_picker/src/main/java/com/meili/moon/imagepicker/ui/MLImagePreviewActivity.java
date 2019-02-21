package com.meili.moon.imagepicker.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.adapter.PreviewListAdapter;
import com.meili.moon.imagepicker.ibean.IImageBean;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.view.photoview.HackyViewPager;
import com.meili.moon.imagepicker.view.photoview.PhotoView;

import java.util.ArrayList;

import static com.meili.moon.imagepicker.ui.MLImageListActivity.RESULT_IMG_LIST;
import static com.meili.moon.imagepicker.util.ConstantsUtil.CODE_RESULT_IMG_PREVIEW_LIST;
import static com.meili.moon.imagepicker.util.ConstantsUtil.PREVIEW_ONLY;

public class MLImagePreviewActivity extends ImageBaseActivity {
    private final static String TAG = MLImagePreviewActivity.class.getSimpleName();

    private Toolbar toolbar;
    protected TextView toolbarCenterTitle;
    protected TextView toolbarRightTitle;
    //
    private HackyViewPager vpPreview;

    private RecyclerView rvPreviewList;
    private PreviewListAdapter previewListAdapter;

    private LinearLayout layoutImagePreview;
    private CheckBox cbPreview;

    private RelativeLayout layoutPreviewBottom;

    private int currPosition = 0;

    private int screenWidth;
    private int screenHeight;

    private ArrayList<IImageBean> imageList = new ArrayList<>();

    private boolean isOnlyPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        imageList = getIntent().getParcelableArrayListExtra(RESULT_IMG_LIST);
        currPosition = getIntent().getIntExtra("position", 0);

        isOnlyPreview = getIntent().getBooleanExtra(PREVIEW_ONLY, false);

        screenHeight = getScreenHeight(this);
        screenWidth = getScreenWidth(this);

        initView();
        initData();


    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        if (imageList.size() > 0) {
            toolbar.setTitle((currPosition + 1) + "/" + imageList.size());
        } else {
            toolbar.setTitle(getString(R.string.img_picker_name));
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.go_back_left_arrow);

        layoutPreviewBottom = findViewById(R.id.layoutPreviewBottom);
        layoutPreviewBottom.setBackgroundColor(MNImagePicker.getInstance().getPickerCommonConfig().getToolbarColor());
        toolbarCenterTitle = findViewById(R.id.toolbarCenterTitle);
        toolbarRightTitle = findViewById(R.id.toolbarRightTitle);
        toolbarRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MNImagePicker.getInstance().getImageList().size() > 0) {
                    setResult(CODE_RESULT_IMG_PREVIEW_LIST);
                    finish();
                } else {
                    Toast.makeText(MLImagePreviewActivity.this, getString(R.string.picture_no_choose), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (MNImagePicker.getInstance().getImageList().size() > 0) {
            int configMaxSize = MNImagePicker.getInstance().getConfig().getPhotoMaxSize();
            int currSize = MNImagePicker.getInstance().getImageList().size();
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
        } else {
            toolbarRightTitle.setText(getString(R.string.send));
        }

        vpPreview = findViewById(R.id.vpPreview);
        rvPreviewList = findViewById(R.id.rvPreviewList);

        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        layoutImagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cbPreview.isChecked()){
                    int configMaxSize = MNImagePicker.getInstance().getConfig().getPhotoMaxSize();
                    int currSize = MNImagePicker.getInstance().getImageList().size();
                    if (configMaxSize > 0) {
                        if (currSize>=configMaxSize){
                            Toast.makeText(MLImagePreviewActivity.this, getString(R.string.picture_max_limit) + configMaxSize + getString(R.string.picture_unit), Toast.LENGTH_SHORT).show();
                            cbPreview.setChecked(false);
                            return;
                        }
                    } else {
                        int commonMaxSize = MNImagePicker.getInstance().getPickerCommonConfig().getPhotoMaxSize();
                        if (commonMaxSize > 0) {
                            if (currSize>=commonMaxSize){
                                Toast.makeText(MLImagePreviewActivity.this, getString(R.string.picture_max_limit) + commonMaxSize + getString(R.string.picture_unit), Toast.LENGTH_SHORT).show();
                                cbPreview.setChecked(false);
                                return;
                            }
                        }
                    }
                }
                if (cbPreview.isChecked()) {
                    MNImagePicker.getInstance().getImageList().remove(imageList.get(currPosition));
                    cbPreview.setChecked(false);
                } else {
                    MNImagePicker.getInstance().getImageList().add(imageList.get(currPosition));
                    cbPreview.setChecked(true);
                }
                Log.e(TAG, "current imgUrl:" + imageList.get(currPosition).getImgPath());

                if (MNImagePicker.getInstance().getImageList().size() > 0) {
                    int configMaxSize = MNImagePicker.getInstance().getConfig().getPhotoMaxSize();
                    int currSize = MNImagePicker.getInstance().getImageList().size();
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
                    if (!rvPreviewList.isShown()) {
                        rvPreviewList.setVisibility(View.VISIBLE);
                    }
                } else {
                    toolbarRightTitle.setText(getString(R.string.send));
                    rvPreviewList.setVisibility(View.GONE);
                }

                previewListAdapter.notifyDataSetChanged();
            }
        });
        cbPreview = findViewById(R.id.cbPreview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPreviewList.setLayoutManager(linearLayoutManager);

        if (isOnlyPreview) {
            toolbarRightTitle.setVisibility(View.GONE);
            findViewById(R.id.layoutBottom).setVisibility(View.GONE);
            vpPreview.setPadding(0, 0, 0, 0);
        }
    }

    private void initData() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (MNImagePicker.getInstance().getImageList().contains(imageList.get(currPosition))) {
            cbPreview.setChecked(true);
        } else {
            cbPreview.setChecked(false);
        }

        previewListAdapter = new PreviewListAdapter(this, MNImagePicker.getInstance().getImageList());
        rvPreviewList.setAdapter(previewListAdapter);
        previewListAdapter.setOnItemClickListener(new PreviewListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, IImageBean imageBean, int position) {
                if (imageList.contains(imageBean)) {
                    vpPreview.setCurrentItem(imageList.indexOf(imageBean), false);
                    previewListAdapter.notifyDataSetChanged();
                } else {
                    vpPreview.setCurrentItem(0, false);
                    previewListAdapter.setCurrImageBean(null);
                }

            }
        });

        if (MNImagePicker.getInstance().getImageList().size() > 0) {
            rvPreviewList.setVisibility(View.VISIBLE);
        } else {
            rvPreviewList.setVisibility(View.GONE);
        }


        vpPreview.setAdapter(new PhotoViewAdapter());
        vpPreview.setCurrentItem(currPosition);
        vpPreview.setOffscreenPageLimit(3);
        vpPreview.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle((position + 1) + "/" + imageList.size());

                if (MNImagePicker.getInstance().getImageList().contains(imageList.get(position))) {
                    cbPreview.setChecked(true);
                } else {
                    cbPreview.setChecked(false);
                }

                currPosition = position;

                previewListAdapter.setCurrImageBean(imageList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        previewListAdapter.setCurrImageBean(imageList.get(currPosition));
    }


    class PhotoViewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View rootView = LayoutInflater.from(MLImagePreviewActivity.this).inflate(R.layout.item_preview_layout, null);
            PhotoView view = rootView.findViewById(R.id.photoPreview);
            int previewWidth = MNImagePicker.getInstance().getImagePreviewConfig().getPreviewWidth();
            int previewHeight = MNImagePicker.getInstance().getImagePreviewConfig().getPreviewHeight();
            if (previewWidth > 0 && previewHeight > 0) {
                MNImagePicker.getInstance().getImageLoadFrame().loadPreviewPhoto(MLImagePreviewActivity.this, imageList.get(position).getImgPath(), view, previewWidth, previewHeight);
            } else {
                MNImagePicker.getInstance().getImageLoadFrame().loadPreviewPhoto(MLImagePreviewActivity.this, imageList.get(position).getImgPath(), view, screenWidth, screenHeight);
            }
            ImageView downloadIcon = rootView.findViewById(R.id.downloadIcon);
            downloadIcon.setVisibility(View.GONE);
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private int getScreenHeight(Context context) {
        if (context != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        } else {
            return 1920;
        }
    }

    private int getScreenWidth(Context context) {
        if (context != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        } else {
            return 1080;
        }
    }
}
