package com.meili.moon.imagepicker.iconfig.impl;

import com.meili.moon.imagepicker.iconfig.ICropConfig;
import com.meili.moon.imagepicker.view.CropImageView;

/**
 * Author： fanyafeng
 * Date： 2018/11/15 5:30 PM
 * Email: fanyafeng@live.cn
 */
public class CropConfigImpl implements ICropConfig {
    @Override
    public int getOutPutWidth() {
        return 1000;
    }

    @Override
    public int getOutPutHeight() {
        return 600;
    }

    @Override
    public int getCutWidth() {
        return 600;
    }

    @Override
    public int getCutHeight() {
        return 600;
    }

    @Override
    public CropImageView.Style getCropImageStyle() {
        return CropImageView.Style.RECTANGLE;
    }

    @Override
    public boolean isSaveRectangle() {
        return true;
    }
}
