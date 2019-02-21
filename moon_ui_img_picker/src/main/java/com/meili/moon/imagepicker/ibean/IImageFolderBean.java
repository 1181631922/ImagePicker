package com.meili.moon.imagepicker.ibean;

import java.util.List;

/**
 * Author： fanyafeng
 * Date： 2018/11/16 3:03 PM
 * Email: fanyafeng@live.cn
 */
public interface IImageFolderBean extends IBaseBean {
    void setFolderName(String folderName);

    String getFolderName();

    void setFolderPath(String folderPath);

    String getFolderPath();


    <T extends IImageBean> void  setImageBean(T imageBean);

    IImageBean getImageBean();

    void setImageBeanList(List<? extends IImageBean> imageBeanList);

    List<? extends IImageBean> getImageBeanList();

    void setChecked(boolean isChecked);

    boolean isChecked();
}
