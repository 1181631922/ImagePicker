package com.meili.moon.imagepicker.iconfig;

import com.meili.moon.imagepicker.view.CropImageView;

import java.io.Serializable;

/**
 * Author： fanyafeng
 * Date： 2018/11/15 5:26 PM
 * Email: fanyafeng@live.cn
 *
 * 裁剪图片接口
 */

public interface ICropConfig extends Serializable {
    //处理后图片的宽度
    int getOutPutWidth();

    //处理后图片的高度
    int getOutPutHeight();

    //裁剪宽度
    int getCutWidth();

    //裁剪高度
    int getCutHeight();

    //裁剪style
    CropImageView.Style getCropImageStyle();

    //处理图片框形状
    boolean isSaveRectangle();
}
