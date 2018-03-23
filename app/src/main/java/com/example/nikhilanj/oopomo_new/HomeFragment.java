package com.example.nikhilanj.oopomo_new;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.github.lzyzsd.circleprogress.CircleProgress;

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

    private CircleProgress progressCircle;

    private TextView showPausedTextView;
    ViewPropertyAnimator showPausedAnimator;
    private TextView timeTextView;
    private TextView currentTaskTextView;

    private FloatingActionButton startButton;
    private FloatingActionButton pauseButton;
    private FloatingActionButton stopButton;
    private FloatingActionButton skipSessionButton;
    FloatingActionButton editTimeProfilesButton;

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
        progressCircle = view.findViewById(R.id.circleProgress2);

        showPausedTextView = view.findViewById(R.id.tv_show_when_paused);

        timeTextView = view.findViewById(R.id.current_countdown_time_view);
        currentTaskTextView = view.findViewById(R.id.current_session_view);

        startButton = view.findViewById(R.id.startButton);
        pauseButton = view.findViewById(R.id.pauseTimeButton);
        stopButton = view.findViewById(R.id.stopTimeButton);
        skipSessionButton = view.findViewById(R.id.skipSessionButton);
        editTimeProfilesButton = view.findViewById(R.id.editTimeProfilesButton);

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
                animationOnStartClicked();
                startButton.setHapticFeedbackEnabled(true);
                getView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Toast.makeText(getContext(), "Starting Time !", Toast.LENGTH_SHORT).show();
                if(pomoTask.isTaskRunning()) {
                    pomoTimer.resumeTimer();
                    showPausedAnimator.cancel();
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
                animationWhenPaused();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pomoTimer.pauseTimer();
                stopButton.setHapticFeedbackEnabled(true);
                getView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showStopAlert();
            }
        });

        skipSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSkipAlert();
            }
        });

        return view;
    }

    public void showTimeSettingsFragment() {
        timeProfileFragment = new TimeProfileSheetFragment();
        getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        timeProfileFragment.show(getChildFragmentManager(), timeProfileFragment.getTag());
    }

    private void buttonFadeAnimation(View view,float toAlpha,long fadetime,boolean setenable){
        ViewPropertyAnimator buttonanimation = view.animate().alpha(toAlpha).setDuration(fadetime);
        buttonanimation.start();
        view.setEnabled(setenable);
    }

    private void animationOnStartClicked(){
        pauseButton.setAlpha(0.001f);
        pauseButton.setVisibility(View.VISIBLE);
        stopButton.setAlpha(0f);
        stopButton.setVisibility(View.VISIBLE);
        skipSessionButton.setAlpha(0f);
        skipSessionButton.setVisibility(View.VISIBLE);

        ViewPropertyAnimator timeViewSizeAnimator = timeTextView.animate().scaleX(1.3f).scaleY(1.3f).setDuration(2000);
        timeViewSizeAnimator.setInterpolator(new DecelerateInterpolator());
        timeViewSizeAnimator.start();
        //showPausedAnimator.cancel();
        buttonFadeAnimation(editTimeProfilesButton,0f,500,false);
        buttonFadeAnimation(startButton,0f,1000,false);
        buttonFadeAnimation(pauseButton,1.0f,1200,true);
        buttonFadeAnimation(stopButton,1.0f,1200,true);
        buttonFadeAnimation(skipSessionButton,1.0f,1200,true);
    }

    private void animationWhenPaused(){
        int DURATION_PER_CYCLE = 1500;
        int NUM_CYCLES = 100;
        showPausedTextView.setText("PAUSED");
        showPausedAnimator = showPausedTextView.animate().alpha(1f).setDuration(DURATION_PER_CYCLE*NUM_CYCLES);
        showPausedAnimator.setInterpolator(new CycleInterpolator(100));
        showPausedAnimator.start();
        buttonFadeAnimation(startButton, 1f,1000,true);
        buttonFadeAnimation(pauseButton,0f,1200,false);
        buttonFadeAnimation(stopButton,1f,1200,true);
    }

    private void showStopAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String stopMessage = getString(R.string.stoptrackingdialog_message);
        builder.setMessage(stopMessage).setTitle(R.string.stoptrackingdialog_title);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                quitTask();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                continueTimer();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void quitTask(){
        Toast.makeText(getContext(), "stopCountdown()", Toast.LENGTH_SHORT).show();
        animationOnQuitTask();
        this.pomoTask.resetTask();
        this.timeTextView.setText(PomoTimer.getTime(selectedProfile.getFocusTime() * 60));
        this.currentTaskTextView.setText(this.pomoTask.getCurrentTask());
    }

    private void animationOnQuitTask(){
        buttonFadeAnimation(pauseButton, 0f,800,false);
        buttonFadeAnimation(stopButton, 0f,800,false);
        buttonFadeAnimation(skipSessionButton, 0f,800,false);

        buttonFadeAnimation(startButton, 1f,1200,true);
        buttonFadeAnimation(editTimeProfilesButton,1f,1200,true);

        showPausedTextView.setAlpha(0.001f);
        ViewPropertyAnimator timeViewSizeAnimator = timeTextView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1500);
        timeViewSizeAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        timeViewSizeAnimator.start();
    }

    private void showSkipAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String currentSession = "FOCUS 1";
        String stopMessage = "Skip current session ?\nThis will erase your progress in this session.";
        builder.setMessage(stopMessage).setTitle("Skip FOCUS 1");
        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        skipCurrentSession();
                    }
                });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {continueTimer();}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String stopMessage = "You have paused work for too long !\n";
                builder.setMessage(stopMessage).setTitle("Get back to work !");
                builder.setPositiveButton("BACK TO WORK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                pomoTimer.resumeTimer();
                                showPausedTextView.setText("RESUMED");
                                //showPausedTextView.setAlpha(1f);
                                //showPausedAnimator.cancel();
                                Animation blinkAnimation = new AlphaAnimation(1,0);
                                blinkAnimation.setDuration(1500);
                                blinkAnimation.setInterpolator(new LinearInterpolator());
                                showPausedTextView.setAnimation(blinkAnimation);

                                //ViewPropertyAnimator showResumed =  showPausedTextView.animate().alpha(0.1f).setDuration(2000);
                                //showResumed.setStartDelay(2000);
                                //showResumed.start();
                            }
                        });
                builder.setNegativeButton("SNOOZE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO : restartUnboundedPauseCounter;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
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
        try {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    float fraction = (float)pomoTimer.getCurrentCountdownTime()/pomoTimer.getTotalCountdownTime();
                    int progress = (int)(fraction*100);
                    System.out.println(pomoTimer.getCurrentCountdownTime()+"/"+
                            pomoTimer.getTotalCountdownTime()+"="+
                            fraction+"=>"+
                            progress);
                    timeTextView.setText(pomoTimer.getTimeString());
                    progressCircle.setProgress(progress);
                }
            });
        }
        catch(NullPointerException e){Log.e("Can't update time",Log.getStackTraceString(e));}
    }

    public void onPomoTimerTick() {
        this.pomoTask.startNextTask();
        try {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    currentTaskTextView.setText(pomoTask.getCurrentTask());
                    if (pomoTask.getRepeats() == 0) {
                        timeTextView.setText(PomoTimer.getTime(selectedProfile.getFocusTime() * 60));
                    }
                }
            });
        }
        catch(NullPointerException e){Log.e("Can't update time",Log.getStackTraceString(e));}
    }

    /*
    ProfileSheetInteractionListener interface implementation
    */
    public void onSelectedProfileChange(PomoProfile selectedProfile) {
        this.selectedProfile = selectedProfile;
        this.timeTextView.setText( PomoTimer.getTime(selectedProfile.getFocusTime() * 60) );
    }
}
