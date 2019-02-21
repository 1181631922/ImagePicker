package com.meili.moon.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.ibean.IImageBean;
import com.meili.moon.imagepicker.view.MNPickerView;

import java.util.List;

/**
 * Author： fanyafeng
 * Date： 18/7/6 下午6:52
 * Email: fanyafeng@live.cn
 */
public class ImageChooseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int ITEM_TYPE_ADD = 2000;
    private final static int ITEM_TYPE_PICTURE = 3000;

    private static int SCREEN_WIDTH;
    private static int ONE_DP;
    private static RelativeLayout.LayoutParams LAYOUTPARAMS;
    private boolean isShowAdd = true;

    private Context context;
    private List<IImageBean> imageBeanList;

    private OnChooseImgChangeListener onChooseImgChangeListener;

    public void setOnChooseImgChangeListener(OnChooseImgChangeListener onChooseImgChangeListener) {
        this.onChooseImgChangeListener = onChooseImgChangeListener;
    }

    public interface OnChooseImgChangeListener {
        void onChooseImgChangeListener(IImageBean imageBean, int position);
    }

    public ImageChooseListAdapter(Context context, List<IImageBean> imageBeanList) {
        this.context = context;
        this.imageBeanList = imageBeanList;
        SCREEN_WIDTH = getScreenWidth(context);
        ONE_DP = (int) dip2px(context, 1);
        LAYOUTPARAMS = new RelativeLayout.LayoutParams(SCREEN_WIDTH / 4, SCREEN_WIDTH / 4);
        LAYOUTPARAMS.setMargins(0, (int) (0.9 * ONE_DP), 0, 0);
    }

    public void refreshData(List<IImageBean> imageBeanList) {
        this.imageBeanList = imageBeanList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_PICTURE:
                View viewPicture = LayoutInflater.from(context).inflate(R.layout.mn_item_picker_list_layout, parent, false);
                return new ImageChooseListViewHolder(viewPicture);
            case ITEM_TYPE_ADD:
                View viewAdd = LayoutInflater.from(context).inflate(R.layout.mn_item_picker_add_layout, parent, false);
                return new AddPictureViewHolder(viewAdd);
        }
        View viewPicture = LayoutInflater.from(context).inflate(R.layout.mn_item_picker_list_layout, parent, false);
        return new ImageChooseListViewHolder(viewPicture);
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAdd) {
            return position == 0 ? ITEM_TYPE_ADD : ITEM_TYPE_PICTURE;
        } else {
            return ITEM_TYPE_PICTURE;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ImageChooseListViewHolder) {
            final IImageBean imageBean = imageBeanList.get(isShowAdd ? position - 1 : position);
            ImageChooseListViewHolder viewHolder = (ImageChooseListViewHolder) holder;


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
            viewHolder.mnItemPickerList.setHasCancel(true);
            viewHolder.mnItemPickerList.setCanOperate(true);

            viewHolder.mnItemPickerList.setOnOperateListener(new MNPickerView.OnOperateListener() {
                @Override
                public void OnStatusListener() {
                    // TODO: 18/7/6 执行删除操作
                    if (imageBeanList.contains(imageBean)) {
                        imageBeanList.remove(imageBean);
                        if (onChooseImgChangeListener != null) {
                            onChooseImgChangeListener.onChooseImgChangeListener(imageBean, position);
                        }
                        notifyDataSetChanged();
                    }
                }
            });
        } else if (holder instanceof AddPictureViewHolder) {
            AddPictureViewHolder viewHolder = (AddPictureViewHolder) holder;
            viewHolder.layoutItemPickerCamera.setPadding(0, 0, 1 * ONE_DP, 0);

            viewHolder.layoutItemPickerCamera.setLayoutParams(LAYOUTPARAMS);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    MLImagePicker.getInstance().startPictureChoose((Activity) context, imageBeanList, 100);
                    MNImagePicker.getInstance().photoPickForMulti(context,imageBeanList,100);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return isShowAdd ? imageBeanList.size() + 1 : imageBeanList.size();
    }

    class ImageChooseListViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutItemPickerList;
        private MNPickerView mnItemPickerList;

        public ImageChooseListViewHolder(View itemView) {
            super(itemView);
            layoutItemPickerList = itemView.findViewById(R.id.layoutItemPickerList);
            mnItemPickerList = itemView.findViewById(R.id.mnItemPickerList);
        }
    }

    class AddPictureViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutItemPickerCamera;
        MNPickerView mnItemPickerCamera;

        public AddPictureViewHolder(View itemView) {
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
        return (dipValue * scale + 0.5f);
    }
}
