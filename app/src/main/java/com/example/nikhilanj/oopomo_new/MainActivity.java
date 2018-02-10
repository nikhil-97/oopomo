package com.example.nikhilanj.oopomo_new;

import android.content.Context;
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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikhilanj.oopomo_new.lib.PomoTimer;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener {

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

        getSupportActionBar().setTitle(getString (R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dark_mode_setting:
                item.setChecked(!item.isChecked());
                Context context = getBaseContext();
                String toasttext;
                if(item.isChecked()){
                    setTheme(R.style.DarkTheme);
                    //TODO: Doesn't work
                    toasttext="Dark Theme applied";
                }
                else {
                    toasttext="Dark Theme removed";
                    setTheme(R.style.AppTheme);
                }
                Toast.makeText(context, toasttext, Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void loadHomeFragment(HomeFragment homefragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, homefragment).commit();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.addToBackStack(null);
        getSupportActionBar().setTitle(getString (R.string.app_name));
    }

    private void loadGoalsFragment(GoalsFragment goalsfragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, goalsfragment).commit();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        getSupportActionBar().setTitle(getString (R.string.title_goals));
    }

    private void loadSettingsFragment(SettingsFragment settingsfragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(android.R.id.content, settingsfragment).commit();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        getSupportActionBar().setTitle(getString (R.string.title_settings));
    }

    private void loadStatsFragment(StatsFragment statsfragment){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(android.R.id.content, statsfragment).commit();
        getSupportActionBar().setTitle(getString (R.string.title_stats));
    }

    /*
    * Home fragment listener interface implementation*/
    public void startTimer(View view){
        PomoTimer timer = new PomoTimer(this, 60);
        timer.startTimer();
    }
}
