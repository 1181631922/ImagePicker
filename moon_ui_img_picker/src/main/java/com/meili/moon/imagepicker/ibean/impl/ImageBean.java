package com.meili.moon.imagepicker.ibean.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.meili.moon.imagepicker.ibean.IImageBean;
import com.meili.moon.imagepicker.util.StringUtil;

/**
 * Author： fanyafeng
 * Date： 17/11/16 下午4:22
 * Email: fanyafeng@live.cn
 */
public class ImageBean extends BaseBean implements Parcelable, Comparable<ImageBean>, IImageBean {
    private String imgName;
    private String imgPath;
    private long imgSize;
    private int imgWidth;
    private int imgHeight;
    private String imgType;
    private long imgCreateTime;
    private Object tag;


    public ImageBean() {
    }

    // FIXME: 17/11/20
//    shadow$_klass_
//    shadow$_monitor_
    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageBean) {
            ImageBean item = (ImageBean) o;
            if (StringUtil.isNullOrEmpty(item.imgPath) || StringUtil.isNullOrEmpty(this.imgPath)) {
                return false;
            }
            return this.imgPath.equalsIgnoreCase(item.imgPath) && this.imgCreateTime == item.imgCreateTime;
        }

        return super.equals(o);
    }

    public ImageBean(String imgName, String imgPath, long imgSize, int imgWidth, int imgHeight, String imgType, long imgCreateTime) {
        this.imgName = imgName;
        this.imgPath = imgPath;
        this.imgSize = imgSize;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.imgType = imgType;
        this.imgCreateTime = imgCreateTime;
    }

    protected ImageBean(Parcel in) {
        imgName = in.readString();
        imgPath = in.readString();
        imgSize = in.readLong();
        imgWidth = in.readInt();
        imgHeight = in.readInt();
        imgType = in.readString();
        imgCreateTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgName);
        dest.writeString(imgPath);
        dest.writeLong(imgSize);
        dest.writeInt(imgWidth);
        dest.writeInt(imgHeight);
        dest.writeString(imgType);
        dest.writeLong(imgCreateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel in) {
            return new ImageBean(in);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };

    @Override
    public int compareTo(@NonNull ImageBean o) {
        if (this.imgCreateTime > o.imgCreateTime) {
            return -1;
        } else if (this.imgCreateTime < o.imgCreateTime) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "ImageBean{" +
                "imgName='" + imgName + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", imgSize=" + imgSize +
                ", imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                ", imgType='" + imgType + '\'' +
                ", imgCreateTime=" + imgCreateTime +
                ", tag=" + tag +
                '}';
    }

    @Override
    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    @Override
    public String getImgName() {
        return imgName;
    }

    @Override
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public String getImgPath() {
        return imgPath;
    }

    @Override
    public void setImgSize(long imgSize) {
        this.imgSize = imgSize;
    }

    @Override
    public long getImgSize() {
        return imgSize;
    }

    @Override
    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    @Override
    public int getImgWidth() {
        return imgWidth;
    }

    @Override
    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    @Override
    public int getImgHeight() {
        return imgHeight;
    }

    @Override
    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    @Override
    public String getImgType() {
        return imgType;
    }

    @Override
    public void setImgCreateTime(long imgCreateTime) {
        this.imgCreateTime = imgCreateTime;
    }

    @Override
    public long getImgCreateTime() {
        return imgCreateTime;
    }

    @Override
    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public Object getTag() {
        return tag;
    }
}
