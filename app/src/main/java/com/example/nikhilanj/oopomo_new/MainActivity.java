package com.example.nikhilanj.oopomo_new;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.goals_package.GoalsFragment;

import java.lang.reflect.Field;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements IgoalFragmentActivityInterface{

    private BottomNavigationView bottomNav;
    float bottomNavDefaultElevation;
    private FragmentManager manager = getSupportFragmentManager();
    private HomeFragment homeFragment = new HomeFragment();
    private GoalsFragment goalsFragment = new GoalsFragment();
    private StatsFragment statsFragment = new StatsFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();

    public static Stack<Integer> bottomNavTabStack = new Stack<>();

    //bottomNavTabStack is the stack where all the tabs are added on clicking.
    //This will be useful to go back to previous tab when back (<-) is pressed
    static MenuItem item1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instantiate room database
        PomoDatabase.getPomoDatabaseInstance(getApplicationContext());

        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomnavigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavDefaultElevation = bottomNav.getElevation();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(android.R.id.content, homeFragment).commit();
        getSupportActionBar().setTitle(getString (R.string.app_name));

        bottomNavTabStack.push(R.id.navigation_home);
    }

    static class BottomNavigationViewHelper {
        @SuppressLint("RestrictedApi")
        static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("BNVHelper", "Unable to get shift mode field", e);
            } catch (IllegalAccessException e) {
                Log.e("BNVHelper", "Unable to change value of shift mode", e);
            }
        }
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
        System.out.println("selected something");
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
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    item.setIcon(R.drawable.ic_fullscreen_exit_white_24px);
                    System.out.println("insane checked");
                }
                else if(!item.isChecked()){
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    item.setIcon(R.drawable.ic_fullscreen_white_24px);
                    System.out.println("insane unchecked");
                }
                return true;
        }
        return true;
    }

    private void loadTabFragment(Fragment fragment,int stringid){

        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
        ft.replace(android.R.id.content, fragment).commit();
        getSupportActionBar().setTitle(getString (stringid));
    }

    @Override
    public void setBottomNavBarElevation(float elev) {
        bottomNav.setElevation(elev);
    }

    @Override
    public float getBottomNavBarDefaultElevation() {
        return bottomNavDefaultElevation;
    }

}
