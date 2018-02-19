package com.example.nikhilanj.oopomo_new;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Stack;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

interface timeChangeNotifierInterface{
    void registerInterface(timeChangeListenerInterface listener);
}

public class MainActivity extends AppCompatActivity implements timeChangeListenerInterface{

    private BottomNavigationView bottomnav;
    private FragmentManager manager = getSupportFragmentManager();
    private HomeFragment homefragment = new HomeFragment();
    private GoalsFragment goalsfragment = new GoalsFragment();
    private StatsFragment statsfragment = new StatsFragment();
    private SettingsFragment settingsfragment = new SettingsFragment();

    public static Stack<Integer> bottomnavtabstack = new Stack<>();
    //bottomnavtabstack is the stack where all the tabs are added on clicking.
    //This will be useful to go back to previous tab when back (<-) is pressed
    static MenuItem item1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomnav = findViewById(R.id.bottomnavigation);
        bottomnav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(android.R.id.content, homefragment).commit();
        getSupportActionBar().setTitle(getString (R.string.app_name));

        bottomnavtabstack.push(R.id.navigation_home);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            item1 = item;

            System.out.println(bottomnavtabstack);
            if(!bottomnavtabstack.empty()) {
                //This if-statement is there because Stack.peek() throws EmptyStackException when stack is empty
                if (bottomnavtabstack.peek() != item.getItemId())
                    bottomnavtabstack.push(item.getItemId());
            }
            else{
                //push id to stack regardless
                bottomnavtabstack.push(item.getItemId());
            }

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadTabFragment(homefragment,R.string.title_home);
                    return true;
                case R.id.navigation_goals:
                    loadTabFragment(goalsfragment,R.string.title_goals);
                    return true;
                case R.id.navigation_stats:
                    loadTabFragment(statsfragment,R.string.title_stats);
                    return true;
                case R.id.navigation_settings:
                    loadTabFragment(settingsfragment,R.string.title_settings);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        System.out.println("backpressed="+bottomnavtabstack);
        if (bottomnavtabstack.size()>1){
            int popid = bottomnavtabstack.pop();
            //This while loop is there to prevent self-referential pops in stack popping, i.e.
            // the popid refers to the current tab itself, and prevents any further movement.
            //Don't change this unless you are absolutely sure of what you're doing. I am not :)
            while(bottomnav.findViewById(popid).getId()==item1.getItemId() && !bottomnavtabstack.empty())
            {popid= bottomnavtabstack.pop();}

            bottomnav.findViewById(popid).performClick();
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

    @Override
    public void updateTimeView(int data) {
        //TODO
    }
}
