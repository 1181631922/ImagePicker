package com.meili.moon.imagepicker.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.ScanImageSource;
import com.meili.moon.imagepicker.adapter.PickerListAdapter;
import com.meili.moon.imagepicker.adapter.PickerTitleListAdapter;
import com.meili.moon.imagepicker.ibean.impl.ImageBean;
import com.meili.moon.imagepicker.ibean.impl.ImageFolderBean;
import com.meili.moon.imagepicker.ibean.impl.PickerTitleBean;
import com.meili.moon.imagepicker.util.ImagePickerUtil;
import com.meili.moon.imagepicker.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static com.meili.moon.imagepicker.ui.MLImageListActivity.RESULT_IMG_LIST;
import static com.meili.moon.imagepicker.util.ConstantsUtil.RESULT_PICKER_IMG_LIST;

public class MNImagePickerActivity extends ImageBaseActivity implements ScanImageSource.OnImageLoadedListener {
    private final static String TAG = ImageBaseActivity.class.getSimpleName();

    private final static int REQUEST_CODE_CAMERA_AND_STORAGE = 1000;

    private final static int REQUEST_CODE_CAMERA = 1001;

    private final static int REQUEST_CODE_STORAGE = 1002;

    public final static int REQUEST_CODE_GET_CAMERA_IMG = 3428;

    /**
     * 顶部图片列表view
     * <p>
     * 顶部列表结果即为回传结果
     */
    private RecyclerView pickerTitleList;

    /**
     * 顶部列表adapter的list
     * <p>
     * 只维护一份此list
     */
    private List<PickerTitleBean> titleBeanList = new ArrayList<>();

    /**
     * 顶部滚动view adapter
     */
    private PickerTitleListAdapter titleListAdapter;

    /**
     * 下方图片列表view
     * <p>
     * 用来显示手机所有图片
     */
    private RecyclerView imagePickerList;

    /**
     * 下方图片列表adapter的list
     * <p>
     * 除去tag中的内容其余属性均不可替换
     */
    private List<ImageBean> imageBeanList = new ArrayList<>();

    /**
     * 下方图片列表view 适配器
     */
    private PickerListAdapter pickerListAdapter;

    /**
     * 当前顶部视图被选中的图片
     * <p>
     * 点击后图片列表可定位到屏幕可视位置
     * 如果当前图片未选中，选中后可跳转到下个position
     */
    private ImageBean currentImageBean;

    /**
     * 当前用户选择的顶部view的position
     * <p>
     * 默认为0
     */
    private int tabPosition = 0;

    /**
     * 判断是否有列表头
     * true->多类目图片
     * false->单类目单张图片，无图片标题
     */
    private boolean hasTitleList = true;

    /**
     * 图片列表是否有相机选项
     * 用来确认次级页面是否添加拍照功能
     * true->次级页面有拍照icon，并且item的position默认+1
     * false->次级页面无拍照icon，position可以直接用
     */
    private boolean isShowCamera = true;

    protected TextView toolbarCenterTitle;
    protected TextView toolbarRightTitle;

    /**
     * 图片张数显示
     */
    private TextView tvCount;

    private ScanImageSource mScanImageSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mn_activity_image_picker);

        if (getIntent().getParcelableArrayListExtra("titleBeanList") != null) {
            /**
             * 多类目单张
             *
             * 需要从前一个页面接收一个pickertitle列表，
             * 必须要有position，用户选择时执行回调
             */
            hasTitleList = true;
            titleBeanList = getIntent().getParcelableArrayListExtra("titleBeanList");
            tabPosition = getIntent().getIntExtra("tabPosition", -1);
        } else {
            /**
             * 单张多类目
             */
            hasTitleList = false;
            List<ImageBean> imageBeans = getIntent().getParcelableArrayListExtra("imageBeanList");
            if (imageBeans != null && imageBeans.size() > 0) {
                MNImagePicker.getInstance().getImageList().addAll(imageBeans);
            }
        }

        initView();
        requestMediaPermission();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        tvCount = findViewById(R.id.tvCount);
        tvCount.setVisibility(hasTitleList ? View.GONE : View.VISIBLE);
        tvCount.setText(MNImagePicker.getInstance().getImageList().size() + "");

        toolbarCenterTitle = findViewById(R.id.toolbarCenterTitle);
        toolbarCenterTitle.setText("图片");
        if (hasTitleList) {
            toolbarCenterTitle.setText(titleBeanList.get(tabPosition).getTitle());
        }
        toolbarRightTitle = findViewById(R.id.toolbarRightTitle);

        pickerTitleList = findViewById(R.id.mnImagePickerTitleList);
        imagePickerList = findViewById(R.id.mnImagePickerList);
        imagePickerList.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));

        toolbarRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 用户点击取消按钮，结果不回传
                 */
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                ImagePickerUtil.clearConfig();
                finish();
            }
        });
    }

    private void requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_AND_STORAGE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            } else {
                operateData();
            }
        } else {
            operateData();
        }
    }

    private void operateData() {
        mScanImageSource = new ScanImageSource(this, null, this);
        initData();
    }

    private void initData() {
        initPickerListData();

        initPickerTitleData();

        findViewById(R.id.layoutSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (hasTitleList) {
                    intent.putParcelableArrayListExtra(RESULT_PICKER_IMG_LIST, (ArrayList<? extends Parcelable>) titleBeanList);
                    MNImagePicker.getInstance().getImageTitleBeanList().addAll(titleBeanList);
                    if (MNImagePicker.getInstance().imageTitleListChangeListener != null) {
                        MNImagePicker.getInstance().imageTitleListChangeListener.onImageTitleListChangeListener(MNImagePicker.getInstance().getImageTitleBeanList());
                        MNImagePicker.getInstance().imageTitleListChangeListener = null;
                    }
                    setResult(RESULT_OK, intent);
                } else {
                    if (MNImagePicker.getInstance().imageListChangeListener != null) {
                        MNImagePicker.getInstance().imageListChangeListener.onImageListChangeListener(MNImagePicker.getInstance().getImageList());
                        MNImagePicker.getInstance().imageListChangeListener = null;
                    }
                    intent.putParcelableArrayListExtra(RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MNImagePicker.getInstance().getImageList());
                    setResult(RESULT_OK, intent);
                }
                ImagePickerUtil.clearConfig();
                finish();
            }
        });
    }

    private void initPickerListData() {
        pickerListAdapter = new PickerListAdapter(this, imageBeanList, hasTitleList);
        imagePickerList.setAdapter(pickerListAdapter);

        if (hasTitleList) {
            List<ImageBean> imageBeans = new ArrayList<>();
            for (int i = 0; i < titleBeanList.size(); i++) {
                imageBeans.add(titleBeanList.get(i).getImageBean());
            }
            pickerListAdapter.setCurrentBeanList(imageBeans);
        }


        pickerListAdapter.setOnChooseImgChangeListener(new PickerListAdapter.OnChooseImgChangeListener() {
            @Override
            public void onChooseImgChangeListener(ImageBean imageBean, final int position, boolean isDel) {
                if (!isDel) {
                    if (imageBean.getImgPath() != null) {
                        if (hasTitleList) {
                            // TODO: 18/7/5 图片title更新
                            titleBeanList.get(tabPosition).cloneFromImageBean(imageBean);

                            toolbarCenterTitle.setText(titleBeanList.get(tabPosition).getTitle());

                            titleListAdapter.setCurrentBean(titleBeanList.get(tabPosition));
                            titleListAdapter.notifyDataSetChanged();

                            List<ImageBean> imageBeans = new ArrayList<>();
                            for (int i = 0; i < titleBeanList.size(); i++) {
                                imageBeans.add(titleBeanList.get(i).getImageBean());
                            }

                            pickerListAdapter.setCurrentBeanList(imageBeans);

                            Intent intent = new Intent();
                            intent.putParcelableArrayListExtra(RESULT_PICKER_IMG_LIST, (ArrayList<? extends Parcelable>) titleBeanList);
                            setResult(RESULT_OK, intent);

                            currentImageBean = imageBean;
                            pickerListAdapter.setCurrentBean(currentImageBean);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (tabPosition < titleBeanList.size() - 1) {
                                        tabPosition += 1;
                                        toolbarCenterTitle.setText(titleBeanList.get(tabPosition).getTitle());
                                        titleListAdapter.setCurrentBean(titleBeanList.get(tabPosition));
                                        titleListAdapter.notifyDataSetChanged();

                                        currentImageBean = titleBeanList.get(tabPosition).getImageBean();
                                        pickerListAdapter.setCurrentBean(currentImageBean);

                                        pickerTitleList.smoothScrollToPosition(tabPosition);
                                    }

                                }
                            }, 100);
                        } else {
                            titleBeanList.clear();
                            for (int i = 0; i < MNImagePicker.getInstance().getImageList().size(); i++) {
                                PickerTitleBean bean = new PickerTitleBean();
                                bean.cloneFromImageBean(MNImagePicker.getInstance().getImageList().get(i));
                                bean.setPosition(i);
                                titleBeanList.add(bean);
                            }
                            titleListAdapter.refreshData(titleBeanList);


                            List<ImageBean> imageBeans = new ArrayList<>();
                            for (int i = 0; i < titleBeanList.size(); i++) {
                                imageBeans.add(titleBeanList.get(i).getImageBean());
                            }
                            pickerListAdapter.setCurrentBeanList(imageBeans);
                        }
                    }
                } else {
                    if (hasTitleList) {
                        for (int i = 0; i < titleBeanList.size(); i++) {
                            ImageBean copyBean = titleBeanList.get(i).getImageBean();
                            if (!TextUtils.isEmpty(imageBean.getImgPath())) {
                                if (imageBean.getImgPath().equals(copyBean.getImgPath())) {
                                    titleBeanList.get(i).cloneFromImageBean(new ImageBean());
                                    final int j = i;
                                    titleListAdapter.notifyDataSetChanged();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            tabPosition = j;
                                            toolbarCenterTitle.setText(titleBeanList.get(tabPosition).getTitle());
                                            titleListAdapter.setCurrentBean(titleBeanList.get(j));
                                        }
                                    }, 100);

                                    break;
                                }
                            }
                        }
                        List<ImageBean> imageBeans = new ArrayList<>();
                        for (int i = 0; i < titleBeanList.size(); i++) {
                            imageBeans.add(titleBeanList.get(i).getImageBean());
                        }
                        pickerListAdapter.setCurrentBean(new ImageBean());
                        pickerListAdapter.setCurrentBeanList(imageBeans);
                    } else {
                        List<ImageBean> imageBeans = new ArrayList<>();
                        for (int i = 0; i < titleBeanList.size(); i++) {
                            ImageBean copyBean = titleBeanList.get(i).getImageBean();
                            if (!TextUtils.isEmpty(imageBean.getImgPath())) {
                                if (!imageBean.getImgPath().equals(copyBean.getImgPath())) {
                                    imageBeans.add(titleBeanList.get(i).getImageBean());
                                }
                            }
                        }
                        pickerListAdapter.setCurrentBean(new ImageBean());
                        pickerListAdapter.setCurrentBeanList(imageBeans);

                        titleBeanList.clear();
                        for (int i = 0; i < MNImagePicker.getInstance().getImageList().size(); i++) {
                            PickerTitleBean bean = new PickerTitleBean();
                            bean.cloneFromImageBean(MNImagePicker.getInstance().getImageList().get(i));
                            bean.setPosition(i);
                            titleBeanList.add(bean);
                        }
                        titleListAdapter.refreshData(titleBeanList);
                    }
                }

                tvCount.setText(MNImagePicker.getInstance().getImageList().size() + "");
            }
        });
    }

    private void initPickerTitleData() {
        titleListAdapter = new PickerTitleListAdapter(this, titleBeanList, hasTitleList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        pickerTitleList.setLayoutManager(linearLayoutManager);
        pickerTitleList.setAdapter(titleListAdapter);

        if (hasTitleList) {
            currentImageBean = titleBeanList.get(tabPosition).getImageBean();
            toolbarCenterTitle.setText(titleBeanList.get(tabPosition).getTitle());
            titleListAdapter.setCurrentBean(titleBeanList.get(tabPosition));
            pickerTitleList.smoothScrollToPosition(tabPosition);
        } else {
            titleBeanList.clear();
            for (int i = 0; i < MNImagePicker.getInstance().getImageList().size(); i++) {
                PickerTitleBean bean = new PickerTitleBean();
                bean.cloneFromImageBean(MNImagePicker.getInstance().getImageList().get(i));
                bean.setPosition(i);
                titleBeanList.add(bean);
            }
            titleListAdapter.refreshData(titleBeanList);
        }

        pickerListAdapter.setCurrentBean(currentImageBean);

        titleListAdapter.notifyDataSetChanged();

        titleListAdapter.setOnItemClickListener(new PickerTitleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, PickerTitleBean pickerTitleBean, final int position) {
                tabPosition = position;
                if (hasTitleList) {
                    toolbarCenterTitle.setText(titleBeanList.get(tabPosition).getTitle());
                }
                titleListAdapter.setCurrentBean(pickerTitleBean);

                // TODO: 18/7/5 刷新图片详情列表
                currentImageBean = pickerTitleBean.getImageBean();
                pickerListAdapter.setCurrentBean(currentImageBean);

                List<ImageBean> imageBeans = new ArrayList<>();
                for (int i = 0; i < titleBeanList.size(); i++) {
                    imageBeans.add(titleBeanList.get(i).getImageBean());
                }

                pickerListAdapter.setCurrentBeanList(imageBeans);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pickerTitleList.smoothScrollToPosition(position);
                    }
                }, 100);

                // TODO: 18/7/5 刷新图片详情列表的同时需要将选中图片滚动到第一屏
                for (int i = 0; i < imageBeanList.size(); i++) {
                    final int j = i;
                    ImageBean imageBean = pickerTitleBean.getImageBean();
                    if (imageBean.getImgPath() != null && imageBean.getImgCreateTime() != 0) {
                        if (pickerTitleBean.getImageBean().equals(imageBeanList.get(i))) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imagePickerList.scrollToPosition(isShowCamera ? j + 1 : j);
                                }
                            }, 100);

                            return;
                        }
                    }
                }

            }
        });

        //删除按钮
        titleListAdapter.setOnChooseImgChangeListener(new PickerTitleListAdapter.OnChooseImgChangeListener() {
            @Override
            public void onChooseImgChangeListener(ImageBean imageBean, final int position) {
                tabPosition = position;
                if (hasTitleList) {
                    toolbarCenterTitle.setText(titleBeanList.get(tabPosition).getTitle());
                }
                if (hasTitleList) {
                    titleBeanList.get(position).cloneFromImageBean(imageBean);

                    titleListAdapter.notifyDataSetChanged();

                    List<ImageBean> imageBeans = new ArrayList<>();
                    for (int i = 0; i < titleBeanList.size(); i++) {
                        imageBeans.add(titleBeanList.get(i).getImageBean());
                    }

                    pickerListAdapter.setCurrentBeanList(imageBeans);
                    pickerListAdapter.setCurrentBean(new ImageBean());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            titleListAdapter.setCurrentBean(titleBeanList.get(position));
                        }
                    }, 100);
                } else {
                    List<ImageBean> imageBeans = new ArrayList<>();
                    for (int i = 0; i < titleBeanList.size(); i++) {
                        if (!StringUtil.isNullOrEmpty(titleBeanList.get(i).getImgPath())) {
                            imageBeans.add(titleBeanList.get(i).getImageBean());
                        }
                    }
                    pickerListAdapter.setCurrentBeanList(imageBeans);


                    titleBeanList.clear();
                    for (int i = 0; i < MNImagePicker.getInstance().getImageList().size(); i++) {
                        PickerTitleBean bean = new PickerTitleBean();
                        bean.cloneFromImageBean(MNImagePicker.getInstance().getImageList().get(i));
                        bean.setPosition(i);
                        titleBeanList.add(bean);
                    }
                    titleListAdapter.refreshData(titleBeanList);

                    tvCount.setText(MNImagePicker.getInstance().getImageList().size() + "");
                }
            }
        });
    }


    @Override
    public void onImageLoaded(List<ImageFolderBean> imageFolderBeanList) {
        if (imageFolderBeanList.size() == 0) {
            // TODO: 17/11/16 文件夹为0
        } else {
            Log.d(TAG, "imageFolderBeanListSize:" + imageFolderBeanList.size());
            Log.d(TAG, "imageFolderBeanList:" + imageFolderBeanList.get(0).toString());
            imagePickerList = findViewById(R.id.mnImagePickerList);
            imagePickerList.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
            imageBeanList = imageFolderBeanList.get(0).getImageBeanList();

            if (isGetCameraPicture) {
                if (hasTitleList) {
                    currentImageBean.setImgCreateTime(imageBeanList.get(0).getImgCreateTime());
                    pickerListAdapter.setCurrentBean(currentImageBean);
                    titleBeanList.get(tabPosition).cloneFromImageBean(currentImageBean);
                    toolbarCenterTitle.setText(titleBeanList.get(tabPosition).getTitle());

                    List<ImageBean> imageBeans = new ArrayList<>();
                    for (int i = 0; i < titleBeanList.size(); i++) {
                        imageBeans.add(titleBeanList.get(i).getImageBean());
                    }
                    pickerListAdapter.setCurrentBeanList(imageBeans);
                } else {
                    int size = MNImagePicker.getInstance().getImageList().size();
                    MNImagePicker.getInstance().getImageList().get(size - 1).setImgCreateTime(imageBeanList.get(0).getImgCreateTime());

                    titleBeanList.clear();
                    for (int i = 0; i < MNImagePicker.getInstance().getImageList().size(); i++) {
                        PickerTitleBean bean = new PickerTitleBean();
                        bean.cloneFromImageBean(MNImagePicker.getInstance().getImageList().get(i));
                        bean.setPosition(i);
                        titleBeanList.add(bean);
                    }
                    titleListAdapter.refreshData(titleBeanList);
                }
                isGetCameraPicture = false;
            }
            pickerListAdapter.refreshData(imageBeanList);

            if (hasTitleList) {
                for (int i = 0; i < imageBeanList.size(); i++) {
                    if (imageBeanList.get(i).equals(currentImageBean)) {
                        imagePickerList.scrollToPosition(isShowCamera ? i + 1 : i);
                        return;
                    }
                }
            }

            List<ImageBean> imageBeans = new ArrayList<>();
            for (int i = 0; i < titleBeanList.size(); i++) {
                imageBeans.add(titleBeanList.get(i).getImageBean());
            }
            pickerListAdapter.setCurrentBeanList(imageBeans);


        }
    }

    //判断图片是不是拍摄所得，广播需要时间刷新图片
    boolean isGetCameraPicture = false;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GET_CAMERA_IMG) {
                isGetCameraPicture = true;
                // TODO: 18/7/6 替换掉当前的图片并且勾选
                if (hasTitleList) {

                    currentImageBean.setImgPath(MNImagePicker.getInstance().getTakeImageFile().getAbsolutePath());
                    currentImageBean.setImgCreateTime(MNImagePicker.getInstance().getTakeImageFile().lastModified());
                    Log.e("createTime", "创建时间：" + MNImagePicker.getInstance().getTakeImageFile().lastModified());
                    pickerListAdapter.notifyDataSetChanged();

                    titleBeanList.get(tabPosition).cloneFromImageBean(currentImageBean);
                    toolbarCenterTitle.setText(titleBeanList.get(tabPosition).getTitle());
                    titleListAdapter.notifyDataSetChanged();

                } else {

                    ImageBean getCameraImageBean = new ImageBean();
                    getCameraImageBean.setImgPath(MNImagePicker.getInstance().getTakeImageFile().getAbsolutePath());
                    MNImagePicker.getInstance().getImageList().add(getCameraImageBean);
                    tvCount.setText(MNImagePicker.getInstance().getImageList().size() + "");
                    Log.e("createTime", "创建时间：" + MNImagePicker.getInstance().getTakeImageFile().lastModified());
                    pickerListAdapter.notifyDataSetChanged();
                }

                //发送扫描广播会有延时
                ImagePickerUtil.galleryAddPic(this, MNImagePicker.getInstance().getTakeImageFile());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_AND_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    operateData();
                } else {
                    Toast.makeText(MNImagePickerActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ImagePickerUtil.clearConfig();
                    finish();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    operateData();
                } else {
                    Toast.makeText(MNImagePickerActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ImagePickerUtil.clearConfig();
                    finish();
                }
                break;
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    operateData();
                } else {
                    Toast.makeText(MNImagePickerActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ImagePickerUtil.clearConfig();
                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (hasTitleList) {
            intent.putParcelableArrayListExtra(RESULT_PICKER_IMG_LIST, (ArrayList<? extends Parcelable>) titleBeanList);
            setResult(RESULT_OK, intent);
        } else {
            intent.putParcelableArrayListExtra(RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MNImagePicker.getInstance().getImageList());
            setResult(RESULT_OK, intent);
        }
        ImagePickerUtil.clearConfig();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing() && mScanImageSource != null) {
            mScanImageSource.recycle();
            mScanImageSource = null;
        }
    }
}
