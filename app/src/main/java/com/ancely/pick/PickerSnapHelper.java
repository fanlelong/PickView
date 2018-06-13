package com.ancely.pick;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PickerSnapHelper extends LinearSnapHelper {
    @Nullable
    private OrientationHelper mHorizontalHelper;
    private OrientationHelper mVerticalHelper;

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        return findTargetSnapPosition1(layoutManager, velocityX, velocityY);
    }


    private int findTargetSnapPosition1(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {

        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final View currentView = findSnapView(layoutManager);//获取到目标view
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        final int currentPosition = layoutManager.getPosition(currentView);//获取到目标view的索引
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }


        RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider = (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
        // 通过ScrollVectorProvider接口中的computeScrollVectorForPosition（）方法
        // 来确定layoutManager的布局方向
        PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
        if (vectorForEnd == null) {
            return RecyclerView.NO_POSITION;
        }

        //计算一屏的item数
        int deltaThreshold = 3;

        int deltaJump, xdeltaJump, hdeltaJump;
        if (layoutManager.canScrollHorizontally()) {
            //layoutManager是横向布局，并且内容超出一屏，canScrollHorizontally()才返回true
            //估算fling结束时相对于当前snapView位置的横向位置偏移量
            xdeltaJump = estimateNextPositionDiffForFling(layoutManager, getHorizontalHelper(layoutManager), velocityX, 0);
            if (xdeltaJump > deltaThreshold) {
                xdeltaJump = deltaThreshold;
            }
            if (xdeltaJump < -deltaThreshold) {
                xdeltaJump = -deltaThreshold;
            }

            //vectorForEnd.x < 0代表layoutManager是反向布局的，就把偏移量取反
            if (vectorForEnd.x < 0) {
                //不能横向滚动，横向位置偏移量当然就为0
                xdeltaJump = -xdeltaJump;
            }
        } else {
            xdeltaJump = 0;
        }

        if (layoutManager.canScrollVertically()) {
            hdeltaJump = estimateNextPositionDiffForFling(layoutManager,
                    getVerticalHelper(layoutManager), 0, velocityY);
            if (hdeltaJump > deltaThreshold) {
                hdeltaJump = deltaThreshold;
            }
            if (hdeltaJump < -deltaThreshold) {
                hdeltaJump = -deltaThreshold;
            }

            if (vectorForEnd.y < 0) {
                hdeltaJump = -hdeltaJump;
            }
        } else {
            hdeltaJump = 0;
        }

        deltaJump = layoutManager.canScrollVertically() ? hdeltaJump : xdeltaJump;

        if (deltaJump == 0) {
            return RecyclerView.NO_POSITION;
        }
        //当前位置加上偏移位置，就得到fling结束时的位置，这个位置就是targetPosition
        int targetPos = currentPosition + deltaJump;
        if (targetPos < 0) {
            targetPos = 0;
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1;
        }
        return targetPos;


    }

    private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager layoutManager, OrientationHelper helper, int velocityX, int velocityY) {
        //计算滚动的总距离，这个距离受到触发fling时的速度的影响
        int[] distances = calculateScrollDistance(velocityX, velocityY);

        //计算每个ItemView的长度
        float distancePerChild = computeDistancePerChild(layoutManager, helper);

        if (distancePerChild <= 0) {
            return 0;
        }
        //这里其实就是根据是横向布局还是纵向布局，来取对应布局方向上的滚动距离

        int distance = Math.abs(distances[0]) > Math.abs(distances[1]) ? distances[0] : distances[1];
        //distance的正负值符号表示滚动方向，数值表示滚动距离。横向布局方式，内容从右往左滚动为正；竖向布局方式，内容从下往上滚动为正
        // 滚动距离/item的长度=滚动item的个数，这里取计算结果的整数部分
        return Math.round(distance / distancePerChild);
    }

    private float computeDistancePerChild(RecyclerView.LayoutManager layoutManager,
                                          OrientationHelper helper) {
        View minPosView = null;
        View maxPosView = null;
        int minPos = Integer.MAX_VALUE;
        int maxPos = Integer.MIN_VALUE;
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return INVALID_DISTANCE;
        }

        //循环遍历layoutManager的itemView，得到最小position和最大position，以及对应的view
        for (int i = 0; i < childCount; i++) {
            View child = layoutManager.getChildAt(i);
            final int pos = layoutManager.getPosition(child);
            if (pos == RecyclerView.NO_POSITION) {
                continue;
            }
            if (pos < minPos) {
                minPos = pos;
                minPosView = child;
            }
            if (pos > maxPos) {
                maxPos = pos;
                maxPosView = child;
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE;
        }

        //最小位置和最大位置肯定就是分布在layoutManager的两端，但是无法直接确定哪个在起点哪个在终点（因为有正反向布局）
        //所以取两者中起点坐标小的那个作为起点坐标
        //终点坐标的取值一样的道理
        int start = Math.min(helper.getDecoratedStart(minPosView),
                helper.getDecoratedStart(maxPosView));
        int end = Math.max(helper.getDecoratedEnd(minPosView),
                helper.getDecoratedEnd(maxPosView));
        //终点坐标减去起点坐标得到这些itemview的总长度
        int distance = end - start;
        if (distance == 0) {
            return INVALID_DISTANCE;
        }
        // 总长度 / itemview个数 = itemview平均长度
        return 1f * distance / ((maxPos - minPos) + 1);
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }

    private static final float INVALID_DISTANCE = 1f;


    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }
}
