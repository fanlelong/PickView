package com.ancely.pick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PickRecycleView extends RecyclerView {

    private int mWidth;
    private int mHeight;
    private Rect mTextBound;
    private Rect mRvChildBound;
    private boolean mIsShowSuffix;
    private String mSuffixText = "";
    private Context mContext;

    public PickRecycleView(Context context) {
        this(context, null);
    }

    public PickRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        android:overScrollMode="never"
        setOverScrollMode(OVER_SCROLL_NEVER);
        this.mContext = context;
        mPaint = new Paint();
        mTextPaint = new Paint();
        mRvPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(px2dp(1));

        mTextPaint.setColor(0xff333333);
        mTextPaint.setTextSize(px2dp(28));
        mTextPaint.setAntiAlias(true);

        mRvPaint.setTextSize(px2dp(34));

        mTextBound = new Rect();
        mRvChildBound = new Rect();

        mTopPath = new Path();
        mBottomPath = new Path();
    }

    private Paint mPaint;
    private Paint mTextPaint;
    private Paint mRvPaint;
    private Path mTopPath;
    private Path mBottomPath;

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        PickerLayoutManager layoutManager = (PickerLayoutManager) getLayoutManager();
        if (layoutManager != null) {
            mTopPath.moveTo(0, mHeight * 1.0f / 2 - layoutManager.getItemViewHeight() / 2);
            mTopPath.lineTo(mWidth, mHeight * 1.0f / 2 - layoutManager.getItemViewHeight() / 2);
            mBottomPath.moveTo(0, mHeight * 1.0f / 2 + layoutManager.getItemViewHeight() / 2);
            mBottomPath.lineTo(mWidth, mHeight * 1.0f / 2 + layoutManager.getItemViewHeight() / 2);

            c.drawPath(mTopPath, mPaint);
            c.drawPath(mBottomPath, mPaint);
            if (mIsShowSuffix) {
                c.drawText(mSuffixText, mWidth * 1.0f / 2 + mRvChildBound.width() * 1.0f / 2 + px2dp(10), mHeight * 1.0f / 2 + mTextBound.height() / 2, mTextPaint);
            }
        }
    }

    /**
     * 设置选择器item的key的个数,计算出大小
     */
    public void setRVChildSinglerText(String itemKey) {
        mRvPaint.getTextBounds(itemKey, 0, itemKey.length(), mRvChildBound);
    }

    public int[] getTopAndBottomPathHeight() {
        PickerLayoutManager layoutManager = (PickerLayoutManager) getLayoutManager();
        int[] height = new int[]{0, 0};
        if (layoutManager != null) {
            height[0] = (int) (mHeight * 1.0f / 2 - layoutManager.getItemViewHeight() / 2);
            height[1] = (int) (mHeight * 1.0f / 2 + layoutManager.getItemViewHeight() / 2);
        }
        return height;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);

    }

    /**
     * 是否显示后缀
     */
    public void isShowSuffix(String suffixString) {
        this.mIsShowSuffix = true;
        this.mSuffixText = suffixString;
        mTextPaint.getTextBounds(suffixString, 0, suffixString.length(), mTextBound);
    }

    public int px2dp(int px) {

        float scale = mContext.getResources().getDisplayMetrics().widthPixels / 720.0f;
        return (int) (px * scale + 0.5f);
    }
}
