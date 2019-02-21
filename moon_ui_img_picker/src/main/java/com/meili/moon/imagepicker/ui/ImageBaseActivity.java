package com.meili.moon.imagepicker.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.R;

public class ImageBaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected Toolbar toolbar;
    protected TextView toolbarCenterTitle;
    protected TextView toolbarRightTitle;

    /**
     * android md风格
     * <p>
     * 标题栏颜色为toolbar色值+16（印象中是）
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(MNImagePicker.getInstance().getPickerCommonConfig().getStatusBarColor());
            if (MNImagePicker.getInstance().getPickerCommonConfig().isLight()) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(MNImagePicker.getInstance().getPickerCommonConfig().getNavigationIconRes());

            toolbar.setBackgroundColor(MNImagePicker.getInstance().getPickerCommonConfig().getToolbarColor());

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        toolbarCenterTitle = findViewById(R.id.toolbarCenterTitle);
        if (toolbarCenterTitle != null) {
            toolbarCenterTitle.setTextColor(MNImagePicker.getInstance().getPickerCommonConfig().getToolbarCenterTitleColor());
        }

        toolbarRightTitle = findViewById(R.id.toolbarRightTitle);
        if (toolbarRightTitle != null) {
            toolbarRightTitle.setTextColor(MNImagePicker.getInstance().getPickerCommonConfig().getToolbarRightTitleColor());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MNImagePicker.getInstance().onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MNImagePicker.getInstance().onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {

    }
}
