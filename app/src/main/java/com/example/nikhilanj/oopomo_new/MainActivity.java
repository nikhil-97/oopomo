package com.example.nikhilanj.oopomo_new;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    HomeFragment homefragment = new HomeFragment();
    GoalsFragment goalsfragment = new GoalsFragment();
    StatsFragment statsfragment = new StatsFragment();
    SettingsFragment settingsfragment = new SettingsFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    loadHomeFragment(homefragment);
                    return true;
                case R.id.navigation_stats:
                    mTextMessage.setText(R.string.title_stats);
                    loadStatsFragment(statsfragment);
                    return true;
                case R.id.navigation_goals:
                    mTextMessage.setText(R.string.title_goals);
                    loadGoalsFragment(goalsfragment);
                    return true;
                case R.id.navigation_settings:
                    mTextMessage.setText(R.string.title_settings);
                    loadSettingsFragment(settingsfragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void loadHomeFragment(HomeFragment homefragment){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.home_fragment, homefragment).commit();
    }

    private void loadGoalsFragment(GoalsFragment goalsfragment){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.goals_fragment, goalsfragment).commit();
    }

    private void loadSettingsFragment(SettingsFragment settingsfragment){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.settings_fragment, settingsfragment).commit();
    }

    private void loadStatsFragment(StatsFragment statsfragment){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.stats_fragment, statsfragment).commit();
    }



}
