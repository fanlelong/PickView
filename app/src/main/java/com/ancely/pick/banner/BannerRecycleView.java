package com.ancely.pick.banner;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


/*
 *  @项目名：  PickView 
 *  @包名：    com.ancely.pick.banner
 *  @文件名:   BannerRecycleView
 *  @创建者:   fanlelong
 *  @创建时间:  2018/6/13 下午6:06
 *  @描述：    TODO
 */

public class BannerRecycleView extends RecyclerView {
    public BannerRecycleView(Context context) {
        super(context);
    }

    public BannerRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);

    }
}
