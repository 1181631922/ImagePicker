package com.meili.moon.imagepicker.ibean.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.meili.moon.imagepicker.ibean.IImageBean;
import com.meili.moon.imagepicker.ibean.IImageFolderBean;

import java.util.List;

/**
 * Author： fanyafeng
 * Date： 17/11/16 下午4:27
 * Email: fanyafeng@live.cn
 */
public class ImageFolderBean extends BaseBean implements Parcelable, Comparable<ImageFolderBean>, IImageFolderBean {
    private String folderName;
    private String folderPath;
    private ImageBean imageBean;
    private List<ImageBean> imageBeanList;
    private boolean isChecked;

    public ImageFolderBean() {
    }


    protected ImageFolderBean(Parcel in) {
        folderName = in.readString();
        folderPath = in.readString();
        imageBean = in.readParcelable(ImageBean.class.getClassLoader());
        imageBeanList = in.createTypedArrayList(ImageBean.CREATOR);
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderName);
        dest.writeString(folderPath);
        dest.writeParcelable(imageBean, flags);
        dest.writeTypedList(imageBeanList);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageFolderBean> CREATOR = new Creator<ImageFolderBean>() {
        @Override
        public ImageFolderBean createFromParcel(Parcel in) {
            return new ImageFolderBean(in);
        }

        @Override
        public ImageFolderBean[] newArray(int size) {
            return new ImageFolderBean[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        ImageFolderBean o = (ImageFolderBean) obj;
        return this.folderPath.equalsIgnoreCase(o.folderPath) && this.folderName.equalsIgnoreCase(o.folderName);
    }


    @Override
    public int compareTo(@NonNull ImageFolderBean o) {
        if (this.imageBeanList.size() > o.imageBeanList.size()) {
            return -1;
        } else if (this.imageBeanList.size() > o.imageBeanList.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "ImageFolderBean{" +
                "folderName='" + folderName + '\'' +
                ", folderPath='" + folderPath + '\'' +
                ", imageBean=" + imageBean +
                ", imageBeanList=" + imageBeanList +
                '}';
    }

    @Override
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public String getFolderName() {
        return folderName;
    }

    @Override
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    @Override
    public String getFolderPath() {
        return folderPath;
    }

    @Override
    public <T extends IImageBean> void setImageBean(T imageBean) {
        this.imageBean = (ImageBean) imageBean;
    }

    @Override
    public IImageBean getImageBean() {
        return imageBean;
    }

    @Override
    public void setImageBeanList(List<? extends IImageBean> imageBeanList) {
        this.imageBeanList = (List<ImageBean>) imageBeanList;
    }

    @Override
    public List getImageBeanList() {
        return imageBeanList;
    }


    @Override
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }
}
