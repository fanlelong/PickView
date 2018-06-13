package com.ancely.pick;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static float sNoncompatDensity;
    private static float sNoncompatScaleDensity;
    private PickerLayoutManager mPickerLayoutManager1;
    private PickerLayoutManager mPickerLayoutManager3;
    private PickerLayoutManager mPickerLayoutManager2;

    private List<String> mYearLists;
    private List<String> mCurrentMonthDays;
    private TextView mTime;
    private TextView mTime1;
    private TextView mTime2;
    private List<String> mMonthLists;
    private PickerLayoutManager mPickerLayoutManager4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setCustomDensity(this, PickApplication.getInstance());
        setContentView(R.layout.activity_main);

        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);//设置起时间

        mTime = findViewById(R.id.time);
        mTime1 = findViewById(R.id.time1);
        mTime2 = findViewById(R.id.time2);

        PickRecycleView textRv = findViewById(R.id.text_rv);
        PickRecycleView textRv2 = findViewById(R.id.text_rv2);
        PickRecycleView textRv3 = findViewById(R.id.text_rv3);
        PickRecycleView textRv4 = findViewById(R.id.text_rv4);

        mCurrentMonthDays = new ArrayList<>();
        mCurrentMonthDays.addAll(getCurrentMonthDays());

        mYearLists = new ArrayList<>();
        mYearLists.addAll(getYearLists());

        mMonthLists = new ArrayList<>();
        mMonthLists.addAll(getMonthLists());


        textRv.setRVChildSinglerText("2019");
        textRv2.setRVChildSinglerText("20");
        textRv3.setRVChildSinglerText("20");
        textRv.isShowSuffix(true, "年");
        textRv2.isShowSuffix(true, "月");
        textRv3.isShowSuffix(true, "日");

        mPickerLayoutManager1 = new PickerLayoutManager(this,
                textRv, PickerLayoutManager.VERTICAL, false, 3, 0.8f, true, 3);
        mPickerLayoutManager3 = new PickerLayoutManager(this,
                textRv3, PickerLayoutManager.VERTICAL, false, 3, 0.8f, true, 3);
        mPickerLayoutManager2 = new PickerLayoutManager(this,
                textRv2, PickerLayoutManager.VERTICAL, false, 3, 0.8f, true, 3);

        mPickerLayoutManager4 = new PickerLayoutManager(this,
                textRv4, PickerLayoutManager.VERTICAL, false, 5, 0.8f, true, 3);


        mPickerLayoutManager1.setCycle(false);
        mPickerLayoutManager2.setCycle(true);
        mPickerLayoutManager3.setCycle(true);
        mPickerLayoutManager1.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position, int cycleNum) {
                mTime.setText(mYearLists.get(position % mYearLists.size()));

                if (position < mYearLists.size() * cycleNum || position >= mYearLists.size() * (cycleNum + 1)) {
                    mPickerLayoutManager1.scrollToPositionForCycle(position, mYearLists.size(), cycleNum);
                }
            }
        });

        mPickerLayoutManager2.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position, int cycleNum) {
                mTime1.setText(mMonthLists.get(position % mMonthLists.size()));

                if (position < mMonthLists.size() * cycleNum || position >= mMonthLists.size() * (cycleNum + 1)) {
                    mPickerLayoutManager2.scrollToPositionForCycle(position, mMonthLists.size(), cycleNum);
                }
            }
        });

        mPickerLayoutManager3.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position, int cycleNum) {
                mTime2.setText(mCurrentMonthDays.get(position % mCurrentMonthDays.size()));

                if (position < mCurrentMonthDays.size() * cycleNum || position >= mCurrentMonthDays.size() * (cycleNum + 1)) {
                    mPickerLayoutManager3.scrollToPositionForCycle(position, mCurrentMonthDays.size(), cycleNum);
                }
            }
        });

        textRv.setLayoutManager(mPickerLayoutManager1);
        textRv2.setLayoutManager(mPickerLayoutManager2);
        textRv3.setLayoutManager(mPickerLayoutManager3);
        textRv4.setLayoutManager(mPickerLayoutManager4);
        PickAdapter pickAdapter = new PickAdapter(mYearLists, textRv);
        PickAdapter pickAdapter2 = new PickAdapter(mMonthLists, textRv2);
        PickAdapter pickAdapter3 = new PickAdapter(mCurrentMonthDays, textRv3);
        PickAdapter pickAdapter4 = new PickAdapter(mYearLists, textRv4);
        textRv.setAdapter(pickAdapter);
        textRv2.setAdapter(pickAdapter2);
        textRv3.setAdapter(pickAdapter3);
        textRv4.setAdapter(pickAdapter4);

        int year = mYearLists.indexOf(String.valueOf(cal.get(Calendar.YEAR)));
        mPickerLayoutManager1.scrollToPosition(year);
        mPickerLayoutManager4.scrollToPosition(year);
        int month = cal.get(Calendar.MONTH);
        if (month <= 9) {
            month = mMonthLists.indexOf("0" + month) + 1;
        } else {
            month = mMonthLists.indexOf(String.valueOf(month)) + 1;
        }
        mPickerLayoutManager2.scrollToPositionForCycle(month, mMonthLists.size(), 4);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day <= 9) {
            day = mCurrentMonthDays.indexOf("0" + day);
        } else {
            day = mCurrentMonthDays.indexOf(String.valueOf(day));
        }
        mPickerLayoutManager3.scrollToPositionForCycle(day, mCurrentMonthDays.size(), 4);
    }

    public static void setCustomDensity(@NonNull final Activity activity, @NonNull final Application application) {
        final DisplayMetrics metrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            sNoncompatDensity = metrics.density;
            sNoncompatScaleDensity = metrics.scaledDensity;

            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });

            final float targetDensity = metrics.widthPixels * 1.0f / 720;
            final float targetScanleDensity = targetDensity * (sNoncompatScaleDensity / sNoncompatDensity);
            final int targetDensityDpi = (int) (160 * targetDensity);
            metrics.density = targetDensity;
            metrics.scaledDensity = targetScanleDensity;
            metrics.densityDpi = targetDensityDpi;

            DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
            displayMetrics.density = targetDensity;
            displayMetrics.scaledDensity = targetScanleDensity;
            displayMetrics.densityDpi = targetDensityDpi;
        }
    }

    /**
     * 年
     */
    private List<String> getYearLists() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        List<String> yearLists = new ArrayList<>();
        for (int i = year - 20; i < year + 20; i++) {
            yearLists.add(String.valueOf(i));
        }
        return yearLists;
    }


    /**
     * 月
     */
    private List<String> getMonthLists() {
        List<String> monthLists = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            if (i <= 9) {
                monthLists.add("0" + i);
                continue;
            }
            monthLists.add(String.valueOf(i));
        }
        return monthLists;
    }

    private List<String> getCurrentMonthDays() {
        List<String> dayLists = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        c.set(year, month + 1, 1);
        c.add(Calendar.DATE, -1);
        int days = c.get(Calendar.DATE);
        for (int i = 1; i <= days; i++) {
            if (i <= 9) {
                dayLists.add("0" + i);
                continue;
            }
            dayLists.add(String.valueOf(i));
        }
        return dayLists;
    }
}
