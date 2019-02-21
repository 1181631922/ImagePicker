package com.meili.moon.imagepicker.iconfig.impl;

import com.meili.moon.imagepicker.iconfig.IConfig;

import java.io.File;

/**
 * Author： fanyafeng
 * Date： 2018/11/15 4:22 PM
 * Email: fanyafeng@live.cn
 */
public class ConfigImpl implements IConfig {

    private int photoMaxSize = -1;

    public void setPhotoMaxSize(int photoMaxSize) {
        this.photoMaxSize = photoMaxSize;
    }

    @Override
    public int getPhotoMaxSize() {
        return photoMaxSize;
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
