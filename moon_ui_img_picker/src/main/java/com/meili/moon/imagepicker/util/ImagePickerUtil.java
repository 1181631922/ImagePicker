package com.meili.moon.imagepicker.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.iconfig.impl.ConfigImpl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Author： fanyafeng
 * Date： 2018/12/4 2:18 PM
 * Email: fanyafeng@live.cn
 */
public class ImagePickerUtil {

    /**
     * 通知图库更新
     *
     * @param context
     * @param file
     */
    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 调用系统相机拍照
     *
     * @param context
     * @param requestCode
     */
    public void takePicture(Context context, int requestCode) {
        File photoFile = null;
        if (MNImagePicker.getInstance().getConfig().getPhotoFile() != null) {
            photoFile = MNImagePicker.getInstance().getConfig().getPhotoFile();
        } else {
            if (MNImagePicker.getInstance().getPickerCommonConfig().getPhotoFile() != null) {
                photoFile = MNImagePicker.getInstance().getPickerCommonConfig().getPhotoFile();
            } else {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    photoFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
                } else {
                    photoFile = Environment.getDataDirectory();
                }
            }
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            if (photoFile != null) {
                File takeImageFile = createFile(photoFile, "IMG_", ".jpg");
                Uri uri = null;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    uri = Uri.fromFile(takeImageFile);
                } else {
                    try {
                        uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", takeImageFile);
                        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                MNImagePicker.getInstance().setTakeImageFile(takeImageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }
        ((Activity) context).startActivityForResult(takePictureIntent, requestCode);
    }

    private File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    public static File getCropCacheFolder() {
        File cropImgFolder = null;
        if (MNImagePicker.getInstance().getConfig().getCropFile() != null) {
            cropImgFolder = MNImagePicker.getInstance().getConfig().getCropFile();
        } else {
            if (MNImagePicker.getInstance().getPickerCommonConfig().getCropFile() != null) {
                cropImgFolder = MNImagePicker.getInstance().getPickerCommonConfig().getCropFile();
            } else {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    cropImgFolder = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
                } else {
                    cropImgFolder = Environment.getDataDirectory();
                }
            }
        }
        return cropImgFolder;
    }

    public static void clearConfig() {
        MNImagePicker.getInstance().setConfig(new ConfigImpl());
    }

}
