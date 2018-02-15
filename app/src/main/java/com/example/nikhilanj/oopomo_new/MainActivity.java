package com.example.nikhilanj.oopomo_new;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.transition.Fade;
import android.transition.Transition;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nikhilanj.oopomo_new.lib.PomoTimer;

public class MainActivity extends AppCompatActivity implements
        HomeFragment.OnFragmentInteractionListener,
        PomoTimer.PomoTimerEventsListener{

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
     *Implementation of PomoTimerEventListener
     */
    public void onPomoTimerUpdate(){
        Log.d("interaction", "onPomoTimerUpdate invoked");
    }

    /**
     * Implementation of HomeFragment interaction listener
     */
    PomoTimer pomoTimer;
    public void startTimer(View view){
        if( this.pomoTimer != null ){
            if(this.pomoTimer.isTimerRunning()){
                this.pauseTimer(view);
            }
            else {
                this.resumeTimer(view);
            }
            return;
        }
        this.pomoTimer = new PomoTimer(60, this);
        this.pomoTimer.startTimer();
        FloatingActionButton timerActionButton = view.findViewById(R.id.timer_action_button);
        timerActionButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.ic_pause_black_24dp,
                        null
                )
        );
    }

    public void pauseTimer(View view){
        this.pomoTimer.pauseTimer();
        FloatingActionButton timerActionButton = view.findViewById(R.id.timer_action_button);
        timerActionButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.ic_play_arrow_white_24dp,
                        null
                )
        );
    }

    public void resumeTimer(View view){
        this.pomoTimer.resumeTimer();
        FloatingActionButton timerActionButton = view.findViewById(R.id.timer_action_button);
        timerActionButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.ic_pause_black_24dp,
                        null
                )
        );
    }
}
