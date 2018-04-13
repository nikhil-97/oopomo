package com.example.nikhilanj.oopomo_new;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.github.lzyzsd.circleprogress.CircleProgress;

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.entity.PomoProfile;
import com.example.nikhilanj.oopomo_new.lib.PomoTask;
import com.example.nikhilanj.oopomo_new.lib.PomoTimer;
import com.example.nikhilanj.oopomo_new.utils.PomoProfileManager;

class ReverseInterpolator implements Interpolator{
    @Override
    public float getInterpolation(float v) {
        return Math.abs(v - 1f);
    }
}
public class HomeFragment extends Fragment implements
        PomoTimer.TimerEventsListener,
        TimeProfileSheetFragment.ProfileSheetInteractionListener{

    private static int UNBOUNDED_PAUSE_TIME_MINUTES = 1000;

    final static int COUNTDOWN_NOT_RUNNING  = 1;
    final static int COUNTDOWN_RUNNING      = 2;
    final static int COUNTDOWN_PAUSED       = 3;
    final static int COUNTDOWN_RESUMED      = 4;


    public HomeFragment() {} //essential empty constructor

    private PomoTimer pomoTimer;
    private PomoTask pomoTask;
    private PomoProfile selectedProfile;
    private PomoProfileManager pomoProfileManager;

    private TextView workOnSpecificGoalTextView;

    private CircleProgress progressCircle;
    private FloatingActionButton editTimeProfilesButton;

    private TextView showPausedTextView;
    ViewPropertyAnimator showPausedAnimator;
    private TextView timeTextView;
    private TextView currentTaskTextView;

    private FloatingActionButton startButton;
    private FloatingActionButton pauseButton;
    private FloatingActionButton stopButton;
    private FloatingActionButton skipSessionButton;

    private BottomSheetDialogFragment timeProfileFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pomoProfileManager = new PomoProfileManager(
                PomoDatabase.getPomoDatabaseInstance(getContext())
        );
        this.selectedProfile = pomoProfileManager.getCurrentProfile(getContext());
        this.pomoTimer = PomoTimer.getPomoTimer(this.selectedProfile.getFocusTime(), this);
        this.pomoTask = PomoTask.getPomoTask(this.selectedProfile, this.pomoTimer);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);
        workOnSpecificGoalTextView = view.findViewById(R.id.work_on_specific_goal);
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

        workOnSpecificGoalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();
                activity.bottomNav.findViewById(R.id.navigation_goals).performClick();
            }
        });

        editTimeProfilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {showTimeSettingsFragment();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                animateViewsOnChange(COUNTDOWN_RUNNING);
                startButton.setHapticFeedbackEnabled(true);
                getView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Toast.makeText(getContext(), "Starting Time !", Toast.LENGTH_SHORT).show();
                if(pomoTask.isTaskRunning()) {
                    animateViewsOnChange(COUNTDOWN_RESUMED);
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
                animateViewsOnChange(COUNTDOWN_PAUSED);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                boolean isTimerRunning = pomoTimer.isTimerRunning();
                pomoTimer.pauseTimer();
                stopButton.setHapticFeedbackEnabled(true);
                getView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                showStopAlert();
                /*
                * Resume timer if task is not quit and timer is running before "STOP" dialog
                */
                if(pomoTask.isTaskRunning() && isTimerRunning) {
                    pomoTimer.resumeTimer();
                }
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

    private void buttonFadeAnimation(View view,float toAlpha,long fadetime,boolean setenable) {
        view.setEnabled(setenable);
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
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void quitTask(){
        Toast.makeText(getContext(), "stopCountdown()", Toast.LENGTH_SHORT).show();
        animateViewsOnChange(COUNTDOWN_NOT_RUNNING);
        this.pomoTask.resetTask();
        this.timeTextView.setText(PomoTimer.getTime(selectedProfile.getFocusTime() * 60));
        this.currentTaskTextView.setText(this.pomoTask.getCurrentTask());
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
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
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

    public void preventUnboundedPause() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(UNBOUNDED_PAUSE_TIME_MINUTES*60*1000);
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

    @SuppressLint("NewApi")
    private void animateViewsOnChange(int nextState){

        ObjectAnimator startButtonAnimator = ObjectAnimator.ofFloat(startButton,startButton.ALPHA,1,0);
        ObjectAnimator editTimeProfilesButtonAnimator= ObjectAnimator.ofFloat(editTimeProfilesButton,editTimeProfilesButton.ALPHA,1,0);

        ObjectAnimator pauseButtonAnimator = ObjectAnimator.ofFloat(pauseButton,pauseButton.ALPHA,0,1);
        ObjectAnimator stopButtonAnimator = ObjectAnimator.ofFloat(stopButton,stopButton.ALPHA,0,1);
        ObjectAnimator skipSessionButtonAnimator = ObjectAnimator.ofFloat(skipSessionButton,skipSessionButton.ALPHA,0,1);

        AnimatorSet buttonAnimationSet = new AnimatorSet();
        buttonAnimationSet.playTogether(startButtonAnimator,editTimeProfilesButtonAnimator,
                pauseButtonAnimator,stopButtonAnimator,skipSessionButtonAnimator);
        buttonAnimationSet.setDuration(1500);

        AnimatorSet onPausedAnimatorSet = new AnimatorSet();
        onPausedAnimatorSet.playTogether(startButtonAnimator,pauseButtonAnimator);
        onPausedAnimatorSet.setInterpolator(new ReverseInterpolator());


        if(nextState==COUNTDOWN_NOT_RUNNING){

            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            skipSessionButton.setEnabled(false);
            buttonAnimationSet.setInterpolator(new ReverseInterpolator());
            buttonAnimationSet.start();

            showPausedTextView.setAlpha(0.001f);
            ViewPropertyAnimator timeViewSizeAnimator = timeTextView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1500);
            timeViewSizeAnimator.setInterpolator(new LinearOutSlowInInterpolator());
            timeViewSizeAnimator.start();
        }
        else if(nextState==COUNTDOWN_RUNNING){

            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            skipSessionButton.setVisibility(View.VISIBLE);

            if(showPausedAnimator != null) showPausedAnimator.cancel();

            ViewPropertyAnimator timeViewSizeAnimator = timeTextView.animate().scaleX(1.3f).scaleY(1.3f).setDuration(2000);
            timeViewSizeAnimator.setInterpolator(new DecelerateInterpolator());
            timeViewSizeAnimator.start();

            buttonAnimationSet.setInterpolator(new LinearOutSlowInInterpolator());
            buttonAnimationSet.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);

        }
        else if(nextState==COUNTDOWN_PAUSED){
            int DURATION_PER_CYCLE = 1500;
            int NUM_CYCLES = (int)((UNBOUNDED_PAUSE_TIME_MINUTES*60*1000)/DURATION_PER_CYCLE);
            showPausedTextView.setText(R.string.paused_text);
            showPausedAnimator = showPausedTextView.animate().alpha(1f).setDuration(UNBOUNDED_PAUSE_TIME_MINUTES*60*1000);
            showPausedAnimator.setInterpolator(new CycleInterpolator(NUM_CYCLES));
            showPausedAnimator.start();

            pauseButton.setEnabled(false);
            startButton.setEnabled(true);
            onPausedAnimatorSet.start();
        }
        else if(nextState==COUNTDOWN_RESUMED){
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
            skipSessionButton.setEnabled(true);
            startButton.setEnabled(false);
            showPausedTextView.setAlpha(1f);
            showPausedTextView.setText("RESUMED");
            buttonFadeAnimation(showPausedTextView,0f,2000,true);

            onPausedAnimatorSet.start();
            //animateViewsOnChange(COUNTDOWN_RUNNING);
        }
    }

    private void setProgressOfCircle(float time){
        float fraction = time/pomoTimer.getTotalCountdownTime();
        int progress = (int)(fraction*100);
        System.out.println(pomoTimer.getCurrentCountdownTime()+"/"+
                pomoTimer.getTotalCountdownTime()+"="+
                fraction+"=>"+
                progress);
        progressCircle.setProgress(progress);
    }

    public void showUnboundedPauseAlert() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String stopMessage = "You have paused work for too long\n";
                builder.setMessage(stopMessage).setTitle("Get back to work !");
                builder.setPositiveButton("BACK TO WORK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                animateViewsOnChange(COUNTDOWN_RESUMED);
                                pomoTimer.resumeTimer();

                            }
                        });
                builder.setNegativeButton("SNOOZE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        preventUnboundedPause();
                        animateViewsOnChange(COUNTDOWN_PAUSED);
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

                    setProgressOfCircle((float)pomoTimer.getCurrentCountdownTime());
                    timeTextView.setText(pomoTimer.getTimeString());
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
