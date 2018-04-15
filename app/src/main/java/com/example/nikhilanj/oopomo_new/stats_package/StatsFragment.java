package com.example.nikhilanj.oopomo_new.stats_package;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nikhilanj.oopomo_new.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

class DayValueFormatter implements IAxisValueFormatter {

    static long convertDDMMYYYYDateStringToLongTimeMillis(String datestring){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault());
        try{
            Date d = sdf.parse(datestring);
            System.out.println("d = "+d);
            return d.getTime();
        }
        catch (ParseException p){p.printStackTrace();}
        return 0;
    }

    static String getDateMonthStringFromTimeMillis(long timeInMinutes){
        long timeInMillis = TimeUnit.DAYS.toMillis(timeInMinutes);
        return (new SimpleDateFormat("dd",Locale.getDefault()).format(timeInMillis)+"-"+
                new SimpleDateFormat("MMM",Locale.getDefault()).format(timeInMillis));
    }
    static String getMonthStringFromTimeMillis(long timeInDays){
        long timeInMillis = TimeUnit.DAYS.toMillis(timeInDays);
        return (new SimpleDateFormat("MMMM",Locale.getDefault()).format(timeInMillis));
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis){
        String x = "0";
        if(StatsFragment.currentViewRange==StatsFragment.VIEWRANGE_DAILY) {
            x = getDateMonthStringFromTimeMillis((long) value);
        }
        else if(StatsFragment.currentViewRange==StatsFragment.VIEWRANGE_MONTHLY) {
            x = getMonthStringFromTimeMillis((long) value);
        }
        return x;
    }
}

public class StatsFragment extends Fragment{

    public StatsFragment() {}

    private TextView statstextview;
    private BarChart statsBarChart;

    private List<StatObject> statsListDaily = new ArrayList<>();
    private List<StatObject> statsListWeekly = new ArrayList<>();
    private List<StatObject> statsListMonthly = new ArrayList<>();

    final public static int VIEWRANGE_DAILY   = 0;
    final public static int VIEWRANGE_WEEKLY  = 1;
    final public static int VIEWRANGE_MONTHLY = 2;

    public static int currentViewRange = VIEWRANGE_DAILY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_fragment_layout, container, false);



        TabLayout statsTabLayout = view.findViewById(R.id.stats_tab_layout);

        statstextview = view.findViewById(R.id.statstext);
        statstextview.setVisibility(View.INVISIBLE);

        statsBarChart = view.findViewById(R.id.stats_bar_chart);
        statsBarChart.getDescription().setEnabled(false);
        //statsBarChart.setPinchZoom(false);

        statsBarChart.setDrawGridBackground(false);
        statsBarChart.setDrawBarShadow(false);
        statsBarChart.setGridBackgroundColor(Color.WHITE);
        statsBarChart.setClickable(false);

        statsBarChart.setDrawValueAboveBar(false);
        statsBarChart.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = statsBarChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false);
        statsBarChart.getAxisRight().setEnabled(false);


        XAxis xLabels = statsBarChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
        xLabels.setValueFormatter(new DayValueFormatter());
        xLabels.setGranularity(1f);
        xLabels.setDrawGridLines(true);
        xLabels.setLabelCount(8,true);

        statsBarChart.getLegend().setEnabled(false);

        showChart(VIEWRANGE_DAILY);

        statsBarChart.setFitBars(true);
        statsBarChart.invalidate();


        statsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                statsBarChart.animateY(500, Easing.EasingOption.EaseInOutBack);
                if(tab.getPosition()==0){
                    currentViewRange = VIEWRANGE_DAILY;
                    showChart(currentViewRange);
                }
                else if (tab.getPosition()==1){
                    currentViewRange = VIEWRANGE_WEEKLY;
                    showChart(currentViewRange);
                }
                else if (tab.getPosition()==2){
                    currentViewRange = VIEWRANGE_MONTHLY;
                    showChart(currentViewRange);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    @Override
    public void onDetach() {super.onDetach();}

    private void makeChart(ArrayList<BarEntry> yValues){
        if(statsBarChart.getBarData()!=null) statsBarChart.getBarData().clearValues();
        BarDataSet set1;
        set1 = new BarDataSet(yValues, "Your Stats");
        set1.setDrawIcons(false);
        set1.setColors(getColors());
        set1.setStackLabels(new String[]{"Focus Time", "Break Time"});

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextColor(Color.WHITE);

        statsBarChart.setData(data);
        }

    private void showChart(int viewRange){
        ArrayList<StatObject> statsListInRange = loadStats(viewRange);
        ArrayList<BarEntry> yVals1 = addTimeValues(statsListInRange);
        makeChart(yVals1);
        statsBarChart.getBarData().notifyDataChanged();
        statsBarChart.notifyDataSetChanged();

    }

    private int[] getColors() {

        int color1 = ResourcesCompat.getColor(getResources(), R.color.timeCircleFinished,null);
        int color2 = ResourcesCompat.getColor(getResources(), R.color.timeCircleUnfinished, null);

        return (new int[]{color1,color2});
    }

    private ArrayList<BarEntry> addTimeValues(List<StatObject> statlist) {
        ArrayList<BarEntry> yValues = new ArrayList<>();
        int listsize = statlist.size();
        for (int i = 0; i < listsize; i++) {
            StatObject stat = statlist.get(i);
            float focustime = (float) stat.getFocusTime();
            float breaktime = (float) stat.getBreakTime();

            yValues.add(
                    new BarEntry(TimeUnit.MILLISECONDS.toDays(stat.getTimeStamp()),
                            new float[]{focustime, breaktime}));
        }
            return yValues;
        }

    private ArrayList<StatObject> loadStats(int viewRange){
        ArrayList<StatObject> statsRequested = new ArrayList<>();
        if(viewRange==VIEWRANGE_DAILY){
            //load stats for last 7 days
        }
        else if(viewRange==VIEWRANGE_WEEKLY){
            //Load stats for last 4 weeks
        }
        else if(viewRange==VIEWRANGE_MONTHLY){
            //load stats for last 4(?) months
        }
        statsRequested.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("08-4-2018"),2,0));
        statsRequested.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("09-4-2018"),25,17));
        statsRequested.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("10-4-2018"),15,10));
        statsRequested.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("11-4-2018"),20,15));
        statsRequested.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("12-4-2018"),45,20));
        statsRequested.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("13-4-2018"),35,10));
        statsRequested.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("14-4-2018"),25,5));
        return statsRequested;
    }

}