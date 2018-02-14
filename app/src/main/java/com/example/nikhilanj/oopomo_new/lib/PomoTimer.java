package com.example.nikhilanj.oopomo_new.lib;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.nikhilanj.oopomo_new.R;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.Timer;
import java.util.TimerTask;

public class PomoTimer {
    private Activity timerActivity;
    private ArcProgress arcProgress;
    private TextView timerText;
    private Timer timer;
    private TimerTask task;
    private int countdownTime, currentCountdown;
    private boolean isTimerRunning;

    public PomoTimer(Context context, int countdownTime) {
        this.timerActivity = (Activity) context;
        this.arcProgress = timerActivity.findViewById(R.id.arc_progress);
        this.timerText = timerActivity.findViewById(R.id.timeView);
        this.countdownTime = countdownTime;
        this.timer = new Timer();
        this.task = new PomoTimerTask();
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void updateUiElements(View view) {
        this.timerActivity = (Activity) view.getContext();
        this.arcProgress = view.findViewById(R.id.arc_progress);
        this.timerText = view.findViewById(R.id.timeView);
    }

    public String getTime() {
        int minutes = (int) (currentCountdown/60);
        int seconds = currentCountdown % 60;
        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    public void startTimer() {
        this.currentCountdown = countdownTime;
        this.isTimerRunning = true;
        this.timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void pauseTimer() {
        this.timer.cancel();
        this.isTimerRunning = false;
    }

    public void resumeTimer(){
        if( this.isTimerRunning() ){
            return;
        }
        this.timer = new Timer();
        this.task = new PomoTimerTask();
        this.timer.scheduleAtFixedRate(task, 0, 1000);
        this.isTimerRunning = true;
    }

    public void updateViews(){
        timerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("timer_debug", "Thread running");
                arcProgress.setProgress((countdownTime-currentCountdown)*100/countdownTime);
                timerText.setText(getTime());
            }
        });
    }

    class PomoTimerTask extends TimerTask {
        public void run() {
            if( currentCountdown == 0 ) {
                timer.cancel();
                isTimerRunning = false;
                return;
            }
            currentCountdown--;
            updateViews();
        }
    }
}