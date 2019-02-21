package com.meili.moon.imagepicker.ibean.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.meili.moon.imagepicker.ibean.IImageBean;
import com.meili.moon.imagepicker.ibean.IImageTitleBean;

/**
 * Author： fanyafeng
 * Date： 18/7/4 下午4:18
 * Email: fanyafeng@live.cn
 */
public class PickerTitleBean extends BaseBean implements Parcelable, Comparable<ImageBean>, IImageTitleBean {
    private String imgName;
    private String imgPath;
    private long imgSize;
    private int imgWidth;
    private int imgHeight;
    private String imgType;
    private long imgCreateTime;
    private int position;
    private String title;
    private Object tag;

    public PickerTitleBean() {
    }

    public PickerTitleBean cloneFromImageBean(IImageBean imageBean) {
        this.imgName = imageBean.getImgName();
        this.imgPath = imageBean.getImgPath();
        this.imgSize = imageBean.getImgSize();
        this.imgWidth = imageBean.getImgWidth();
        this.imgHeight = imageBean.getImgHeight();
        this.imgType = imageBean.getImgType();
        this.imgCreateTime = imageBean.getImgCreateTime();
        return this;
    }

    public ImageBean getImageBean() {
        ImageBean imageBean = new ImageBean();
        imageBean.setImgName(this.imgName);
        imageBean.setImgPath(this.imgPath);
        imageBean.setImgSize(this.imgSize);
        imageBean.setImgWidth(this.imgWidth);
        imageBean.setImgHeight(this.imgHeight);
        imageBean.setImgType(this.imgType);
        imageBean.setImgCreateTime(this.imgCreateTime);
        return imageBean;
    }

    protected PickerTitleBean(Parcel in) {
        imgName = in.readString();
        imgPath = in.readString();
        imgSize = in.readLong();
        imgWidth = in.readInt();
        imgHeight = in.readInt();
        imgType = in.readString();
        imgCreateTime = in.readLong();
        position = in.readInt();
        title = in.readString();
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
        dest.writeInt(position);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PickerTitleBean> CREATOR = new Creator<PickerTitleBean>() {
        @Override
        public PickerTitleBean createFromParcel(Parcel in) {
            return new PickerTitleBean(in);
        }

        @Override
        public PickerTitleBean[] newArray(int size) {
            return new PickerTitleBean[size];
        }
    };

    //判断选中状态，有可能会选择错误选择同一张图片
    @Override
    public boolean equals(Object o) {
        if (o instanceof PickerTitleBean) {
            PickerTitleBean item = (PickerTitleBean) o;
            return position == item.position;
        }

        return super.equals(o);
    }

    //根据时间排序图片
    @Override
    public int compareTo(@NonNull ImageBean o) {
        if (this.imgCreateTime > o.getImgCreateTime()) {
            return -1;
        } else if (this.imgCreateTime < o.getImgCreateTime()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "PickerTitleBean{" +
                "imgName='" + imgName + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", imgSize=" + imgSize +
                ", imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                ", imgType='" + imgType + '\'' +
                ", imgCreateTime=" + imgCreateTime +
                ", position=" + position +
                ", title='" + title + '\'' +
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

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }
}
