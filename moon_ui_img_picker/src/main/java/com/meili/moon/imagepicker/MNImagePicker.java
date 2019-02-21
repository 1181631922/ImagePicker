package com.meili.moon.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.meili.moon.imagepicker.ibean.IImageBean;
import com.meili.moon.imagepicker.ibean.IImageTitleBean;
import com.meili.moon.imagepicker.iconfig.ICropConfig;
import com.meili.moon.imagepicker.iconfig.IImagePickerCommonConfig;
import com.meili.moon.imagepicker.iconfig.IChooseType;
import com.meili.moon.imagepicker.iconfig.IConfig;
import com.meili.moon.imagepicker.iconfig.IImageLoadFrame;
import com.meili.moon.imagepicker.iconfig.IImagePreviewConfig;
import com.meili.moon.imagepicker.iconfig.ImageCropListener;
import com.meili.moon.imagepicker.iconfig.ImageListChangeListener;
import com.meili.moon.imagepicker.iconfig.ImageTitleListChangeListener;
import com.meili.moon.imagepicker.iconfig.TakePhotoListener;
import com.meili.moon.imagepicker.iconfig.impl.CropConfigImpl;
import com.meili.moon.imagepicker.iconfig.impl.ImagePickerCommonConfig;
import com.meili.moon.imagepicker.iconfig.impl.ConfigImpl;
import com.meili.moon.imagepicker.iconfig.impl.ImagePreviewConfig;
import com.meili.moon.imagepicker.iconfig.impl.MutliChooseType;
import com.meili.moon.imagepicker.iconfig.impl.SingleChooseType;
import com.meili.moon.imagepicker.ui.MLImageListActivity;
import com.meili.moon.imagepicker.ui.MLTakePhotoActivity;
import com.meili.moon.imagepicker.ui.MNImagePickerActivity;
import com.meili.moon.imagepicker.ui.MNImagePreviewActivity;
import com.meili.moon.imagepicker.util.Preconditions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.meili.moon.imagepicker.util.ConstantsUtil.LOCATION_URL;

/**
 * Author： fanyafeng
 * Date： 2018/11/15 4:33 PM
 * Email: fanyafeng@live.cn
 */
public class MNImagePicker {

    private MNImagePicker() {
    }

    private static MNImagePicker instance;

    public static MNImagePicker getInstance() {
        if (instance == null) {
            synchronized (MNImagePicker.class) {
                if (instance == null) {
                    instance = new MNImagePicker();
                }
            }
        }
        return instance;
    }

    //图片拍照
    public TakePhotoListener takePhotoListener;

    private File takeImageFile;

    public File getTakeImageFile() {
        return takeImageFile;
    }

    public void setTakeImageFile(File takeImageFile) {
        this.takeImageFile = takeImageFile;
    }

    //打开图片预览
    public void photoPreview(Activity activity, List<IImageTitleBean> beanList, int currPosition) {
        Intent intent = new Intent(activity, MNImagePreviewActivity.class);

        if (currPosition > 50) {
            intent.putExtra("position", 50);
            if (beanList.size() > (currPosition + 50)) {
                List<IImageTitleBean> tempList = new ArrayList<>();
                for (int i = currPosition - 50; i < currPosition + 50; i++) {
                    tempList.add(beanList.get(i));
                }
                intent.putParcelableArrayListExtra("imageList", (ArrayList<? extends Parcelable>) tempList);
            } else {
                List<IImageTitleBean> tempList = new ArrayList<>();
                for (int i = currPosition - 50; i < beanList.size(); i++) {
                    tempList.add(beanList.get(i));
                }
                intent.putParcelableArrayListExtra("imageList", (ArrayList<? extends Parcelable>) tempList);
            }
        } else {
            intent.putExtra("position", currPosition);
            if (beanList.size() > 100) {
                List<IImageTitleBean> tempList = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                    tempList.add(beanList.get(i));
                }
                intent.putParcelableArrayListExtra("imageList", (ArrayList<? extends Parcelable>) tempList);
            } else {
                intent.putParcelableArrayListExtra("imageList", (ArrayList<? extends Parcelable>) beanList);
            }
        }
        activity.startActivity(intent);
    }

    //裁剪图片文件夹
    public ImageCropListener imageCropListener;

    public void photoCrop(Context context, String locationUrl) {
        photoCrop(context, locationUrl, -1);
    }

    // /storage/emulated/0/DCIM/Camera/IMG_1532052705324.jpg
    public void photoCrop(Context context, String locationUrl, int requestCode) {
        Intent intent = new Intent(context, MLTakePhotoActivity.class);
        intent.putExtra(LOCATION_URL, locationUrl);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public ImageTitleListChangeListener imageTitleListChangeListener;

    //辛巴单选图片
    public void photoPickForSingle(Context context, List<IImageTitleBean> titleBeanList, int tabPosition) {
        MNImagePicker.getInstance().getImageTitleBeanList().clear();
        Intent intent = new Intent(context, MNImagePickerActivity.class);
        intent.putParcelableArrayListExtra("titleBeanList", (ArrayList<? extends Parcelable>) titleBeanList);
        intent.putExtra("tabPosition", tabPosition);
        context.startActivity(intent);
    }

    private List<IImageTitleBean> imageTitleBeanList = new ArrayList<>();

    public List<IImageTitleBean> getImageTitleBeanList() {
        return imageTitleBeanList;
    }

    public void setImageTitleBeanList(List<IImageTitleBean> imageTitleBeanList) {
        this.imageTitleBeanList = imageTitleBeanList;
    }

    public ImageListChangeListener imageListChangeListener;

    //类微信单选图片
    public void photoPickForSingle(Context context) {
        photoPickForSingle(context, -1);
    }

    public void photoPickForSingle(Context context, int requestCode) {
        MNImagePicker.getInstance().getImageList().clear();
        MNImagePicker.getInstance().setChooseType(new SingleChooseType());
        Intent intent = new Intent(context, MLImageListActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    //辛巴多选图片
    public void photoPickForMulti(Context context, List<IImageBean> imageBeanList) {
        photoPickForMulti(context, imageBeanList, -1);
    }

    public void photoPickForMulti(Context context, List<IImageBean> imageBeanList, int requestCode) {
        MNImagePicker.getInstance().getImageList().clear();
        Intent intent = new Intent(context, MNImagePickerActivity.class);
        intent.putParcelableArrayListExtra("imageBeanList", (ArrayList<? extends Parcelable>) imageBeanList);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    //类微信多选图片
    public void photoPickForMulti(Context context) {
        photoPickForMulti(context, -1);
    }

    public void photoPickForMulti(Context context, int requestCode) {
        MNImagePicker.getInstance().getImageList().clear();
        MNImagePicker.getInstance().setChooseType(new MutliChooseType());
        Intent intent = new Intent(context, MLImageListActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    //图片预览


    //选中图片列表结果
    private List<IImageBean> imageList = new ArrayList<>();

    public List<IImageBean> getImageList() {
        return imageList;
    }

    public void setImageList(List<IImageBean> imageList) {
        this.imageList = imageList;
    }

    private IImagePickerCommonConfig pickerCommonConfig = new ImagePickerCommonConfig();

    public IImagePickerCommonConfig getPickerCommonConfig() {
        return pickerCommonConfig;
    }

    public MNImagePicker setPickerCommonConfig(IImagePickerCommonConfig pickerCommonConfig) {
        Preconditions.checkNotNull(pickerCommonConfig, "IImagePickerCommonConfig 接口未实现");
        this.pickerCommonConfig = pickerCommonConfig;
        return this;
    }

    private IImageLoadFrame imageLoadFrame;

    public IImageLoadFrame getImageLoadFrame() {
        Preconditions.checkNotNull(imageLoadFrame, "IImageLoadFrame 接口未实现");
        return imageLoadFrame;
    }

    public MNImagePicker setImageLoadFrame(IImageLoadFrame imageLoadFrame) {
        Preconditions.checkNotNull(imageLoadFrame, "IImageLoadFrame 接口未实现");
        this.imageLoadFrame = imageLoadFrame;
        return this;
    }

    private IConfig iConfig = new ConfigImpl();

    public IConfig getConfig() {
        return iConfig;
    }

    public MNImagePicker setConfig(IConfig iConfig) {
        Preconditions.checkNotNull(iConfig, "IConfig 接口未实现");
        this.iConfig = iConfig;
        return this;
    }

    private IChooseType iChooseType = new MutliChooseType();

    public IChooseType getChooseType() {
        return iChooseType;
    }

    public MNImagePicker setChooseType(IChooseType iChooseType) {
        Preconditions.checkNotNull(iChooseType, "iChooseType 接口未实现");
        this.iChooseType = iChooseType;
        return this;
    }

    private ICropConfig iCropConfig = new CropConfigImpl();

    public ICropConfig getCropConfig() {
        return iCropConfig;
    }

    public MNImagePicker setCropConfig(ICropConfig iCropConfig) {
        this.iCropConfig = iCropConfig;
        return this;
    }

    private IImagePreviewConfig imagePreviewConfig = new ImagePreviewConfig();

    public IImagePreviewConfig getImagePreviewConfig() {
        return imagePreviewConfig;
    }

    public MNImagePicker setImagePreviewConfig(IImagePreviewConfig imagePreviewConfig) {
        this.imagePreviewConfig = imagePreviewConfig;
        return this;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        takeImageFile = (File) savedInstanceState.getSerializable("takeImageFile");

        pickerCommonConfig = (IImagePickerCommonConfig) savedInstanceState.getSerializable("pickerCommonConfig");
        imageLoadFrame = (IImageLoadFrame) savedInstanceState.getSerializable("imageLoadFrame");
        iConfig = (IConfig) savedInstanceState.getSerializable("iConfig");
        iChooseType = (IChooseType) savedInstanceState.getSerializable("iChooseType");
        iCropConfig = (ICropConfig) savedInstanceState.getSerializable("iCropConfig");
        imagePreviewConfig = (IImagePreviewConfig) savedInstanceState.getSerializable("imagePreviewConfig");

        imageTitleBeanList = savedInstanceState.getParcelableArrayList("imageTitleBeanList");
        imageList = savedInstanceState.getParcelableArrayList("imageList");
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("takeImageFile", takeImageFile);

        outState.putSerializable("pickerCommonConfig", pickerCommonConfig);
        outState.putSerializable("imageLoadFrame", imageLoadFrame);
        outState.putSerializable("iConfig", iConfig);
        outState.putSerializable("iChooseType", iChooseType);
        outState.putSerializable("iCropConfig", iCropConfig);
        outState.putSerializable("imagePreviewConfig", imagePreviewConfig);

        outState.putParcelableArrayList("imageTitleBeanList", (ArrayList<? extends Parcelable>) imageTitleBeanList);
        outState.putParcelableArrayList("imageList", (ArrayList<? extends Parcelable>) imageList);
    }
}
