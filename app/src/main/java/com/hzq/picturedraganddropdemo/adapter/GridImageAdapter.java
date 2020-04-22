package com.hzq.picturedraganddropdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hzq.picturedraganddropdemo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 小强
 * @time 2020/4/20  09:00
 * @desc 图片选择适配器
 */

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder> {

    public static final int TYPE_CAMERA  = 1;
    public static final int TYPE_PICTURE = 2;

    private LayoutInflater        mInflater;
    private List<String>          list      = new ArrayList<>();
    private int                   selectMax = 9;
    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    public interface onAddPicClickListener {
        void onAddPicClick ();
    }

    /**
     * 删除
     */
    public void delete (int position) {
        try {

            if (position != RecyclerView.NO_POSITION && list.size() > position) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GridImageAdapter (Context context, onAddPicClickListener mOnAddPicClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax (int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList (List<String> list) {
        this.list = list;
    }

    public List<String> getData () {
        return list == null ? new ArrayList<>() : list;
    }

    public void remove (int position) {
        if (list != null) {
            list.remove(position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView mIvDel;

        public ViewHolder (View view) {
            super(view);
            mImg = view.findViewById(R.id.iv_image);
            mIvDel = view.findViewById(R.id.iv_del);
        }
    }

    @Override
    public int getItemCount () {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType (int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_grid_filter_image, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    private boolean isShowAddItem (int position) {
        int size = list.size() == 0 ? 0 : list.size();
        return position == size;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder (final ViewHolder viewHolder, final int position) {
        //少于8张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mImg.setImageResource(R.mipmap.icon_add_image);
            viewHolder.mImg.setOnClickListener(v -> mOnAddPicClickListener.onAddPicClick());
            viewHolder.mIvDel.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.mIvDel.setVisibility(View.VISIBLE);
            viewHolder.mIvDel.setOnClickListener(view -> {
                int index = viewHolder.getAdapterPosition();
                // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                if (index != RecyclerView.NO_POSITION && list.size() > index) {
                    list.remove(index);
                    notifyItemRemoved(index);
                    notifyItemRangeChanged(index, list.size());
                }
            });
            String media = list.get(position);

            Glide.with(viewHolder.itemView.getContext())
                    .load(media)
                    .centerCrop()
                    .placeholder(R.mipmap.icon_production)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.mImg);


            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(v -> {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    mItemClickListener.onItemClick(v, adapterPosition);
                });
            }

            if (mItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener(v -> {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    mItemLongClickListener.onItemLongClick(viewHolder, adapterPosition, v);
                    return true;
                });
            }
        }
    }

    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener (OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    private OnItemLongClickListener mItemLongClickListener;

    public void setItemLongClickListener (OnItemLongClickListener l) {
        this.mItemLongClickListener = l;
    }


    public interface OnItemClickListener {
        /**
         * item点击事件
         */
        void onItemClick (View v, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick (RecyclerView.ViewHolder holder, int position, View v);
    }
}
