package com.example.nikhilanj.oopomo_new.lib;

import java.util.Timer;
import java.util.TimerTask;

class PomoTimer {
    Timer timer;
    TimerTask task;
    int countdownTime, breakTime, totalTime, currentCountdown;
    boolean isTimerRunning;

    PomoTimer(int countdownTime, int breakTime, int totalTime) {
        this.countdownTime = countdownTime;
        this.breakTime = breakTime;
        this.totalTime = totalTime;
        this.timer = new Timer();
        this.task = new PomoTimerTask();
    }

    void startTimer() {
        this.currentCountdown = countdownTime;
        this.isTimerRunning = true;
        this.timer.scheduleAtFixedRate(task, 0, 1000);
    }

    void pauseTimer() {
        this.timer.cancel();
        this.isTimerRunning = false;
    }

    void resetTimer() {
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
            if( currentCountdown == 20) {
                timer.cancel();
                resetTimer();
            }
            System.out.println("Count down time: " + currentCountdown);
            currentCountdown--;
        }
    }
}