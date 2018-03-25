package com.example.nikhilanj.oopomo_new.lib;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class PomoTimer {
    private Timer timer;
    private TimerTask task;
    private int countdownTime, currentCountdown;
    private boolean isTimerRunning;
    private TimerEventsListener mListener;

    public PomoTimer(int countdownTime, TimerEventsListener mListener) {
        this.mListener = mListener;
        this.countdownTime = countdownTime;
        this.task = new PomoTimerTask();
    }

    /*
    * Getters
    * */
    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public int getCurrentCountdown() {
        return currentCountdown;
    }

    /*
        * Setters
        * */
    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
    }

    public String getTime() {
        int minutes = currentCountdown/60;
        int seconds = currentCountdown % 60;
        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    public static String getTime(int timeInSeconds) {
        int minutes = timeInSeconds/60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    public void startTimer() {

        if(this.isTimerRunning()) {
            this.timer.cancel();
        }

        this.timer = new Timer();
        this.task = new PomoTimerTask();
        this.currentCountdown = countdownTime;
        this.timer.scheduleAtFixedRate(task, 0, 1000);
        this.isTimerRunning = true;
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

    public void notifyTimerListeners(){
        this.mListener.onPomoTimerUpdate();
    }

    class PomoTimerTask extends TimerTask {
        public void run() {
            if( currentCountdown == 0 ) {
                timer.cancel();
                isTimerRunning = false;
                mListener.onPomoTimerTick();
                return;
            }
            currentCountdown--;
            notifyTimerListeners();
        }
    }

    /*
     *  This interface must be implemented
     *  by the class that listens to the timer update events
     */
    public interface TimerEventsListener {
        void onPomoTimerUpdate();
        void onPomoTimerTick();
    }
}