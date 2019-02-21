package com.meili.moon.imagepicker.iconfig.impl;

import com.meili.moon.imagepicker.iconfig.IChooseType;

/**
 * Author： fanyafeng
 * Date： 2018/11/15 6:02 PM
 * Email: fanyafeng@live.cn
 */
public class SingleChooseType implements IChooseType {
    @Override
    public boolean isSingleType() {
        return true;
    }
}
