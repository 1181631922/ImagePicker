package com.meili.moon.imagepicker.iconfig;

import java.io.Serializable;

/**
 * Author： fanyafeng
 * Date： 2018/11/23 2:11 PM
 * Email: fanyafeng@live.cn
 */
public interface IImagePreviewConfig extends Serializable {
    //获取预览图的宽度
    int getPreviewWidth();

    //获取预览图的高度
    int getPreviewHeight();
}
