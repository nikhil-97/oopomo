package com.example.nikhilanj.oopomo_new.lib;

public class PomoTask{
    private int focusTime, breakTime, longBreakTime;
    private int repititions;
    private PomoTimer pomoTimer;

    PomoTask(int focusTime, int breakTime, int longBreakTime, int repetitions, PomoTimer pomoTimer){
        this.focusTime = focusTime;
        this.breakTime = breakTime;
        this.longBreakTime = longBreakTime;
        this.repititions = repetitions;
        this.pomoTimer = pomoTimer;
    }

    void startPomoTaskFocus(){
        this.pomoTimer.setCountdownTime(focusTime);
        this.pomoTimer.startTimer();
    }

    void startPomoTaskBreak(){
        this.pomoTimer.setCountdownTime(breakTime);
        this.pomoTimer.startTimer();
    }

    void startPomoTaskLongBreak(){
        this.pomoTimer.setCountdownTime(breakTime);
        this.pomoTimer.startTimer();
    }

    void startNextTask(){
    }
}
