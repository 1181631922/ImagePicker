package com.meili.moon.imagepicker.iconfig;

import android.content.Context;

import com.meili.moon.imagepicker.property.RoundingParams;
import com.meili.moon.imagepicker.view.MNPickerView;

import java.io.Serializable;

/**
 * Author： fanyafeng
 * Date： 2018/11/15 6:44 PM
 * Email: fanyafeng@live.cn
 */
public interface IImageLoadFrame extends Serializable {

    void loadPhoto(Context context, String imagePath, MNPickerView mnPickerView, int width, int height);

    void loadPreviewPhoto(Context context, String imagePath, MNPickerView mnPickerView, int width, int height);

    void loadPhoto(Context context, String imagePath, RoundingParams roundingParams, MNPickerView mnPickerView, int width, int height);

    boolean downloadPicture(Context context, String imagePath, String targetPath);

    void clearMemoryCache(Context context);
}
