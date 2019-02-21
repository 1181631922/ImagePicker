package com.meili.moon.imagepicker.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.ibean.impl.ImageBean;
import com.meili.moon.imagepicker.util.BitmapUtil;
import com.meili.moon.imagepicker.util.ImagePickerUtil;
import com.meili.moon.imagepicker.view.CropImageView;

import java.io.File;

import static com.meili.moon.imagepicker.util.ConstantsUtil.LOCATION_DEAL_URL;
import static com.meili.moon.imagepicker.util.ConstantsUtil.LOCATION_URL;


public class MLTakePhotoActivity extends ImageBaseActivity implements CropImageView.OnBitmapSaveCompleteListener {
    private final static String TAG = MLTakePhotoActivity.class.getSimpleName();

    private final static int REQUEST_CODE_CAMERA_AND_STORAGE = 1000;

    private final static int REQUEST_CODE_CAMERA = 1001;

    private final static int REQUEST_CODE_STORAGE = 1002;

    private final static int PICTURE_DEAL_TAKE_PHOTO = 500;

    //    private Toolbar toolbar;
    private TextView toolbarRightTitle;
    private CropImageView civPhotoView;

    private String locationUrl;
    private Bitmap currBitmap;

    private boolean isTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        if (getIntent().getStringExtra(LOCATION_URL) != null) {
            locationUrl = getIntent().getStringExtra(LOCATION_URL);
        } else {
            requestMediaPermission();
        }

        initView();
        initData();
    }

    private void requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_AND_STORAGE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            } else {
                operateCamera();
            }
        } else {
            operateCamera();
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.deal_picture));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.go_back_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbarRightTitle = findViewById(R.id.toolbarRightTitle);
        toolbarRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// TODO: 17/11/24 图片裁剪完成
                civPhotoView.saveBitmapToFile(ImagePickerUtil.getCropCacheFolder(),
                        MNImagePicker.getInstance().getCropConfig().getOutPutWidth(), MNImagePicker.getInstance().getCropConfig().getOutPutHeight(), MNImagePicker.getInstance().getCropConfig().isSaveRectangle());
            }
        });

    }

    private void initData() {
        civPhotoView = findViewById(R.id.civPhotoView);
        civPhotoView.setFocusHeight(MNImagePicker.getInstance().getCropConfig().getCutHeight());
        civPhotoView.setFocusWidth(MNImagePicker.getInstance().getCropConfig().getCutWidth());
        civPhotoView.setOnBitmapSaveCompleteListener(this);
        civPhotoView.setFocusStyle(MNImagePicker.getInstance().getCropConfig().getCropImageStyle());

        if (locationUrl != null && !locationUrl.equals("")) {
            loadPicture(locationUrl);
        }
    }

    private void loadPicture(String picUrl) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picUrl, options);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        currBitmap = BitmapFactory.decodeFile(picUrl, options);
        civPhotoView.setImageBitmap(civPhotoView.rotate(currBitmap, BitmapUtil.getBitmapDegree(picUrl)));
    }

    private void operateCamera() {
        new ImagePickerUtil().takePicture(this, PICTURE_DEAL_TAKE_PHOTO);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICTURE_DEAL_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                loadPicture(MNImagePicker.getInstance().getTakeImageFile().getAbsolutePath());
                isTakePhoto = true;
//                Intent intent = new Intent();
//                intent.putExtra(MLImagePicker.LOCATION_DEAL_URL, MLImagePicker.getInstance().getTakeImageFile().getAbsolutePath());
//                setResult(MLImagePicker.CODE_RESULT_LOCAL_PHOTO, intent);
//                finish();
            } else {
                Toast.makeText(this, "拍照取消", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
        ImageBean imageBean = new ImageBean();
        imageBean.setImgPath(file.getAbsolutePath());
        Intent intent = new Intent();
        if (isTakePhoto) {
//            MLImagePicker.getInstance().getDealImageBeanList().add(imageBean);
//            intent.putParcelableArrayListExtra(RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MLImagePicker.getInstance().getDealImageBeanList());
//            setResult(MLImagePicker.CODE_RESULT_TAKE_PHOTO, intent);
        } else {
//            intent.putExtra(MLImagePicker.LOCATION_DEAL_URL, file.getAbsolutePath());
//            setResult(MLImagePicker.CODE_RESULT_LOCAL_PHOTO, intent);
        }
        ImagePickerUtil.galleryAddPic(this, file);
        if (MNImagePicker.getInstance().imageCropListener != null) {
            MNImagePicker.getInstance().imageCropListener.onImageCropListener(file);
            MNImagePicker.getInstance().imageCropListener = null;
        }
        intent.putExtra(LOCATION_DEAL_URL, file.getAbsolutePath());
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public void onBitmapSaveError(File file) {
        if (MNImagePicker.getInstance().imageCropListener != null) {
            MNImagePicker.getInstance().imageCropListener.onImageCropListener(null);
            MNImagePicker.getInstance().imageCropListener = null;
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_AND_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    operateCamera();
                } else {
                    Toast.makeText(MLTakePhotoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    operateCamera();
                } else {
                    Toast.makeText(MLTakePhotoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    operateCamera();
                } else {
                    Toast.makeText(MLTakePhotoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != civPhotoView) {
            civPhotoView.setOnBitmapSaveCompleteListener(null);
        }
        if (null != currBitmap && !currBitmap.isRecycled()) {
            currBitmap.recycle();
            currBitmap = null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //android可以保存view，但是view会进行重新绘制，重绘需要重新设置属性
        civPhotoView.setFocusHeight(MNImagePicker.getInstance().getCropConfig().getCutHeight());
        civPhotoView.setFocusWidth(MNImagePicker.getInstance().getCropConfig().getCutWidth());
        civPhotoView.setOnBitmapSaveCompleteListener(this);
        civPhotoView.setFocusStyle(MNImagePicker.getInstance().getCropConfig().getCropImageStyle());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}
