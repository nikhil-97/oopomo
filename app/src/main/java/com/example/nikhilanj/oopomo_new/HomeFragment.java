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

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.entity.PomoProfile;
import com.example.nikhilanj.oopomo_new.lib.PomoTask;
import com.example.nikhilanj.oopomo_new.lib.PomoTimer;
import com.example.nikhilanj.oopomo_new.utils.PomoProfileManager;

public class HomeFragment extends Fragment implements
        PomoTimer.TimerEventsListener,
        TimeProfileSheetFragment.ProfileSheetInteractionListener{

    public HomeFragment() {} //essential empty constructor

    private PomoTimer pomoTimer;
    private PomoTask pomoTask;
    private PomoProfile selectedProfile;
    private PomoProfileManager pomoProfileManager;

    private FloatingActionButton startButton;
    private FloatingActionButton pauseButton;
    private FloatingActionButton stopButton;

    private TextView timeTextView;
    private TextView currentTaskTextView;

    private BottomSheetDialogFragment timeProfileFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pomoProfileManager = new PomoProfileManager(
                PomoDatabase.getPomoDatabaseInstance(getContext())
        );
        this.selectedProfile = pomoProfileManager.getCurrentProfile(getContext());
        this.pomoTimer = new PomoTimer(this.selectedProfile.getFocusTime(), this);
        this.pomoTask = new PomoTask(this.selectedProfile, this.pomoTimer);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);

        FloatingActionButton editTimeProfilesButton = view.findViewById(R.id.editTimeProfilesButton);

        timeTextView = view.findViewById(R.id.current_countdown_time_view);
        currentTaskTextView = view.findViewById(R.id.textView3);

        startButton = view.findViewById(R.id.startButton);
        pauseButton = view.findViewById(R.id.pauseTimeButton);
        stopButton = view.findViewById(R.id.stopTimeButton);

        timeTextView.setText(PomoTimer.getTime(selectedProfile.getFocusTime() * 60));
        currentTaskTextView.setText(pomoTask.getCurrentTask());

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
                buttonFadeAnimation(pauseButton,(float)1,1200,true);
                buttonFadeAnimation(stopButton,(float)1,1200,true);
                Toast.makeText(getContext(), "Starting Time !", Toast.LENGTH_SHORT).show();

                if(pomoTask.isTaskRunning()) {
                    pomoTimer.resumeTimer();
                    return;
                }

                pomoTask.setProfile(selectedProfile);
                pomoTask.startFocus();
                currentTaskTextView.setText(pomoTask.getCurrentTask());
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pomoTimer.pauseTimer();
                preventUnboundedPause();
                buttonFadeAnimation(startButton, 1f,1000,true);
                buttonFadeAnimation(pauseButton,0f,1200,false);
                buttonFadeAnimation(stopButton,1f,1200,true);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pomoTimer.pauseTimer();
                showStopAlert();
            }
        });

        return view;
    }

    public void showTimeSettingsFragment() {
        timeProfileFragment = new TimeProfileSheetFragment();
        getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        timeProfileFragment.show(getChildFragmentManager(), timeProfileFragment.getTag());
    }

    private void buttonFadeAnimation(FloatingActionButton somebutton,float toAlpha,long fadetime,boolean setenable){
        ViewPropertyAnimator buttonanimation = somebutton.animate().alpha(toAlpha).setDuration(fadetime);
        buttonanimation.start();
        somebutton.setEnabled(setenable);
    }

    private void showStopAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage( R.string.stoptrackingdialog_message).setTitle(R.string.stoptrackingdialog_title);

        builder.setPositiveButton(
                R.string.stoptrackingdialog_quitmsg,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        quitTask();
                    }
                });

        builder.setNeutralButton(
                R.string.stoptrackingdialog_skipcurrent,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        skipCurrentSession();
                    }
                });

        builder.setNegativeButton(
                R.string.stoptrackingdialog_nogoback,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        continueTimer();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void quitTask(){
        Toast.makeText(getContext(), "stopCountdown()", Toast.LENGTH_SHORT).show();
        buttonFadeAnimation(pauseButton, 0f,1000,false);
        buttonFadeAnimation(stopButton, 0f,1000,false);
        buttonFadeAnimation(startButton,1f, 1000,true);

        this.pomoTask.resetTask();
        this.timeTextView.setText(PomoTimer.getTime(selectedProfile.getFocusTime() * 60));
        this.currentTaskTextView.setText(this.pomoTask.getCurrentTask());
    }

    private void skipCurrentSession(){
        Toast.makeText(getContext(), "skipSession()", Toast.LENGTH_SHORT).show();
        this.pomoTask.startNextTask();
        this.currentTaskTextView.setText(this.pomoTask.getCurrentTask());
        if(pomoTask.getRepeats() == 0) {
            quitTask();
        }
    }

    private void continueTimer(){
        Toast.makeText(getContext(), "resumeCountdown()", Toast.LENGTH_SHORT).show();
        pomoTimer.resumeTimer();
    }

    public void preventUnboundedPause() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    if(!pomoTimer.isTimerRunning()) {
                        showUnboundedPauseAlert();
                    }
                    return;
                } catch (InterruptedException e) {

                }
            }
        };
        thread.start();
    }

    public void showUnboundedPauseAlert() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Pause time limit exceeded !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDetach() {super.onDetach();}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's state here
    }

    /*
    TimerEventsListener interface implementation
    */
    public void onPomoTimerUpdate() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeTextView.setText( pomoTimer.getTime() );
            }
        });
    }

    public void onPomoTimerTick() {
        this.pomoTask.startNextTask();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentTaskTextView.setText( pomoTask.getCurrentTask() );
                if(pomoTask.getRepeats() == 0) {
                    timeTextView.setText(PomoTimer.getTime(selectedProfile.getFocusTime() * 60));
                }
            }
        });
    }

    /*
    ProfileSheetInteractionListener interface implementation
    */
    public void onSelectedProfileChange(PomoProfile selectedProfile) {
        this.selectedProfile = selectedProfile;
        this.timeTextView.setText( PomoTimer.getTime(selectedProfile.getFocusTime() * 60) );
    }
}
