package com.meili.moon.imagepicker.kotlinextension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.meili.moon.imagepicker.MNImagePicker
import com.meili.moon.imagepicker.ibean.IImageBean
import com.meili.moon.imagepicker.ibean.IImageTitleBean
import com.meili.moon.imagepicker.iconfig.*
import com.meili.moon.imagepicker.iconfig.impl.ConfigImpl
import com.meili.moon.imagepicker.ui.MNImagePreviewActivity
import com.meili.moon.imagepicker.ui.MNTakePhotoAgencyActivity
import java.io.File
import java.util.ArrayList

/**
 * Author： fanyafeng
 * Date： 2018/11/15 4:00 PM
 * Email: fanyafeng@live.cn
 */

/** 调用系统相机拍照,并且有回调 */
@JvmOverloads
fun Context.photoTake(success: ((File) -> Unit)? = null) {
    MNImagePicker.getInstance().takePhotoListener = TakePhotoListener {
        success?.invoke(it)
    }
    this.startActivity(Intent(this, MNTakePhotoAgencyActivity::class.java))
}

@JvmOverloads
fun Activity.photoTake(success: ((File) -> Unit)? = null) {
    (this as Context).photoTake(success)
}

@JvmOverloads
fun Fragment.photoTake(success: ((File) -> Unit)? = null) {
    this.activity?.photoTake(success)
}

/** 辛巴单图选择 */
@JvmOverloads
fun Context.photoPickForSingle(tabPosition: Int = 0, titleBeanList: List<IImageTitleBean>, success: ((List<IImageTitleBean>) -> Unit)? = null) {
    MNImagePicker.getInstance().imageTitleListChangeListener = ImageTitleListChangeListener {
        success?.invoke(it)
    }
    MNImagePicker.getInstance().photoPickForSingle(this, titleBeanList, tabPosition)
}

@JvmOverloads
fun Activity.photoPickForSingle(tabPosition: Int = 0, titleBeanList: List<IImageTitleBean>, success: ((List<IImageTitleBean>) -> Unit)? = null) {
    (this as Context).photoPickForSingle(tabPosition, titleBeanList, success)
}

@JvmOverloads
fun Fragment.photoPickForSingle(tabPosition: Int = 0, titleBeanList: List<IImageTitleBean>, success: ((List<IImageTitleBean>) -> Unit)? = null) {
    this.activity?.photoPickForSingle(tabPosition, titleBeanList, success)
}

/** 类似微信单图选择 */
@JvmOverloads
fun Context.photoPickForSingle(success: ((IImageBean) -> kotlin.Unit)? = null) {
    MNImagePicker.getInstance().imageListChangeListener = ImageListChangeListener {
        success?.invoke(it[0])
    }
    MNImagePicker.getInstance().photoPickForSingle(this)
}

@JvmOverloads
fun Activity.photoPickForSingle(success: ((IImageBean) -> kotlin.Unit)? = null) {
    (this as Context).photoPickForSingle(success)
}


@JvmOverloads
fun Fragment.photoPickForSingle(success: ((IImageBean) -> kotlin.Unit)? = null) {
    this.activity?.photoPickForSingle(success)
}

/** 辛巴多图选择 */
@JvmOverloads
fun Context.photoPick(imageList: List<IImageBean>, success: ((List<IImageBean>) -> Unit)? = null) {
    MNImagePicker.getInstance().imageListChangeListener = ImageListChangeListener {
        success?.invoke(it)
    }
    MNImagePicker.getInstance().photoPickForMulti(this, imageList)
}

@JvmOverloads
fun Activity.photoPick(imageList: List<IImageBean>, success: ((List<IImageBean>) -> Unit)? = null) {
    (this as Context).photoPick(imageList, success)
}

@JvmOverloads
fun Fragment.photoPick(imageList: List<IImageBean>, success: ((List<IImageBean>) -> Unit)? = null) {
    this.activity?.photoPick(imageList, success)
}

@JvmOverloads
fun Context.photoPick(count: Int, imageList: List<IImageBean>, success: ((List<IImageBean>) -> Unit)? = null) {
    val config = ConfigImpl()
    config.photoMaxSize = count
    MNImagePicker.getInstance().config = config
    this.photoPick(imageList, success)
}

@JvmOverloads
fun Activity.photoPick(count: Int, imageList: List<IImageBean>, success: ((List<IImageBean>) -> Unit)? = null) {
    (this as Context).photoPick(count, imageList, success)
}

@JvmOverloads
fun Fragment.photoPick(count: Int, imageList: List<IImageBean>, success: ((List<IImageBean>) -> Unit)? = null) {
    this.activity?.photoPick(count, imageList, success)
}

@JvmOverloads
fun Context.photoPick(config: IConfig, imageList: List<IImageBean>, success: ((List<IImageBean>) -> Unit)? = null) {
    MNImagePicker.getInstance().config = config
    this.photoPick(imageList, success)
}

@JvmOverloads
fun Activity.photoPick(config: IConfig, imageList: List<IImageBean>, success: ((List<IImageBean>) -> Unit)? = null) {
    (this as Context).photoPick(config, imageList, success)
}

@JvmOverloads
fun Fragment.photoPick(config: IConfig, imageList: List<IImageBean>, success: ((List<IImageBean>) -> Unit)? = null) {
    this.activity?.photoPick(config, imageList, success)
}

/** 类似微信多图选择 */
@JvmOverloads
fun Context.photoPick(count: Int, success: ((List<IImageBean>) -> Unit)? = null) {
    val config = ConfigImpl()
    config.photoMaxSize = count
    MNImagePicker.getInstance().config = config
    MNImagePicker.getInstance().imageListChangeListener = ImageListChangeListener {
        success?.invoke(it)
    }
    MNImagePicker.getInstance().photoPickForMulti(this)
}

@JvmOverloads
fun Activity.photoPick(count: Int = -1, success: ((List<IImageBean>) -> Unit)? = null) {
    (this as Context).photoPick(count, success)
}

@JvmOverloads
fun Fragment.photoPick(count: Int = -1, success: ((List<IImageBean>) -> Unit)? = null) {
    this.activity?.photoPick(count, success)
}

@JvmOverloads
fun Context.photoPick(config: IConfig, success: ((List<IImageBean>) -> Unit)? = null) {
    MNImagePicker.getInstance().config = config
    MNImagePicker.getInstance().imageListChangeListener = ImageListChangeListener {
        success?.invoke(it)
    }
    MNImagePicker.getInstance().photoPickForMulti(this)
}

@JvmOverloads
fun Activity.photoPick(config: IConfig, success: ((List<IImageBean>) -> Unit)? = null) {
    (this as Context).photoPick(config, success)
}

@JvmOverloads
fun Fragment.photoPick(config: IConfig, success: ((List<IImageBean>) -> Unit)? = null) {
    this.activity?.photoPick(config, success)
}

/** 图片处理 */
@JvmOverloads
fun Context.photoCrop(locationUrl: String, success: ((File) -> Unit)? = null) {
    MNImagePicker.getInstance().imageCropListener = ImageCropListener {
        success?.invoke(it)
    }
    MNImagePicker.getInstance().photoCrop(this, locationUrl)
}

@JvmOverloads
fun Activity.photoCrop(locationUrl: String, success: ((File) -> Unit)? = null) {
    (this as Context).photoCrop(locationUrl, success)
}

@JvmOverloads
fun Fragment.photoCrop(locationUrl: String, success: ((File) -> Unit)? = null) {
    this.activity?.photoCrop(locationUrl, success)
}

@JvmOverloads
fun Context.photoCrop(cropConfig: ICropConfig, locationUrl: String, success: ((File) -> Unit)? = null) {
    MNImagePicker.getInstance().cropConfig = cropConfig
    this.photoCrop(locationUrl, success)
}

@JvmOverloads
fun Activity.photoCrop(cropConfig: ICropConfig, locationUrl: String, success: ((File) -> Unit)? = null){
    MNImagePicker.getInstance().cropConfig = cropConfig
    this.photoCrop(locationUrl, success)
}

@JvmOverloads
fun Fragment.photoCrop(cropConfig: ICropConfig, locationUrl: String, success: ((File) -> Unit)? = null){
    MNImagePicker.getInstance().cropConfig = cropConfig
    this.photoCrop(locationUrl, success)
}

/** 图片预览 */
@JvmOverloads
fun Context.photoPreview(imageList: List<IImageTitleBean>, currPosition: Int) {
    val intent = Intent(this, MNImagePreviewActivity::class.java)
    if (currPosition > 50) {
        intent.putExtra("position", 50)
        if (imageList.size > (currPosition + 50)) {
            val tempList = ArrayList<IImageTitleBean>()
            for (i in currPosition - 50 until currPosition + 50) {
                tempList.add(imageList[i])
            }
            intent.putParcelableArrayListExtra("imageList", tempList as ArrayList<out Parcelable>)
        } else {
            val tempList = ArrayList<IImageTitleBean>()
            for (i in currPosition - 50 until imageList.size) {
                tempList.add(imageList[i])
            }
            intent.putParcelableArrayListExtra("imageList", tempList as ArrayList<out Parcelable>)
        }
    } else {
        intent.putExtra("position", currPosition)
        if (imageList.size > 100) {
            val tempList = ArrayList<IImageTitleBean>()
            for (i in 0..99) {
                tempList.add(imageList[i])
            }
            intent.putParcelableArrayListExtra("imageList", tempList as ArrayList<out Parcelable>)
        } else {
            intent.putParcelableArrayListExtra("imageList", imageList as ArrayList<out Parcelable>)
        }
    }
    this.startActivity(intent)
}

@JvmOverloads
fun Activity.photoPreview(imageList: List<IImageTitleBean>, currPosition: Int) {
    (this as Context).photoPreview(imageList, currPosition)
}

@JvmOverloads
fun Fragment.photoPreview(imageList: List<IImageTitleBean>, currPosition: Int) {
    this.activity?.photoPreview(imageList, currPosition)
}