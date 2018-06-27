package com.ancely.pick.banner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {

    private List<Integer> mDatas;
    private Context mContext;

    public BannerAdapter(Context context, List<Integer> list) {
        this.mDatas = list;
        this.mContext = context;
    }

    @Override
    public BannerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BannerHolder(new ImageView(mContext));
    }

    @Override
    public void onBindViewHolder(BannerHolder holder, final int position) {
        if (mDatas == null || mDatas.isEmpty())
            return;
        int res;
        if (position == 0) {
            res = mDatas.get(mDatas.size()-1);
        } else if (position == mDatas.size() + 1) {
            res = mDatas.get(0);
        } else {
            res = mDatas.get(position - 1);
        }
        ImageView img = (ImageView) holder.itemView;
        img.setImageResource(res);
        Glide.with(mContext).load(res).into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (onBannerItemClickListener != null) {
//                    onBannerItemClickListener.onItemClick(position % mDatas.size());
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + 2;
    }

    class BannerHolder extends RecyclerView.ViewHolder {
        ImageView bannerItem;

        public BannerHolder(ImageView itemView) {
            super(itemView);
            bannerItem = (ImageView) itemView;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            bannerItem.setLayoutParams(params);
            bannerItem.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }
}