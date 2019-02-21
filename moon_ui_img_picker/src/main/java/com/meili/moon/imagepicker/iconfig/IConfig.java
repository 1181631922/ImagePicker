package com.meili.moon.imagepicker.iconfig;

import java.io.File;
import java.io.Serializable;

/**
 * Author： fanyafeng
 * Date： 2018/11/15 4:13 PM
 * Email: fanyafeng@live.cn
 * <p>
 * 单次调用图片设置接口
 */
public interface IConfig extends Serializable {

//    PhotoPickType getPhotoPickType();

    /**
     * 设置单次图片最大张数 maxSize
     * maxSize>=1
     * maxSize<1不限制
     */
    int getPhotoMaxSize();

    /**
     * 用户拍照存图文件夹
     *
     * @return
     */
    File getPhotoFile();

    /**
     * 用户裁剪保存图片文件夹
     *
     * @return
     */
    File getCropFile();

}
