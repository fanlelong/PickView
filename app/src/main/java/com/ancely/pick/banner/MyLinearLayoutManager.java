package com.ancely.pick.banner;

/*
 *  @项目名：  IntegratedPlatform_android 
 *  @包名：    com.vanke.easysale.widget
 *  @文件名:   MyLinearLayoutManager
 *  @创建者:   fanlelong
 *  @创建时间:  2018/4/26 下午5:01
 *  @描述：    TODO
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class MyLinearLayoutManager extends LinearLayoutManager {
    private boolean isHorizontalEnabled;
    private boolean isVerticalEnabled;
    private boolean isScrollEnabled;

    public MyLinearLayoutManager(Context context) {
        super(context);
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setScrollHorizontalEnabled(boolean flag) {
        this.isHorizontalEnabled = flag;
    }

    public void setScrollVerticalEnabled(boolean flag) {
        this.isVerticalEnabled = flag;
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }


    @Override
    public boolean canScrollHorizontally() {
        return !isScrollEnabled && !isHorizontalEnabled && super.canScrollHorizontally();
    }


    @Override
    public boolean canScrollVertically() {
        return !isScrollEnabled && !isVerticalEnabled && super.canScrollVertically();
    }
}
