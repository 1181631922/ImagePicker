package com.meili.moon.imagepicker.iconfig;

import com.meili.moon.imagepicker.ibean.IImageBean;

import java.io.Serializable;
import java.util.List;

/**
 * Author： fanyafeng
 * Date： 2018/11/19 6:15 PM
 * Email: fanyafeng@live.cn
 */
public interface ImageListChangeListener {
    void onImageListChangeListener(List<IImageBean> imageBeanList);
}
