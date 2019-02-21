package com.meili.moon.imagepicker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.meili.moon.imagepicker.MNImagePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author： fanyafeng
 * Date： 2018/11/13 3:05 PM
 * Email: fanyafeng@live.cn
 */
public class DownLoadPictureUtil {

    public static boolean downloadPicture(Context context, String path) {
        if (path != null && !path.equals("")) {
            if (!path.startsWith("http")) {
                return downloadLocalPicture(context, path);
            } else {
                return downloadNetPicture(context, path);
            }
        } else {
            return false;
        }
    }

    public static boolean downloadLocalPicture(Context context, String imgPath) {
        String filePath = createDir(Environment.getExternalStorageDirectory() + "/mljr_save_pic") + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";

        File file = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(imgPath);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        ImagePickerUtil.galleryAddPic(context, file);

        return true;
    }

    private static String getSDCardPath() {
        return Environment.getExternalStorageDirectory() + File.separator;
    }

    private static String createDir(String path) {
        File file = new File(getSDCardPath() + path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }


    public static boolean downloadNetPicture(Context context, String url) {
        String filePath = createDir(Environment.getExternalStorageDirectory() + "/mljr_save_pic") + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
        return MNImagePicker.getInstance().getImageLoadFrame().downloadPicture(context, url, filePath);
    }
}
