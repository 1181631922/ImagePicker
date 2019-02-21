package com.meili.moon.imagepicker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;


import com.meili.moon.imagepicker.ibean.impl.ImageBean;
import com.meili.moon.imagepicker.ibean.impl.ImageFolderBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author： fanyafeng
 * Date： 17/11/16 下午4:38
 * Email: fanyafeng@live.cn
 */
public class ScanImageSource implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int SCAN_ALL_IMAGES = 100;
    private final int SCAN_FOLDER = 101;

    //    图片属性
    private final String[] IMAGE_ATTRIBUTE = {
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED
    };

    private AppCompatActivity appCompatActivity;
    private List<ImageFolderBean> imageFolderBeanList = new ArrayList<>();
    private OnImageLoadedListener onImageLoadedListener;
    private static ExecutorService mFixedThreadPool = null;
    private static final Object locker = new Object();

    public ScanImageSource(AppCompatActivity appCompatActivity, String path, OnImageLoadedListener onImageLoadedListener) {
        this.appCompatActivity = appCompatActivity;
        this.onImageLoadedListener = onImageLoadedListener;
        synchronized (locker) {
            if (mFixedThreadPool == null) {
                mFixedThreadPool = Executors.newFixedThreadPool(1);
            }
        }

        LoaderManager loaderManager = appCompatActivity.getSupportLoaderManager();
        if (path == null) {
            loaderManager.initLoader(SCAN_ALL_IMAGES, null, this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            loaderManager.initLoader(SCAN_FOLDER, bundle, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        if (id == SCAN_ALL_IMAGES) {
            cursorLoader = new CursorLoader(appCompatActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_ATTRIBUTE, null, null, IMAGE_ATTRIBUTE[6]);
        } else if (id == SCAN_FOLDER) {
            cursorLoader = new CursorLoader(appCompatActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_ATTRIBUTE, IMAGE_ATTRIBUTE[1] + " like %" + args.getString("path") + "%", null, IMAGE_ATTRIBUTE[6]);
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
//        cursor.moveToFirst();
        final List<ImageFolderBean> imageFolderBeanList = new ArrayList<>();
        final List<ImageBean> imageBeanList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String imageName = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[0]));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[1]));
            long imageSize = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[2]));
            int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[3]));
            int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[4]));
            String imageType = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[5]));
            long imageCreateTime = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[6]));
            ImageBean imageBean = new ImageBean(imageName, path, imageSize, imageWidth, imageHeight, imageType, imageCreateTime);
            imageBeanList.add(imageBean);
        }
        if (imageBeanList.size() <= 0) {
            onImageLoadedListener.onImageLoaded(imageFolderBeanList);
            return;
        }
        mFixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (appCompatActivity == null) return;
                for (int i = 0; i < imageBeanList.size(); i++) {
                    ImageBean item = imageBeanList.get(i);
                    File file = new File(item.getImgPath());
                    if (!file.exists() || file.length() <= 0) {
                        imageBeanList.remove(item);
                        i--;
                        continue;
                    }
                    File imageFile = new File(item.getImgPath());
                    File imageParentFile = imageFile.getParentFile();
                    ImageFolderBean imageFolderBean = new ImageFolderBean();
                    imageFolderBean.setFolderName(imageParentFile.getName());
                    imageFolderBean.setFolderPath(imageParentFile.getAbsolutePath());
                    int index = imageFolderBeanList.indexOf(imageFolderBean);
                    if (index < 0) {
                        List<ImageBean> imageBeans = new ArrayList<>();
                        imageBeans.add(item);
                        imageFolderBean.setImageBean(item);
                        imageFolderBean.setImageBeanList(imageBeans);
                        imageFolderBeanList.add(imageFolderBean);
                    } else {
                        ImageFolderBean imageFolderBean1 = imageFolderBeanList.get(index);
                        imageFolderBean1.getImageBeanList().add(item);
                    }
                }
                //按照时间排序
                Collections.sort(imageBeanList);
                if (imageBeanList.size() > 0) {
                    ImageFolderBean allImageFolderBean = new ImageFolderBean();
                    allImageFolderBean.setFolderName("所有图片");
                    allImageFolderBean.setFolderPath("/");
                    allImageFolderBean.setImageBean(imageBeanList.get(0));
                    allImageFolderBean.setImageBeanList(imageBeanList);
                    imageFolderBeanList.add(0, allImageFolderBean);
                }
                synchronized (locker) {
                    Activity act = appCompatActivity;
                    if (act != null) {
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onImageLoadedListener.onImageLoaded(imageFolderBeanList);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface OnImageLoadedListener {
        void onImageLoaded(List<ImageFolderBean> imageFolderBeanList);
    }

    public void recycle() {
        synchronized (locker) {
            appCompatActivity = null;
        }
    }
}