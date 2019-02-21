package com.meili.moon.imagepicker.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.ibean.IImageTitleBean;
import com.meili.moon.imagepicker.util.DownLoadPictureUtil;
import com.meili.moon.imagepicker.util.StringUtil;
import com.meili.moon.imagepicker.view.photoview.HackyViewPager;
import com.meili.moon.imagepicker.view.photoview.PhotoView;
import com.meili.moon.ui.dialog.config.MNDialogConfig;
import com.meili.moon.ui.dialog.widget.MNDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MNImagePreviewActivity extends ImageBaseActivity {

    private HackyViewPager pickerPreviewViewPager;
    protected TextView toolbarCenterTitle;
    private List<? extends IImageTitleBean> imageList = new ArrayList<>();
    private int currPosition = 0;

    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnimage_preview);

        screenHeight = getScreenHeight(this);
        screenWidth = getScreenWidth(this);

        currPosition = getIntent().getIntExtra("position", 0);
        imageList = getIntent().getParcelableArrayListExtra("imageList");

        initView();
        initData();
    }

    private void shareImage(File targetFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri = Uri.fromFile(targetFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        copyImageToClipboard(uri);
    }

    private void copyImageToClipboard(Uri uri) {
        ClipboardManager mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ContentValues values = new ContentValues(2);
        values.put(MediaStore.Images.Media.MIME_TYPE, "Image/jpg");
        values.put(MediaStore.Images.Media.DATA, "file://" + uri.getPath());

        ContentResolver theContent = getContentResolver();
        Uri imageUri = theContent.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        ClipData theClip = ClipData.newUri(getContentResolver(), "Image", imageUri);
        mClipboard.setPrimaryClip(theClip);
        Toast.makeText(this, "复制到剪切板", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.go_back_left_arrow);


        pickerPreviewViewPager = findViewById(R.id.pickerPreviewViewPager);
        toolbarCenterTitle = findViewById(R.id.toolbarCenterTitle);

        if (imageList.size() > 0) {
            if (!StringUtil.isNullOrEmpty(imageList.get(currPosition).getTitle())) {
                toolbarCenterTitle.setText(String.format("%s %d/%d", imageList.get(currPosition).getTitle(), currPosition + 1, imageList.size()));
            } else {
                toolbarCenterTitle.setText(String.format("%d/%d", currPosition + 1, imageList.size()));
            }
        } else {
            toolbarCenterTitle.setText(getString(R.string.img_picker_name));
        }
    }

    private void initData() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pickerPreviewViewPager.setAdapter(new PhotoViewAdapter());
        pickerPreviewViewPager.setCurrentItem(currPosition);
        pickerPreviewViewPager.setOffscreenPageLimit(3);
        pickerPreviewViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currPosition = position;

                if (!StringUtil.isNullOrEmpty(imageList.get(currPosition).getTitle())) {
                    toolbarCenterTitle.setText(String.format("%s %d/%d", imageList.get(currPosition).getTitle(), currPosition + 1, imageList.size()));
                } else {
                    toolbarCenterTitle.setText(String.format("%d/%d", currPosition + 1, imageList.size()));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
    }

    private View dialogView = null;
    private MNDialog mnDialog = null;

    private void showBottomDialog() {
        if (dialogView == null) {
            dialogView = LayoutInflater.from(MNImagePreviewActivity.this).inflate(R.layout.mn_dialog_layout_bottom_dialog, null);
        }

        if (mnDialog == null) {
            mnDialog = new MNDialog(new MNDialogConfig.Builder()
                    .setContext(MNImagePreviewActivity.this)
                    .setGravity(Gravity.BOTTOM)
                    .setContentView(dialogView)
                    .setCancel(true)
                    .setAnimation(R.style.MNMenuAnimation)
                    .setThemeResId(R.style.MNDialogStyle)
                    .build());
        }

        TextView action1 = dialogView.findViewById(R.id.action1);
        action1.setText("复制到剪切板");
        action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MNImagePreviewActivity.this, "复制到剪切板", Toast.LENGTH_SHORT).show();

                shareImage(new File(imageList.get(currPosition).getImgPath()));
                mnDialog.dismiss();
            }
        });

        TextView action2 = dialogView.findViewById(R.id.action2);
        action2.setText("下载图片");
        action2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPicture();
                mnDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btn_dialog_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mnDialog.dismiss();
            }
        });
        mnDialog.show();
    }

    private void downloadPicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean isSuccess = DownLoadPictureUtil.downloadPicture(MNImagePreviewActivity.this, imageList.get(currPosition).getImgPath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccess) {
                            Toast.makeText(MNImagePreviewActivity.this, "图片保存成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MNImagePreviewActivity.this, "图片保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
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
            View rootView = LayoutInflater.from(MNImagePreviewActivity.this).inflate(R.layout.item_preview_layout, null);
            PhotoView view = rootView.findViewById(R.id.photoPreview);
            int previewWidth = MNImagePicker.getInstance().getImagePreviewConfig().getPreviewWidth();
            int previewHeight = MNImagePicker.getInstance().getImagePreviewConfig().getPreviewHeight();
            if (previewWidth > 0 && previewHeight > 0) {
                MNImagePicker.getInstance().getImageLoadFrame().loadPreviewPhoto(MNImagePreviewActivity.this, imageList.get(position).getImgPath(), view, previewWidth, previewHeight);
            } else {
                MNImagePicker.getInstance().getImageLoadFrame().loadPreviewPhoto(MNImagePreviewActivity.this, imageList.get(position).getImgPath(), view, screenWidth, screenHeight);
            }
            ImageView downloadIcon = rootView.findViewById(R.id.downloadIcon);
            if (imageList.get(position).getImgPath().startsWith("http")) {
                downloadIcon.setVisibility(View.VISIBLE);
            } else {
                downloadIcon.setVisibility(View.GONE);
            }
            downloadIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadPicture();
                }
            });
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
