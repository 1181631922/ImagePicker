package com.meili.moon.imagepicker.ibean;

/**
 * Author： fanyafeng
 * Date： 2018/11/21 5:09 PM
 * Email: fanyafeng@live.cn
 */
public interface IImageTitleBean extends IImageBean {
    int getPosition();

    void setPosition(int position);

    String getTitle();

    void setTitle(String title);
}
