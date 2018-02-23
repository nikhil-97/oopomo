package com.example.nikhilanj.oopomo_new;

import android.content.Context;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewPropertyAnimator;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


interface timeChangeListenerInterface{
    void updateTimeView(int data);
}


public class HomeFragment extends Fragment implements timeChangeListenerInterface{

    private timerFragmentInterface tfi;
    private getSetTimesInterface gsti;

    public HomeFragment() {} //essential empty constructor

    private FloatingActionButton startButton;
    private FloatingActionButton pauseButton;
    private FloatingActionButton stopButton;

    private BottomSheetDialogFragment timeProfileFragment;

    Timer timer_instance;

    private int f1,s1,l1,r1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Created View", "Not ded");}

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            tfi = (timerFragmentInterface) context;
        } catch (ClassCastException castException) {
            Log.e("ClassCastException","Couldn't attach");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);

        //timeSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet));
        //profilesbutton = (Button) view.findViewById(R.id.timeProfilesButton);

        FloatingActionButton editTimeProfilesButton = view.findViewById(R.id.editTimeProfilesButton);
        startButton = view.findViewById(R.id.startButton);
        pauseButton = view.findViewById(R.id.pauseTimeButton);
        stopButton = view.findViewById(R.id.stopTimeButton);

        editTimeProfilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showTimeSettingsFragment();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pauseButton.setAlpha((float)0.001);
                pauseButton.setVisibility(View.VISIBLE);
                stopButton.setAlpha((float)0.001);
                stopButton.setVisibility(View.VISIBLE);

                buttonFadeAnimation(startButton,(float)0.001,1000,false);
                //startCountdown();
                buttonFadeAnimation(pauseButton,(float)1,1200,true);
                buttonFadeAnimation(stopButton,(float)1,1200,true);
                Toast.makeText(getContext(), "Starting Time !", Toast.LENGTH_SHORT).show();
                try {
                    gsti = (getSetTimesInterface) timeProfileFragment;
                    f1 = gsti.getFocusTime();
                    s1 = gsti.getShortBreakTime();
                    l1 = gsti.getLongBreakTime();
                    r1 = gsti.getRepeats();
                }
                catch(NullPointerException e){
                    System.out.println("Can't get timeprofilesheetfragment. Loading default settings.");
                    List<Integer> default_data = loadDefaultTimeSettings();
                    f1 = default_data.get(1);
                    s1 = default_data.get(2);
                    l1 = default_data.get(3);
                    r1 = default_data.get(4);
                }
                updateTimeView(f1);
                //timer_instance = tfi.startCountdown(f1,s1,l1,r1);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(timer_instance!=null) tfi.pauseCountdown(timer_instance);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showStopAlert();
            }
        });

        return view;
    }

    public void showTimeSettingsFragment() {
        timeProfileFragment = new TimeProfileSheetFragment();
        getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        timeProfileFragment.show(getFragmentManager(), timeProfileFragment.getTag());

    }

    private void buttonFadeAnimation(FloatingActionButton somebutton,float toAlpha,long fadetime,boolean setenable){
        ViewPropertyAnimator buttonanimation = somebutton.animate().alpha(toAlpha).setDuration(fadetime);
        buttonanimation.start();
        somebutton.setEnabled(setenable);
    }

    private void showStopAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage( R.string.stoptrackingdialog_message).setTitle(R.string.stoptrackingdialog_title);
        builder.setPositiveButton(R.string.stoptrackingdialog_quitmsg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {quitTimer();}
        });
        builder.setNeutralButton(R.string.stoptrackingdialog_skipcurrent, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {skipCurrentSession();}
        });
        builder.setNegativeButton(R.string.stoptrackingdialog_nogoback, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {continueTimer();}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void quitTimer(){
        Toast.makeText(getContext(), "stopCountdown()", Toast.LENGTH_SHORT).show();
        buttonFadeAnimation(pauseButton, (float)0.001,1000,false);
        buttonFadeAnimation(stopButton, (float)0.001,1000,false);
        buttonFadeAnimation(startButton,1, 1000,true);
        //TODO : stopCountdown();
        if(timer_instance!=null) tfi.stopFullCountdown(timer_instance);
    }

    private void skipCurrentSession(){
        Toast.makeText(getContext(), "skipSession()", Toast.LENGTH_SHORT).show();
        //TODO : skipSession()
    }

    private void continueTimer(){
        Toast.makeText(getContext(), "resumeCountdown()", Toast.LENGTH_SHORT).show();
        if(timer_instance!=null) tfi.resumeCountdown(timer_instance);
        //TODO : resumeCountdown()
    }

    @Override
    public void updateTimeView(int data) {
        final String[] placeholder_string = new String[]{"placeholder"};
        System.out.println("updateTimeView in fragment");
        placeholder_string[0] = Integer.toString(data)+":00";
        try {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("in getView().post");
                        TextView timeview = getView().findViewById(R.id.current_countdown_time_view);
                        timeview.setText(placeholder_string[0]);
                    }
                    catch (NullPointerException e) {
                        Log.v("Cannot find timeView", "NPE @ find timeView");
                    }
                }
            });

        }
        catch (NullPointerException e) {
            Log.v("Cannot find View", "NPE @ getView().post()");
        }
    }

    @Override
    public void onDetach() {super.onDetach();}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }

    private List<Integer> loadDefaultTimeSettings(){
        List<Integer> defaultSetting = new ArrayList<Integer>();
        defaultSetting.addAll(Arrays.asList(0,25,5,15,4));
        return defaultSetting;
    }

}
