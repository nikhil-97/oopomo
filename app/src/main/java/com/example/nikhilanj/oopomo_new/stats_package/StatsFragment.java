package com.example.nikhilanj.oopomo_new.stats_package;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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

    @Override
    public String getFormattedValue(float value, AxisBase axis){
        String x = getDateMonthStringFromTimeMillis((long)value);
        return x;
    }
}

public class StatsFragment extends Fragment{

    public StatsFragment() {}

    private TextView statstextview;

    private List<StatObject> statsList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_fragment_layout, container, false);

        statsList.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("08-4-2018"),2,0));
        statsList.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("09-4-2018"),25,17));
        statsList.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("10-4-2018"),15,10));
        statsList.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("11-4-2018"),20,15));
        statsList.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("12-4-2018"),45,20));
        statsList.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("13-4-2018"),35,10));
        statsList.add(new StatObject(DayValueFormatter.convertDDMMYYYYDateStringToLongTimeMillis("14-4-2018"),25,5));

        TabLayout statsTabLayout = view.findViewById(R.id.stats_tab_layout);

        statstextview = view.findViewById(R.id.statstext);
        statstextview.setVisibility(View.INVISIBLE);

        final BarChart statsBarChart = view.findViewById(R.id.stats_bar_chart);
        statsBarChart.getDescription().setEnabled(false);
        //statsBarChart.setPinchZoom(false);

        statsBarChart.setDrawGridBackground(false);
        statsBarChart.setDrawBarShadow(false);
        statsBarChart.setGridBackgroundColor(Color.WHITE);

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
        xLabels.setLabelCount(7,true);

        statsBarChart.getLegend().setEnabled(false);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < statsList.size(); i++) {
            StatObject stat = statsList.get(i);
            float focustime = (float) stat.getFocusTime();
            float breaktime = (float) stat.getBreakTime();
            System.out.println(yVals1);

            yVals1.add(
                    new BarEntry(TimeUnit.MILLISECONDS.toDays(stat.getTimeStamp()),new float[]{focustime, breaktime}));
        }

        BarDataSet set1;

        if (statsBarChart.getData() != null &&
                statsBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) statsBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            statsBarChart.getData().notifyDataChanged();
            statsBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Your Daily Stats");
            set1.setDrawIcons(false);
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{"Focus Time", "Break Time"});

            List<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextColor(Color.WHITE);

            statsBarChart.setData(data);
        }

        statsBarChart.setFitBars(true);
        statsBarChart.invalidate();


        statsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                statsBarChart.animateY(800, Easing.EasingOption.EaseInOutBack);
                if(tab.getPosition()==0){
                    //TODO : Change range to daily
                }
                else if (tab.getPosition()==1){
                    //TODO : Change range to Weekly
                }
                else if (tab.getPosition()==2){
                    //TODO : Change range to MONTHLY
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

    private int[] getColors() {

        int[] colors = {ColorTemplate.MATERIAL_COLORS[0],ColorTemplate.MATERIAL_COLORS[1]};

        return colors;
    }

}