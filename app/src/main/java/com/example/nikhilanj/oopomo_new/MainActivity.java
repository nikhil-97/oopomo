package com.example.nikhilanj.oopomo_new;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;

import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.nikhilanj.oopomo_new.goals_package.GoalsFragment;

import java.util.List;
import java.util.Stack;

interface IGoalFragmentInterface{
    List getGoalsListFromMainActivity();

}
public class MainActivity extends AppCompatActivity implements timerFragmentInterface{

    private BottomNavigationView bottomNav;
    private FragmentManager manager = getSupportFragmentManager();
    private HomeFragment homeFragment = new HomeFragment();
    private GoalsFragment goalsFragment = new GoalsFragment();
    private StatsFragment statsFragment = new StatsFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();

    public static Stack<Integer> bottomNavTabStack = new Stack<>();
    private timeChangeListenerInterface tcli;
    Timer mainTimer;
    //bottomNavTabStack is the stack where all the tabs are added on clicking.
    //This will be useful to go back to previous tab when back (<-) is pressed
    static MenuItem item1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomnavigation);
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(android.R.id.content, homeFragment).commit();
        getSupportActionBar().setTitle(getString (R.string.app_name));

        bottomNavTabStack.push(R.id.navigation_home);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            item1 = item;

            System.out.println(bottomNavTabStack);
            if(!bottomNavTabStack.empty()) {
                //This if-statement is there because Stack.peek() throws EmptyStackException when stack is empty
                if (bottomNavTabStack.peek() != item.getItemId())
                    bottomNavTabStack.push(item.getItemId());
            }
            else{
                //push id to stack regardless
                bottomNavTabStack.push(item.getItemId());
            }

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadTabFragment(homeFragment,R.string.title_home);
                    return true;
                case R.id.navigation_goals:
                    loadTabFragment(goalsFragment,R.string.title_goals);
                    return true;
                case R.id.navigation_stats:
                    loadTabFragment(statsFragment,R.string.title_stats);
                    return true;
                case R.id.navigation_settings:
                    loadTabFragment(settingsFragment,R.string.title_settings);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        System.out.println("backpressed="+bottomNavTabStack);
        if (bottomNavTabStack.size()>1){
            int popid = bottomNavTabStack.pop();
            //This while loop is there to prevent self-referential pops in stack popping, i.e.
            // the popid refers to the current tab itself, and prevents any further movement.
            //Don't change this unless you are absolutely sure of what you're doing. I am not :)
            while(bottomNav.findViewById(popid).getId()==item1.getItemId() && !bottomNavTabStack.empty())
            {popid= bottomNavTabStack.pop();}

            bottomNav.findViewById(popid).performClick();
        }
        else super.onBackPressed();
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
                Toast.makeText(getBaseContext(), toasttext, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.fullscreen_setting:
                item.setChecked(!item.isChecked());
                if(item.isChecked()){
                    final View decorView = getWindow().getDecorView();
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
                    System.out.println("insane checked");
                }
                else{
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    System.out.println("insane unchecked");
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTabFragment(Fragment fragment,int stringid){
        manager.beginTransaction().replace(android.R.id.content, fragment).commit();
        getSupportActionBar().setTitle(getString (stringid));
    }

    public void updateTimeViewInHomeFragment(int data){
        tcli = homeFragment;
        tcli.updateTimeView(data);
    }

    @Override
    public Timer startCountdown(int f,int s,int l,int r){
        this.mainTimer = new Timer(f,s,l,r);
        return this.mainTimer;
    }

    @Override
    public void pauseCountdown(Timer timerinstance){timerinstance.pauseTimer();}

    @Override
    public void resumeCountdown(Timer timerinstance){timerinstance.resumeTimer();}

    @Override
    public void stopFullCountdown(Timer timerinstance){timerinstance.stopTimer();}

    /**
     * Implementation of HomeFragment interaction listener
     */
    /*PomoTimer pomoTimer;
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
    }*/
}
