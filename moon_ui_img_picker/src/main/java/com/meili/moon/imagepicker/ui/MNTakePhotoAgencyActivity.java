package com.meili.moon.imagepicker.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.iconfig.TakePhotoListener;
import com.meili.moon.imagepicker.util.ImagePickerUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MNTakePhotoAgencyActivity extends ImageBaseActivity {

    private final static int REQUEST_CODE_CAMERA_AND_STORAGE = 1000;

    private final static int REQUEST_CODE_CAMERA = 1001;

    private final static int REQUEST_CODE_STORAGE = 1002;

    private final static int TAKE_PHOTO_REQUEST_CODE = 1313;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestMediaPermission();
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
                new ImagePickerUtil().takePicture(this, TAKE_PHOTO_REQUEST_CODE);
            }
        } else {
            new ImagePickerUtil().takePicture(this, TAKE_PHOTO_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_AND_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    new ImagePickerUtil().takePicture(this, TAKE_PHOTO_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new ImagePickerUtil().takePicture(this, TAKE_PHOTO_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new ImagePickerUtil().takePicture(this, TAKE_PHOTO_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST_CODE:
                    if (MNImagePicker.getInstance().takePhotoListener != null) {
                        MNImagePicker.getInstance().takePhotoListener.onTakePhotoListener(MNImagePicker.getInstance().getTakeImageFile());
                        ImagePickerUtil.galleryAddPic(this, MNImagePicker.getInstance().getTakeImageFile());
                        MNImagePicker.getInstance().takePhotoListener = null;
                    }
                    Intent intent=new Intent();
                    intent.putExtra("path",MNImagePicker.getInstance().getTakeImageFile().getAbsolutePath());
                    setResult(RESULT_OK,intent);
                    break;
            }
        }
        finish();
    }
}
