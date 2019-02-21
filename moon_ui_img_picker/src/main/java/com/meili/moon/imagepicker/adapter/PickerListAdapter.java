package com.meili.moon.imagepicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.ibean.IImageTitleBean;
import com.meili.moon.imagepicker.ibean.impl.ImageBean;
import com.meili.moon.imagepicker.ibean.impl.PickerTitleBean;
import com.meili.moon.imagepicker.ui.MNImagePickerActivity;
import com.meili.moon.imagepicker.util.ImagePickerUtil;
import com.meili.moon.imagepicker.util.StringUtil;
import com.meili.moon.imagepicker.view.MNPickerView;
import com.meili.moon.ui.dialog.widget.MNDialog;
import com.meili.moon.ui.dialog.widget.MNToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Author： fanyafeng
 * Date： 18/7/4 下午5:28
 * Email: fanyafeng@live.cn
 */
public class PickerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = PickerListAdapter.class.getSimpleName();

    private final static int ITEM_TYPE_CAMERT = 2000;
    private final static int ITEM_TYPE_PICTURE = 3000;

    private static int SCREEN_WIDTH;
    private static int ONE_DP;
    private static RelativeLayout.LayoutParams LAYOUTPARAMS;
    private boolean isShowCamera = true;
    private int maxSize = 10;

    private OnChooseImgChangeListener onChooseImgChangeListener;

    public void setOnChooseImgChangeListener(OnChooseImgChangeListener onChooseImgChangeListener) {
        this.onChooseImgChangeListener = onChooseImgChangeListener;
    }

    public interface OnChooseImgChangeListener {
        void onChooseImgChangeListener(ImageBean imageBean, int position, boolean isDel);
    }

    public void notifyItemChange(int position) {
        notifyItemChanged(isShowCamera ? position + 1 : position);
    }

    public void refreshData(List<ImageBean> imageBeanList) {
        if (imageBeanList == null || imageBeanList.size() == 0) {
            this.imageBeanList = new ArrayList<>();
        } else {
            this.imageBeanList = imageBeanList;
        }
        notifyDataSetChanged();
    }

    private ImageBean currentBean;

    public void setCurrentBean(ImageBean currentBean) {
        this.currentBean = currentBean;
        if (currentBean != null && !StringUtil.isNullOrEmpty(currentBean.getImgPath())) {
            int notifyPosition = imageBeanList.indexOf(currentBean);
            notifyItemChange(notifyPosition);
        }
    }

    private List<ImageBean> imageBeans = new ArrayList<>();

    public void setCurrentBeanList(List<ImageBean> imageBeans) {
        for (int i = 0; i < this.imageBeans.size(); i++) {
            ImageBean imageBean = this.imageBeans.get(i);
            if (currentBean != null && !StringUtil.isNullOrEmpty(imageBean.getImgPath())) {
                int notifyPosition = imageBeanList.indexOf(imageBean);
                this.imageBeans.get(i).setImgPath("");
                notifyItemChange(notifyPosition);
            }
        }
        this.imageBeans = imageBeans;
    }


    private Context context;
    private List<ImageBean> imageBeanList;
    private Boolean hasTitleList;

    public PickerListAdapter(Context context, List<ImageBean> imageBeanList, boolean hasTitleList) {
        this.context = context;
        this.imageBeanList = imageBeanList;
        this.hasTitleList = hasTitleList;
        init(context);
    }

    private void init(Context context) {
        currentBean = new ImageBean();
        SCREEN_WIDTH = getScreenWidth(context);
        ONE_DP = (int) dip2px(context, 1);
        LAYOUTPARAMS = new RelativeLayout.LayoutParams(SCREEN_WIDTH / 4, SCREEN_WIDTH / 4);
        LAYOUTPARAMS.setMargins(0, (int) (0.9 * ONE_DP), 0, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_CAMERT:
                View viewCamera = LayoutInflater.from(context).inflate(R.layout.mn_item_picker_camera_layout, parent, false);
                return new CameraViewHolder(viewCamera);
            case ITEM_TYPE_PICTURE:
                View viewPicture = LayoutInflater.from(context).inflate(R.layout.mn_item_picker_list_layout, parent, false);
                return new PickerListViewHolder(viewPicture);
        }
        View viewPicture = LayoutInflater.from(context).inflate(R.layout.mn_item_picker_list_layout, parent, false);
        return new PickerListViewHolder(viewPicture);
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) {
            return position == 0 ? ITEM_TYPE_CAMERT : ITEM_TYPE_PICTURE;
        } else {
            return ITEM_TYPE_PICTURE;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PickerListViewHolder) {
            final ImageBean imageBean = imageBeanList.get(isShowCamera ? position - 1 : position);
            final PickerListViewHolder viewHolder = (PickerListViewHolder) holder;


            switch (position % 4) {
                case 0:
                    viewHolder.layoutItemPickerList.setPadding(0, 0, (int) (0.9 * ONE_DP), 0);
                    viewHolder.layoutItemPickerList.setLayoutParams(LAYOUTPARAMS);
                    break;
                case 1:
                    viewHolder.layoutItemPickerList.setPadding((int) (0.3 * ONE_DP), 0, (int) (0.6 * ONE_DP), 0);
                    viewHolder.layoutItemPickerList.setLayoutParams(LAYOUTPARAMS);
                    break;
                case 2:
                    viewHolder.layoutItemPickerList.setPadding((int) (0.6 * ONE_DP), 0, (int) (0.3 * ONE_DP), 0);
                    viewHolder.layoutItemPickerList.setLayoutParams(LAYOUTPARAMS);
                    break;
                case 3:
                    viewHolder.layoutItemPickerList.setPadding((int) (0.9 * ONE_DP), 0, 0, 0);
                    viewHolder.layoutItemPickerList.setLayoutParams(LAYOUTPARAMS);
                    break;
            }

            int width = SCREEN_WIDTH / 4 - ONE_DP;

            MNImagePicker.getInstance().getImageLoadFrame().loadPhoto(context, imageBean.getImgPath(), viewHolder.mnItemPickerList, width, width);

            //必须需要重置，否则复用会出问题
            viewHolder.mnItemPickerList.setCancel(false);
            viewHolder.mnItemPickerList.setHasConfirm(false);
            viewHolder.mnItemPickerList.setHasCancel(false);
            viewHolder.mnItemPickerList.setCanOperate(true);

            if (hasTitleList) {
                if (imageBeans.contains(imageBean)) {
                    viewHolder.mnItemPickerList.setCancel(true);
                    viewHolder.mnItemPickerList.setHasConfirm(true);
                    viewHolder.mnItemPickerList.setShadeColor(Color.parseColor("#80000000"));
                } else {
                    viewHolder.mnItemPickerList.setCancel(false);
                    viewHolder.mnItemPickerList.setHasConfirm(true);
                    viewHolder.mnItemPickerList.setShadeColor(Color.TRANSPARENT);
                }
            } else {
                if (MNImagePicker.getInstance().getImageList().contains(imageBean)) {
                    viewHolder.mnItemPickerList.setCancel(true);
                    viewHolder.mnItemPickerList.setHasConfirm(true);
                    viewHolder.mnItemPickerList.setShadeColor(Color.parseColor("#80000000"));
                } else {
                    viewHolder.mnItemPickerList.setCancel(false);
                    viewHolder.mnItemPickerList.setHasConfirm(true);
                    viewHolder.mnItemPickerList.setShadeColor(Color.TRANSPARENT);
                }
            }

            final PickerListViewHolder finalViewHolder = viewHolder;

            if (!viewHolder.mnItemPickerList.isCancel()) {
                viewHolder.mnItemPickerList.setOnOperateListener(new MNPickerView.OnOperateListener() {
                    @Override
                    public void OnStatusListener() {
                        if (currentBean == null) {
                            currentBean = new ImageBean();
                        }
                        if (hasTitleList) {
                            if ((currentBean.getImgPath() == null || currentBean.getImgPath().equals("")) || currentBean.getImgCreateTime() == 0) {
                                if (onChooseImgChangeListener != null) {
                                    onChooseImgChangeListener.onChooseImgChangeListener(imageBean, position, false);
                                    notifyItemChanged(position);
                                }
                            } else {
                                View view = LayoutInflater.from(context).inflate(R.layout.mn_dialog_layout_center_dialog, null);
                                final MNDialog mnDialog = new MNDialog(context, view);
                                ((TextView) view.findViewById(R.id.text_dialog_title)).setText("是否替换选中图片");
                                view.findViewById(R.id.text_dialog_desc).setVisibility(View.GONE);
                                view.findViewById(R.id.btn_dialog_cancle).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mnDialog.dismiss();
                                    }
                                });
                                view.findViewById(R.id.btn_dialog_confirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (onChooseImgChangeListener != null) {
                                            onChooseImgChangeListener.onChooseImgChangeListener(imageBean, position, false);
                                        }
                                        mnDialog.dismiss();
                                    }
                                });
                                mnDialog.showCenter();
                            }
                        } else {
                            if (!finalViewHolder.mnItemPickerList.isCancel()) {
                                int configMaxSize = MNImagePicker.getInstance().getConfig().getPhotoMaxSize();
                                int currSize = MNImagePicker.getInstance().getImageList().size();
                                if (configMaxSize>0){
                                    if (currSize>=configMaxSize){
                                        MNToast.showCenter(context, "图片已经到达最大张数");
                                    }else {
                                        MNImagePicker.getInstance().getImageList().add(imageBean);
                                    }
                                }else {
                                    int commonMaxSize = MNImagePicker.getInstance().getPickerCommonConfig().getPhotoMaxSize();
                                    if (commonMaxSize>0){
                                        if (currSize>=commonMaxSize){
                                            MNToast.showCenter(context, "图片已经到达最大张数");
                                        }else {
                                            MNImagePicker.getInstance().getImageList().add(imageBean);
                                        }
                                    }else {
                                        MNImagePicker.getInstance().getImageList().add(imageBean);
                                    }
                                }
                            }
                            if (onChooseImgChangeListener != null) {
                                onChooseImgChangeListener.onChooseImgChangeListener(imageBean, position, false);
                            }
                            notifyItemChanged(position);
                        }
                    }
                });
            } else {
                viewHolder.mnItemPickerList.setOnOperateListener(new MNPickerView.OnOperateListener() {
                    @Override
                    public void OnStatusListener() {
                        if (onChooseImgChangeListener != null) {
                            viewHolder.mnItemPickerList.setCancel(false);
                            viewHolder.mnItemPickerList.setHasConfirm(true);
                            viewHolder.mnItemPickerList.setShadeColor(Color.TRANSPARENT);
                            if (!hasTitleList) {
                                MNImagePicker.getInstance().getImageList().remove(imageBean);
                            }
                            onChooseImgChangeListener.onChooseImgChangeListener(imageBean, position, true);
                            notifyItemChanged(position);

                        }
                    }
                });
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<IImageTitleBean> titleBeanList = new ArrayList<>();

                    for (int i = 0; i < imageBeanList.size(); i++) {
                        PickerTitleBean pickerTitleBean = new PickerTitleBean();
                        pickerTitleBean.cloneFromImageBean(imageBeanList.get(i));
                        titleBeanList.add(pickerTitleBean);
                    }

                    MNImagePicker.getInstance().photoPreview((Activity) context, titleBeanList, isShowCamera ? position - 1 : position);
                }
            });
        } else if (holder instanceof CameraViewHolder) {
            CameraViewHolder viewHolder = (CameraViewHolder) holder;
            viewHolder.layoutItemPickerCamera.setPadding(0, 0, ONE_DP, 0);

            viewHolder.layoutItemPickerCamera.setLayoutParams(LAYOUTPARAMS);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 18/7/6 打开相机
                    if (hasTitleList) {
                        if (!StringUtil.isNullOrEmpty(currentBean.getImgPath()) && currentBean.getImgCreateTime() != 0) {
                            View view = LayoutInflater.from(context).inflate(R.layout.mn_dialog_layout_center_dialog, null);
                            final MNDialog mnDialog = new MNDialog(context, view);
                            ((TextView) view.findViewById(R.id.text_dialog_title)).setText("是否替换选中图片");
                            view.findViewById(R.id.text_dialog_desc).setVisibility(View.GONE);
                            view.findViewById(R.id.btn_dialog_cancle).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mnDialog.dismiss();
                                }
                            });
                            view.findViewById(R.id.btn_dialog_confirm).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO: 18/7/5 需要更新title picker list
                                    new ImagePickerUtil().takePicture(context, MNImagePickerActivity.REQUEST_CODE_GET_CAMERA_IMG);
                                    mnDialog.dismiss();
                                }
                            });
                            mnDialog.showCenter();
                        } else {
                            new ImagePickerUtil().takePicture(context, MNImagePickerActivity.REQUEST_CODE_GET_CAMERA_IMG);
                        }
                    } else {
                        new ImagePickerUtil().takePicture(context, MNImagePickerActivity.REQUEST_CODE_GET_CAMERA_IMG);
                    }

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return isShowCamera ? imageBeanList.size() + 1 : imageBeanList.size();
    }

    class PickerListViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutItemPickerList;
        private MNPickerView mnItemPickerList;

        public PickerListViewHolder(View itemView) {
            super(itemView);
            layoutItemPickerList = itemView.findViewById(R.id.layoutItemPickerList);
            mnItemPickerList = itemView.findViewById(R.id.mnItemPickerList);
        }
    }

    class CameraViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutItemPickerCamera;
        MNPickerView mnItemPickerCamera;

        public CameraViewHolder(View itemView) {
            super(itemView);
            layoutItemPickerCamera = itemView.findViewById(R.id.layoutItemPickerCamera);
            mnItemPickerCamera = itemView.findViewById(R.id.mnItemPickerCamera);
        }
    }

    private int getScreenWidth(Context context) {
        if (context != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            return displayMetrics.widthPixels;
        } else {
            return 1080;
        }
    }

    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }
}
