package com.ancely.pick;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class PickerLayoutManager extends LinearLayoutManager {

    private float mScale = 0.5f;
    private boolean mIsAlpha = true;
    private LinearSnapHelper mLinearSnapHelper;
    private OnSelectedViewListener mOnSelectedViewListener;
    private int mItemViewWidth;
    private int mItemViewHeight;
    private int mItemCount = -1;
    private RecyclerView mRecyclerView;
    private int mOrientation;
    private int mPickCount;
    private int mScreenWidth;


    public PickerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.mLinearSnapHelper = new PickerSnapHelper();
        this.mOrientation = orientation;
    }

    public PickerLayoutManager(Context context, RecyclerView recyclerView, int orientation, boolean reverseLayout, int itemCount, float scale, boolean isAlpha, int pickCount) {
        super(context, orientation, reverseLayout);
        this.mLinearSnapHelper = new PickerSnapHelper();
        this.mPickCount = pickCount;
        this.mItemCount = itemCount;
        if (mItemCount % 2 == 0) {
            mItemCount = mItemCount + 1;
        }
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.mOrientation = orientation;
        this.mRecyclerView = recyclerView;
        this.mIsAlpha = isAlpha;
        this.mScale = scale;
        if (mItemCount != 0) setAutoMeasureEnabled(false);
    }

    /**
     * 添加LinearSnapHelper
     */
    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mLinearSnapHelper.attachToRecyclerView(view);
    }

    /**
     * 没有指定显示条目的数量时，RecyclerView的宽高由自身确定
     * 指定显示条目的数量时，根据方向分别计算RecyclerView的宽高
     */
    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        if (getItemCount() != 0 && mItemCount != 0) {

            View view = recycler.getViewForPosition(0);
            measureChildWithMargins(view, widthSpec, heightSpec);

            mItemViewWidth = view.getMeasuredWidth();
            mItemViewHeight = view.getMeasuredHeight();

            if (mItemViewWidth <= 0) {
                if (mPickCount > 0) {
                    mItemViewWidth = mScreenWidth / mPickCount;
                }
            }

            if (mOrientation == HORIZONTAL) {
                int paddingHorizontal = (int) ((mItemCount - 1) * 1.0f / 2 * mItemViewWidth);
                mRecyclerView.setClipToPadding(false);
                mRecyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
                setMeasuredDimension(mItemViewWidth * mItemCount, mItemViewHeight);
            } else if (mOrientation == VERTICAL) {
                int paddingVertical = (mItemCount - 1) / 2 * mItemViewHeight;
                mRecyclerView.setClipToPadding(false);
                if (!mIsCycle) {
                    mRecyclerView.setPadding(0, paddingVertical, 0, paddingVertical);
                }
                setMeasuredDimension(mItemViewWidth, mItemViewHeight * mItemCount);
            }
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
    }

    int getItemViewHeight() {
        return mItemViewHeight;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() < 0 || state.isPreLayout()) return;

        if (mOrientation == HORIZONTAL) {
            scaleHorizontalChildView();
        } else if (mOrientation == VERTICAL) {
            scaleVerticalChildView();
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleHorizontalChildView();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleVerticalChildView();
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 横向情况下的缩放
     */
    private void scaleHorizontalChildView() {
        float mid = getWidth() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f;
            float scale = 1.0f + (-1 * (1 - mScale)) * (Math.min(mid, Math.abs(mid - childMid))) / mid;
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (mIsAlpha) {
                child.setAlpha(scale);
            }
        }
    }

    /**
     * 竖向方向上的缩放
     */
    private void scaleVerticalChildView() {
        float mid = getHeight() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMid = (getDecoratedTop(child) + getDecoratedBottom(child)) / 2.0f;
            float scale = 1.0f + (-1 * (1 - mScale)) * (Math.min(mid, Math.abs(mid - childMid))) / mid;
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (mIsAlpha) {
                child.setAlpha(scale);
            }
        }
    }


    /**
     * 当滑动停止时触发回调
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == 0) {
            if (mOnSelectedViewListener != null && mLinearSnapHelper != null) {
                View view = mLinearSnapHelper.findSnapView(this);
                if (view != null) {
                    int position = getPosition(view);
                    PickAdapter adapter = (PickAdapter) mRecyclerView.getAdapter();
                    mOnSelectedViewListener.onSelectedView(view, position, adapter.getCycleNum() / 2);
                }
            }
        }
    }


    public void setOnSelectedViewListener(OnSelectedViewListener listener) {
        this.mOnSelectedViewListener = listener;
    }

    /**
     * 停止时，显示在中间的View的监听
     */
    public interface OnSelectedViewListener {
        void onSelectedView(View view, int i, int position);
    }


    /**
     * 滚动到目标位置  无限循环时调用此方法
     *
     * @param position 目标位置
     */
    public void scrollToPositionForCycle(int position, int size, int cycleNum) {
        if (cycleNum == 0) {
            cycleNum = ((PickAdapter) mRecyclerView.getAdapter()).getCycleNum() / 2;
        }

        if (mIsCycle) {
            scrollToPositionWithOffset(size * cycleNum + position % size - mItemCount / 2, 0);
        }
    }


    /**
     * 滚动到目标位置   不是无循环时调用
     *
     * @param position 目标位置
     */
    public void scrollToPosition(int position) {
        if (!mIsCycle) {
            scrollToPositionWithOffset(position, 0);
        }
    }

    private boolean mIsCycle;

    /**
     * 设置是否循环
     */
    public void setCycle(boolean cycleFlag) {
        mIsCycle = cycleFlag;
    }

    public boolean getCycle() {
        return mIsCycle;
    }
}
