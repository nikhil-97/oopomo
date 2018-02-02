package com.example.nikhilanj.oopomo_new;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.transition.Fade;
import android.transition.Transition;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Set;


public class MainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    HomeFragment homefragment = new HomeFragment();
    GoalsFragment goalsfragment = new GoalsFragment();
    StatsFragment statsfragment = new StatsFragment();
    SettingsFragment settingsfragment = new SettingsFragment();
    Transition mFadeIn = new Fade(Fade.IN);
    Transition mFadeOut = new Fade(Fade.OUT);




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadHomeFragment(homefragment);
                    return true;
                case R.id.navigation_stats:
                    loadStatsFragment(statsfragment);
                    return true;
                case R.id.navigation_goals:
                    loadGoalsFragment(goalsfragment);
                    return true;
                case R.id.navigation_settings:
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

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(android.R.id.content, homefragment).commit();

        TransitionManager.beginDelayedTransition(navigation, mFadeOut);

        getSupportActionBar().setTitle("Oopomo");
    }

    private void loadHomeFragment(HomeFragment homefragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, homefragment).commit();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.addToBackStack(null);

        getSupportActionBar().setTitle("Oopomo");
    }

    private void loadGoalsFragment(GoalsFragment goalsfragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, goalsfragment).commit();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        getSupportActionBar().setTitle("Goals");
    }

    private void loadSettingsFragment(SettingsFragment settingsfragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, settingsfragment).commit();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        getSupportActionBar().setTitle("Settings");
    }

    private void loadStatsFragment(StatsFragment statsfragment){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(android.R.id.content, statsfragment).commit();
        getSupportActionBar().setTitle("Statistics");
    }



}
