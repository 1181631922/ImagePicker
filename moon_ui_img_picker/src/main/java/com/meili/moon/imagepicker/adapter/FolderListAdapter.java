package com.meili.moon.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.meili.moon.imagepicker.MNImagePicker;
import com.meili.moon.imagepicker.ibean.impl.ImageFolderBean;
import com.meili.moon.imagepicker.R;
import com.meili.moon.imagepicker.view.MNPickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author： fanyafeng
 * Date： 17/11/17 上午10:43
 * Email: fanyafeng@live.cn
 */
public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderListViewHolder> {

    private Context context;
    private List<ImageFolderBean> imageFolderBeanList;

    private static int DP_80 = 0;

    public FolderListAdapter(Context context, List<ImageFolderBean> imageFolderBeanList) {
        this.context = context;
        this.imageFolderBeanList = imageFolderBeanList;
        DP_80 = (int) dip2px(context, 80);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ImageFolderBean imageFolderBean, int position);
    }

    public void refreshData(List<ImageFolderBean> imageFolderBeanList) {
        if (imageFolderBeanList == null || imageFolderBeanList.size() == 0) {
            this.imageFolderBeanList = new ArrayList<>();
        } else {
            this.imageFolderBeanList = imageFolderBeanList;
        }
        notifyDataSetChanged();
    }

    @Override
    public FolderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder_list_layout, parent, false);
        return new FolderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FolderListViewHolder holder, final int position) {
        final ImageFolderBean imageFolderBean = imageFolderBeanList.get(position);

        MNImagePicker.getInstance().getImageLoadFrame().loadPhoto(context, imageFolderBean.getImageBean().getImgPath(), holder.mnFolderList, DP_80, DP_80);

        holder.tvFolderList.setText(context.getString(R.string.folder) + imageFolderBean.getFolderName());
        holder.sizeFolderList.setText(context.getString(R.string.picture_count) + imageFolderBean.getImageBeanList().size());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, imageFolderBean, position);
                }
            }
        });
        holder.rbFolderList.setChecked(false);
        if (imageFolderBean.isChecked()) {
            holder.rbFolderList.setChecked(true);
        } else {
            holder.rbFolderList.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return imageFolderBeanList.size();
    }

    class FolderListViewHolder extends RecyclerView.ViewHolder {
        TextView tvFolderList;
        RadioButton rbFolderList;
        TextView sizeFolderList;
        MNPickerView mnFolderList;

        public FolderListViewHolder(View itemView) {
            super(itemView);
            mnFolderList = itemView.findViewById(R.id.mnFolderList);
            tvFolderList = itemView.findViewById(R.id.tvFolderList);
            rbFolderList = itemView.findViewById(R.id.rbFolderList);
            sizeFolderList = itemView.findViewById(R.id.sizeFolderList);
        }
    }

    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }
}
