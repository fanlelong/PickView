package com.ancely.pick.banner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ancely.pick.R;

import java.util.ArrayList;
import java.util.List;


/*
 *  @项目名：  PickView 
 *  @包名：    com.ancely.pick.banner
 *  @文件名:   BannerRecycleView
 *  @创建者:   fanlelong
 *  @创建时间:  2018/6/13 下午6:06
 *  @描述：    TODO
 */

public class BannerRecycleView extends FrameLayout {


    private int mCrrentPos = 1;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private List<Integer> mList;

    protected boolean isPlaying;//是否正在播放
    protected int WHAT_AUTO_PLAY = 1000;
    protected int WHAT_AUTO_PLAY_T = 2000;
    protected boolean isAutoPlaying;//是否自动轮播
    protected Drawable mSelectedDrawable;
    protected Drawable mUnselectedDrawable;


    protected Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                if (mCrrentPos == mList.size()) {
                    mRecyclerView.smoothScrollToPosition(++mCrrentPos);
                    mCrrentPos = 1;
                    mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY_T, 1000);
                } else {
                    mRecyclerView.smoothScrollToPosition(++mCrrentPos);
                }
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, 3000);
            } else if (msg.what == WHAT_AUTO_PLAY_T) {
                mRecyclerView.scrollToPosition(mCrrentPos);
            }
            return false;
        }
    });
    private RecyclerView mIndicatorContainer;
    private IndicatorAdapter mIndicatorAdapter;


    public BannerRecycleView(Context context) {
        this(context, null);
    }

    public BannerRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);

    }

    public BannerRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        isAutoPlaying = true;
        mList = new ArrayList<>();
        mList.add(R.mipmap.banner1);
        mList.add(R.mipmap.banner2);
        mList.add(R.mipmap.banner3);
        mList.add(R.mipmap.banner4);

        //绘制默认选中状态图形
        GradientDrawable selectedGradientDrawable = new GradientDrawable();
        selectedGradientDrawable.setShape(GradientDrawable.OVAL);
        selectedGradientDrawable.setColor(getColor(R.color.colorAccent));
        selectedGradientDrawable.setSize(dp2px(5), dp2px(5));
        selectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
        mSelectedDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
        //绘制默认未选中状态图形
        GradientDrawable unSelectedGradientDrawable = new GradientDrawable();
        unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
        unSelectedGradientDrawable.setColor(getColor(R.color.colorPrimaryDark));
        unSelectedGradientDrawable.setSize(dp2px(5), dp2px(5));
        unSelectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
        mUnselectedDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});
        initView(context);


    }

    private void initView(Context context) {

        mRecyclerView = new RecyclerView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mRecyclerView, params);

        //开始添加指示器
        mIndicatorContainer = new RecyclerView(context);
        MyLinearLayoutManager indicatorLayoutManager = new MyLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        indicatorLayoutManager.setScrollEnabled(true);
        mIndicatorContainer.setLayoutManager(indicatorLayoutManager);
        mIndicatorAdapter = new IndicatorAdapter();
        mIndicatorContainer.setAdapter(mIndicatorAdapter);
        LayoutParams indicatorParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        indicatorParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        addView(mIndicatorContainer, indicatorParams);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int first = mLayoutManager.findFirstVisibleItemPosition();
                int last = mLayoutManager.findLastVisibleItemPosition();

                if (mCrrentPos != first && first == last) {
                    mCrrentPos = first;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if (mList.size() < 2) return;
                int firstReal = mLayoutManager.findFirstVisibleItemPosition();
                View viewFirst = mLayoutManager.findViewByPosition(firstReal);
                float width = getWidth();
                if (width != 0 && viewFirst != null) {
                    float right = viewFirst.getRight();
                    float ratio = right / width;

                    if (ratio > 0.8) {
                        if (mCrrentPos != firstReal) {
                            mCrrentPos = firstReal;
                            refreshIndicator();
                        }
                    } else if (ratio < 0.2) {
                        if (mCrrentPos != firstReal + 1) {
                            mCrrentPos = firstReal + 1;
                            refreshIndicator();
                        }
                    }
                    if (ratio == 1) {
                        if (mCrrentPos == mList.size() + 1) {
                            mLayoutManager.scrollToPosition(1);
                        }
                        if (mCrrentPos == 0) {
                            mLayoutManager.scrollToPosition(mList.size());
                        }
                    }
                }
            }
        });

        mLayoutManager = getLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
//        mRecyclerView.setAdapter(new BannerAdapter(context, mList));
//        mRecyclerView.scrollToPosition(1);
    }

    /**
     * 设置是否自动播放（上锁）
     *
     * @param playing 开始播放
     */
    protected synchronized void setPlaying(boolean playing) {
        if (isAutoPlaying) {
            if (!isPlaying && playing) {
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, 3000);
                isPlaying = true;

            } else if (isPlaying && !playing) {
                mHandler.removeMessages(WHAT_AUTO_PLAY);
                mHandler.removeMessages(WHAT_AUTO_PLAY_T);
                isPlaying = false;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPlaying(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPlaying(true);
                break;
        }
        //解决recyclerView嵌套问题
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(true);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            setPlaying(true);
        } else {
            setPlaying(false);
        }
    }

    public LinearLayoutManager getLayoutManager(Context context) {

        return new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
    }

    /**
     * 标示点适配器
     */
    protected class IndicatorAdapter extends RecyclerView.Adapter {

        int currentPosition = 0;

        public void setPosition(int currentPosition) {
            this.currentPosition = currentPosition;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ImageView bannerPoint = new ImageView(getContext());
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 10, 10, 10);
            bannerPoint.setLayoutParams(lp);
            return new RecyclerView.ViewHolder(bannerPoint) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ImageView bannerPoint = (ImageView) holder.itemView;
            bannerPoint.setImageDrawable(currentPosition == position ? mSelectedDrawable : mUnselectedDrawable);

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    /**
     * 获取颜色
     */
    protected int getColor(@ColorRes int color) {
        return ContextCompat.getColor(getContext(), color);
    }

    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }


    /**
     * 改变导航的指示点
     */
    protected synchronized void refreshIndicator() {
        if (mList.size() > 1) {
            if (mCrrentPos == 0) {
                mIndicatorAdapter.setPosition(mList.size());
            } else if (mCrrentPos == mList.size() + 1) {
                mIndicatorAdapter.setPosition(0);
            } else {
                mIndicatorAdapter.setPosition(mCrrentPos - 1);
            }
            mIndicatorAdapter.notifyDataSetChanged();
        }
    }
}
