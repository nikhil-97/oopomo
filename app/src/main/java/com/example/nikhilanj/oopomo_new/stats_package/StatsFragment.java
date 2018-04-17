package com.example.nikhilanj.oopomo_new.stats_package;

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
import com.example.nikhilanj.oopomo_new.db.entity.StatObject;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class DayValueFormatter implements IAxisValueFormatter {

    static long convertDateStringToLongTimeMillis(String datestring){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try{
            Date d = sdf.parse(datestring);
            System.out.println("d = "+d);
            return d.getTime();
        }
        catch (ParseException p){p.printStackTrace();}
        return 0;
    }

    static long convertMonthStringToLongTimeMillis(String datestring){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
        try{
            Date m = sdf.parse(datestring);
            System.out.println("m = "+m);
            return m.getTime();
        }
        catch (ParseException p){p.printStackTrace();}
        return 0;
    }

    private static String getDateMonthStringFromTimeDays(long timeInDays){
        long timeInMillis = TimeUnit.DAYS.toMillis(timeInDays+1);
        return (new SimpleDateFormat("dd",Locale.getDefault()).format(timeInMillis)+"-"+
                new SimpleDateFormat("MMM",Locale.getDefault()).format(timeInMillis));
    }
    private static String getMonthStringFromTimeDays(long timeInDays){
        long timeInMillis = TimeUnit.DAYS.toMillis((timeInDays*30+30));
        System.out.println("timeInDays = "+timeInDays+" timeInMillis = "+timeInMillis);
        return (new SimpleDateFormat("MMMM",Locale.getDefault()).format(timeInMillis));
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis){
        String x = "0";
        if(StatsFragment.currentViewRange==StatsFragment.VIEWRANGE_DAILY) {
            x = getDateMonthStringFromTimeDays((long) value);
        }
        else if(StatsFragment.currentViewRange==StatsFragment.VIEWRANGE_MONTHLY) {
            x = getMonthStringFromTimeDays((long) value);
        }
        System.out.println("x getFormattedValue = "+x);
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

    private void makeChart(ArrayList<BarEntry> yValues,int viewRange){
        if(statsBarChart.getBarData()!=null){statsBarChart.getBarData().clearValues();}

        BarDataSet set1 = new BarDataSet(yValues, "Your Stats");
        set1.setColors(getChartColors());
        set1.setStackLabels(new String[]{"Focus Time", "Break Time"});

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextColor(Color.WHITE);

        statsBarChart.setData(data);
        int xRangeMax = 7;
        if(viewRange==VIEWRANGE_DAILY) xRangeMax = 7;
        else if(viewRange==VIEWRANGE_WEEKLY) xRangeMax = 4;
        else if(viewRange==VIEWRANGE_MONTHLY) xRangeMax = 4;
        statsBarChart.setVisibleXRange(1,xRangeMax);

        }

    private void showChart(int viewRange){
        ArrayList<StatObject> statsListInRange = loadStats(viewRange);
        ArrayList<BarEntry> yVals1 =new ArrayList<>();
        addTimeValues(yVals1,statsListInRange);
        System.out.println("yVals1 = "+yVals1);
        makeChart(yVals1,viewRange);
        statsBarChart.getBarData().notifyDataChanged();
        statsBarChart.notifyDataSetChanged();

    }

    private int[] getChartColors() {

        int color1 = ResourcesCompat.getColor(getResources(), R.color.timeCircleFinished,null);
        int color2 = ResourcesCompat.getColor(getResources(), R.color.timeCircleUnfinished, null);

        return (new int[]{color1,color2});
    }

    private void addTimeValues(ArrayList<BarEntry> yValues,List<StatObject> statlist) {
        int listsize = statlist.size();
        for (int i = 0; i < listsize; i++) {
            StatObject stat = statlist.get(i);
            float focustime = (float) stat.getFocusTime();
            float breaktime = (float) stat.getBreakTime();
            System.out.println("ft = "+focustime+" bt = "+breaktime);

            yValues.add(
                    new BarEntry(TimeUnit.MILLISECONDS.toDays(stat.getTimeStamp()),
                            new float[]{focustime, breaktime}));
        }
        }

    private ArrayList<StatObject> loadStats(int viewRange){
        ArrayList<StatObject> statsRequested = new ArrayList<>();
        if(viewRange==VIEWRANGE_DAILY){
            //load stats for last 7 days
            //write queries here
            for(int i =1;i<=30;i++){
                String monthstring = String.format("%01d",i).concat("-4-2018");
                System.out.println(monthstring);
                int ft = new Random().nextInt(150);
                int bt = new Random().nextInt(60);

                statsRequested.add(new StatObject(DayValueFormatter.convertDateStringToLongTimeMillis(monthstring),ft,bt));
            }
        }
        else if(viewRange==VIEWRANGE_WEEKLY){
            //Load stats for last 4 weeks
        }
        else if(viewRange==VIEWRANGE_MONTHLY){
            //load stats for last 4(?) months
            for(int i =1;i<=12;i++){
                String monthstring = String.format("%02d",i).concat("-2018");
                System.out.println(monthstring);
                int ft = new Random().nextInt(1000);
                int bt = new Random().nextInt(500);

                statsRequested.add(new StatObject(DayValueFormatter.convertMonthStringToLongTimeMillis(monthstring),ft,bt));
            }
            }
        return statsRequested;
    }

}