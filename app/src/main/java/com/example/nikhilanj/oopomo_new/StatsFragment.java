package com.example.nikhilanj.oopomo_new;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.StackedBarModel;

public class StatsFragment extends Fragment{

    public StatsFragment() {}

    private TextView statstextview;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.stats_fragment_layout, container, false);

            TabLayout statsTabLayout = view.findViewById(R.id.stats_tab_layout);
            //final StackedBarChart dailyStatsBarChart = (StackedBarChart) view.findViewById(R.id.daily_stats_barchart);
            //StackedBarChart weeklyStatsBarChart = (StackedBarChart) view.findViewById(R.id.weekly_stats_barchart);
            //StackedBarChart monthlyStatsBarChart = (StackedBarChart) view.findViewById(R.id.monthly_stats_barchart);


            statsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                /*    if(tab.getPosition()==0){
                        /*StackedBarModel s1 = new StackedBarModel("7 April 2018");
                        s1.addBar(new BarModel(2.3f, 0xFF63CBB0));
                        s1.addBar(new BarModel(2.3f, 0xFF56B7F1));
                        s1.addBar(new BarModel(2.3f, 0xFFCDA67F));
                        dailyStatsBarChart.addBar(s1);
                        dailyStatsBarChart.startAnimation();/
                    }
                    else if(tab.getPosition()==1){

                    }
                    else if(tab.getPosition()==2){

                    }
                */
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

}