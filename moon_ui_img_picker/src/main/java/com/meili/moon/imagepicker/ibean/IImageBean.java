package com.meili.moon.imagepicker.ibean;

import android.os.Parcelable;

/**
 * Author： fanyafeng
 * Date： 2018/11/16 2:35 PM
 * Email: fanyafeng@live.cn
 */
public interface IImageBean extends IBaseBean, Parcelable {
    void setImgName(String imgName);

    String getImgName();

    void setImgPath(String imgPath);

    String getImgPath();

    void setImgSize(long imgSize);

    long getImgSize();

    void setImgWidth(int imgWidth);

    int getImgWidth();

    void setImgHeight(int imgHeight);

    int getImgHeight();

    void setImgType(String imgType);

    String getImgType();

    void setImgCreateTime(long imgCreateTime);

    long getImgCreateTime();

    void setTag(Object tag);

    Object getTag();
}
