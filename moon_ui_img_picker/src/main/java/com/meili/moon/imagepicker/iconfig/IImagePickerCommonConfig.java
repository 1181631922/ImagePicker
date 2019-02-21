package com.meili.moon.imagepicker.iconfig;

import android.support.annotation.ColorInt;

import java.io.File;
import java.io.Serializable;


/**
 * Author： fanyafeng
 * Date： 2018/11/15 4:25 PM
 * Email: fanyafeng@live.cn
 */

// TODO: 2018/11/20  IImagePickerCommonConfig 
public interface IImagePickerCommonConfig extends Serializable,IConfig {

    //设置返回键
    int getNavigationIconRes();

    //设置状态栏图标颜色
    boolean isLight();

    //设置toolbar颜色
    @ColorInt
    int getToolbarColor();

    //设置状态栏颜色
    @ColorInt
    int getStatusBarColor();

    //设置标题栏中心title颜色
    @ColorInt
    int getToolbarCenterTitleColor();

    //设置标题栏右侧title
    @ColorInt
    int getToolbarRightTitleColor();

//    /**
//     * 设置全局图片最大张数 maxSize
//     * maxSize>=1
//     * maxSize<1不限制
//     */
//    int getPhotoMaxSize();
//
//    /**
//     * 用户拍照通用存图文件夹
//     */
//    File getPhotoFile();
//
//    /**
//     * 用户裁剪保存文件夹
//     */
//    File getCropFile();
}
