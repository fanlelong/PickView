package com.ancely.pick;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

public class PickAdapter extends RecyclerView.Adapter<PickAdapter.ViewHolder> {
    private List<String> mList;
    private RecyclerView mRecyclerView;
    private int mCycleNum;

    public PickAdapter(List<String> list, RecyclerView recyclerView) {
        this.mList = list;
        this.mRecyclerView = recyclerView;
        this.mCycleNum = 8;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(PickApplication.getInstance()).inflate(R.layout.item_wheel_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mList == null || mList.size() == 0) {
            return;
        }
        holder.tvText.setText(mList.get(position % mList.size()));
    }


    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        if (mRecyclerView.getLayoutManager() instanceof PickerLayoutManager) {
            if (((PickerLayoutManager) mRecyclerView.getLayoutManager()).getCycle()) {
                return mList.size() + mList.size() * mCycleNum;
            }
        }
        return mList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;

        public ViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.item_wheel_tv);
        }
    }

    public void setCycleNum(int cycleNum) {
        if (cycleNum % 2 != 0) {
            cycleNum += 1;
        }
        this.mCycleNum = cycleNum;
    }

    public int getCycleNum() {
        return mCycleNum;
    }
}