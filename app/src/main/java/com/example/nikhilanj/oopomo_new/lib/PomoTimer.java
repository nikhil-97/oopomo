package com.example.nikhilanj.oopomo_new.lib;

import java.util.Timer;
import java.util.TimerTask;

public class PomoTimer {
    private Timer timer;
    private TimerTask task;
    private int countdownTime, currentCountdown;
    private boolean isTimerRunning;
    private PomoTimerEventsListener mListener;

    public PomoTimer(int countdownTime, PomoTimerEventsListener mListener) {
        this.mListener = mListener;
        this.countdownTime = countdownTime;
        this.timer = new Timer();
        this.task = new PomoTimerTask();
    }

    /*
    * Getters
    * */
    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    /*
    * Setters
    * */
    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
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
        this.mListener.onPomoTimerUpdate();
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

    /*
     *  This interface must be implemented
     *  by the class that listens to the timer update events
     */
    public interface PomoTimerEventsListener{
        void onPomoTimerUpdate();
    }
}