package com.example.nikhilanj.oopomo_new.lib;

import android.app.Activity;
import android.content.Context;
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
        this.isTimerRunning = true;
        this.timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void resetTimer() {
        if( isTimerRunning ) {
            this.pauseTimer();
            this.currentCountdown = this.countdownTime;
        }
        else {
            this.currentCountdown = this.countdownTime;
        }
    }

    public void updateViews(){
        timerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arcProgress.setProgress((countdownTime-currentCountdown)*100/countdownTime);
                timerText.setText(getTime());
            }
        });
    }

    class PomoTimerTask extends TimerTask {
        public void run() {
            if( currentCountdown == 0) {
                timer.cancel();
                isTimerRunning = false;
                resetTimer();
                return;
            }
            currentCountdown--;
            updateViews();
        }
    }
}