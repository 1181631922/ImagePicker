package com.meili.moon.imagepicker.iconfig.impl;

import android.graphics.Color;

import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.iconfig.IImagePickerCommonConfig;

import java.io.File;

/**
 * Author： fanyafeng
 * Date： 2018/11/15 5:35 PM
 * Email: fanyafeng@live.cn
 */
public class ImagePickerCommonConfig implements IImagePickerCommonConfig {
    @Override
    public int getNavigationIconRes() {
        return R.drawable.mn_imagepicker_back_icon;
    }

    @Override
    public boolean isLight() {
        return false;
    }

    @Override
    public int getToolbarColor() {
        return Color.parseColor("#3F51B5");
    }

    @Override
    public int getStatusBarColor() {
        return Color.parseColor("#303F9F");
    }

    @Override
    public int getToolbarCenterTitleColor() {
        return Color.WHITE;
    }

    @Override
    public int getToolbarRightTitleColor() {
        return Color.WHITE;
    }

    @Override
    public int getPhotoMaxSize() {
        return -1;
    }

    @Override
    public File getPhotoFile() {
        return null;
    }

    @Override
    public File getCropFile() {
        return null;
    }
}
