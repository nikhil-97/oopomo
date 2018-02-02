package com.example.nikhilanj.oopomo_new.lib;

import java.util.Timer;
import java.util.TimerTask;

public class PomoTimer {
    Timer timer;
    TimerTask task;
    int countdownTime, breakTime, totalTime, currentCountdown;
    public boolean isTimerRunning;

    public PomoTimer(int countdownTime, int breakTime, int totalTime) {
        this.countdownTime = countdownTime;
        this.breakTime = breakTime;
        this.totalTime = totalTime;
        this.timer = new Timer();
        this.task = new PomoTimerTask();
        System.out.println(countdownTime);
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

    public void resetTimer() {
        if( isTimerRunning ) {
            return;
        }
        else {
            timer.cancel();
            this.currentCountdown = this.countdownTime;
        }
    }
    class PomoTimerTask extends TimerTask {
        public void run() {
            if( currentCountdown == 0) {
                timer.cancel();
                resetTimer();
            }
            System.out.println("Count down time: " + getTime());
            currentCountdown--;
        }
    }
}