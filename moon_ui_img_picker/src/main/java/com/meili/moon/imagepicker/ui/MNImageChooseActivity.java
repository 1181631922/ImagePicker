package com.meili.moon.imagepicker.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.adapter.ImageChooseListAdapter;
import com.meili.moon.imagepicker.ibean.IImageBean;

import java.util.ArrayList;
import java.util.List;

import static com.meili.moon.imagepicker.ui.MLImageListActivity.RESULT_IMG_LIST;

public class MNImageChooseActivity extends ImageBaseActivity {

    private RecyclerView rvImageChooseList;
    private List<IImageBean> imageBeanList = new ArrayList<>();
    private ImageChooseListAdapter chooseListAdapter;
    protected TextView toolbarCenterTitle;
    protected TextView toolbarRightTitle;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnimage_choose);

        imageBeanList = getIntent().getParcelableArrayListExtra("imageBeanList");
        if (imageBeanList.size() == 0) {
//            MLImagePicker.getInstance().startPictureChoose(this, imageBeanList, 100);
            MNImagePicker.getInstance().photoPickForMulti(this, imageBeanList, 100);
        }
        title = getIntent().getStringExtra("title");

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) imageBeanList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.go_back_left_arrow);

        toolbarCenterTitle = findViewById(R.id.toolbarCenterTitle);
        toolbarRightTitle = findViewById(R.id.toolbarRightTitle);
        rvImageChooseList = findViewById(R.id.rvImageChooseList);
    }

    private void initData() {

        int configMaxSize = MNImagePicker.getInstance().getConfig().getPhotoMaxSize();
        if (configMaxSize > 0) {
            toolbarCenterTitle.setText(title + " " + imageBeanList.size() + "/" + configMaxSize);
        } else {
            int commonMaxSize = MNImagePicker.getInstance().getPickerCommonConfig().getPhotoMaxSize();
            if (commonMaxSize > 0) {
                toolbarCenterTitle.setText(title + " " + imageBeanList.size() + "/" + commonMaxSize);
            } else {
                toolbarCenterTitle.setText(title + " " + imageBeanList.size());
            }
        }

        rvImageChooseList.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        chooseListAdapter = new ImageChooseListAdapter(this, imageBeanList);
        rvImageChooseList.setAdapter(chooseListAdapter);
        chooseListAdapter.setOnChooseImgChangeListener(new ImageChooseListAdapter.OnChooseImgChangeListener() {
            @Override
            public void onChooseImgChangeListener(IImageBean imageBean, int position) {
                int configMaxSize = MNImagePicker.getInstance().getConfig().getPhotoMaxSize();
                if (configMaxSize > 0) {
                    toolbarCenterTitle.setText(title + " " + imageBeanList.size() + "/" + configMaxSize);
                } else {
                    int commonMaxSize = MNImagePicker.getInstance().getPickerCommonConfig().getPhotoMaxSize();
                    if (commonMaxSize > 0) {
                        toolbarCenterTitle.setText(title + " " + imageBeanList.size() + "/" + commonMaxSize);
                    } else {
                        toolbarCenterTitle.setText(title + " " + imageBeanList.size());
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                imageBeanList = data.getParcelableArrayListExtra(RESULT_IMG_LIST);
                chooseListAdapter.refreshData(imageBeanList);
                int configMaxSize = MNImagePicker.getInstance().getConfig().getPhotoMaxSize();
                if (configMaxSize > 0) {
                    toolbarCenterTitle.setText(title + " " + imageBeanList.size() + "/" + configMaxSize);
                } else {
                    int commonMaxSize = MNImagePicker.getInstance().getPickerCommonConfig().getPhotoMaxSize();
                    if (commonMaxSize > 0) {
                        toolbarCenterTitle.setText(title + " " + imageBeanList.size() + "/" + commonMaxSize);
                    } else {
                        toolbarCenterTitle.setText(title + " " + imageBeanList.size());
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) imageBeanList);
        setResult(RESULT_OK, intent);
        super.onBackPressed();

    }
}
