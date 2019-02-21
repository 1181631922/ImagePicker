package com.meili.moon.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.ibean.impl.ImageBean;
import com.meili.moon.imagepicker.ibean.impl.PickerTitleBean;
import com.meili.moon.imagepicker.property.RoundingParams;
import com.meili.moon.imagepicker.util.StringUtil;
import com.meili.moon.imagepicker.view.MNPickerView;

import java.util.List;

/**
 * Author： fanyafeng
 * Date： 18/7/4 下午4:17
 * Email: fanyafeng@live.cn
 */
public class PickerTitleListAdapter extends RecyclerView.Adapter<PickerTitleListAdapter.PickerTitleListViewHolder> {
    private final static String TAG = PickerTitleListAdapter.class.getSimpleName();

    private Context context;
    private List<PickerTitleBean> beanList;

    private static int DP_60;

    private PickerTitleBean currentBean = null;
    private Boolean hasTitleList;

    private RoundingParams roundingParams;

    public void setCurrentBean(PickerTitleBean currentBean) {
        this.currentBean = currentBean;
        notifyDataSetChanged();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, PickerTitleBean pickerTitleBean, int position);
    }

    private OnChooseImgChangeListener onChooseImgChangeListener;

    public void setOnChooseImgChangeListener(OnChooseImgChangeListener onChooseImgChangeListener) {
        this.onChooseImgChangeListener = onChooseImgChangeListener;
    }

    public interface OnChooseImgChangeListener {
        void onChooseImgChangeListener(ImageBean imageBean, int position);
    }

    public PickerTitleListAdapter(Context context, List<PickerTitleBean> beanList, boolean hasTitleList) {
        this.context = context;
        this.beanList = beanList;
        this.hasTitleList = hasTitleList;
        DP_60 = (int) dip2px(context, 60);
        roundingParams = new RoundingParams().setCornersRadius(dip2px(context, 6));
    }

    public void refreshData(List<PickerTitleBean> titleBeanList) {
        this.beanList = titleBeanList;
        notifyDataSetChanged();
    }

    @Override
    public PickerTitleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mn_item_picker_title_list_layout, parent, false);
        return new PickerTitleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PickerTitleListViewHolder holder, final int position) {
        final PickerTitleBean bean = beanList.get(position);
        if (!StringUtil.isNullOrEmpty(bean.getTitle())) {
            holder.mnPickerTitleItemTitle.setText(bean.getTitle());
        } else {
            holder.mnPickerTitleItemTitle.setText("");
        }
        MNImagePicker.getInstance().getImageLoadFrame().loadPhoto(context, bean.getImgPath(), roundingParams, holder.mnPickerTitleItem, DP_60, DP_60);

        if (bean.equals(currentBean)) {
            holder.layoutPickerTitleItem.setBackgroundResource(R.drawable.picker_title_item_shape);
        } else {
            holder.layoutPickerTitleItem.setBackgroundResource(R.drawable.picker_title_item_shape_no_select);
        }

        holder.mnPickerTitleItemTitle.setVisibility(hasTitleList ? View.VISIBLE : View.GONE);

        holder.mnPickerTitleRemove.setVisibility(View.INVISIBLE);
        if (StringUtil.isNullOrEmpty(bean.getImgPath())) {
            holder.mnPickerTitleRemove.setVisibility(View.INVISIBLE);
        } else {
            holder.mnPickerTitleRemove.setVisibility(View.VISIBLE);
            holder.mnPickerTitleRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasTitleList) {
                        bean.cloneFromImageBean(new ImageBean());
                        if (onChooseImgChangeListener != null) {
                            onChooseImgChangeListener.onChooseImgChangeListener(bean.getImageBean(), position);
                        }
                    } else {
                        // FIXME: 18/7/18  
                        MNImagePicker.getInstance().getImageList().remove(bean.getImageBean());
                        if (onChooseImgChangeListener != null) {
                            onChooseImgChangeListener.onChooseImgChangeListener(null, position);
                        }
                    }
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(v, bean, position);
                    Log.e(TAG, bean.toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return beanList.size();
    }

    class PickerTitleListViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutPickerTitleItem;
        MNPickerView mnPickerTitleItem;
        TextView mnPickerTitleItemTitle;
        ImageView mnPickerTitleRemove;

        public PickerTitleListViewHolder(View itemView) {
            super(itemView);
            layoutPickerTitleItem = itemView.findViewById(R.id.layoutPickerTitleItem);
            mnPickerTitleItem = itemView.findViewById(R.id.mnPickerTitleItem);
            mnPickerTitleItemTitle = itemView.findViewById(R.id.mnPickerTitleItemTitle);
            mnPickerTitleRemove = itemView.findViewById(R.id.mnPickerTitleRemove);
        }
    }

    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }
}
