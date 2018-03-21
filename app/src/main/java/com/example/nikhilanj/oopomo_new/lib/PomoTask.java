package com.example.nikhilanj.oopomo_new.lib;

import android.util.Log;

import com.example.nikhilanj.oopomo_new.db.entity.PomoProfile;

public class PomoTask {

    private static final int TASK_FOCUS = 0;
    private static final int TASK_SHORT_BREAK = 1;
    private static final int TASK_LONG_BREAK = 2;

    private int focusTime, shortBreakTime, longBreakTime, repeats;
    private int currentTask = TASK_FOCUS;
    private int currentFocus = 1;
    private boolean shortBreakDone;
    private boolean taskRunning = false;
    private PomoTimer pomoTimer;

    PomoTask(int focusTime, int shortBreakTime, int longBreakTime, int repeats, PomoTimer pomoTimer){
        this.focusTime = focusTime;
        this.shortBreakTime = shortBreakTime;
        this.longBreakTime = longBreakTime;
        this.repeats = repeats;
        this.pomoTimer = pomoTimer;
    }

    public PomoTask(PomoProfile pomoProfile, PomoTimer pomoTimer) {
        this.focusTime = pomoProfile.getFocusTime();
        this.shortBreakTime = pomoProfile.getShortBreakTime();
        this.longBreakTime = pomoProfile.getLongBreakTime();
        this.repeats = pomoProfile.getRepeats();
        this.pomoTimer = pomoTimer;
    }

    public boolean isTaskRunning() {
        return taskRunning;
    }

    public int getRepeats() {
        return repeats;
    }

    public void setTaskRunning(boolean taskRunning) {
        this.taskRunning = taskRunning;
    }

    public void setProfile(PomoProfile pomoProfile) {
        this.focusTime = pomoProfile.getFocusTime();
        this.shortBreakTime = pomoProfile.getShortBreakTime();
        this.longBreakTime = pomoProfile.getLongBreakTime();
        this.repeats = pomoProfile.getRepeats();
    }

    public String getCurrentTask() {
        String currentTask = "";
        switch (this.currentTask) {
            case TASK_FOCUS:
                currentTask =  "FOCUS " + currentFocus;
                break;
            case TASK_SHORT_BREAK:
                currentTask =  "SHORT BREAK";
                break;
            case TASK_LONG_BREAK:
                currentTask =  "LONG BREAK";
                break;
        }
        return currentTask;
    }

    public void startFocus() {
        this.taskRunning = true;
        this.currentTask = TASK_FOCUS;
        this.pomoTimer.setCountdownTime(focusTime * 60);
        this.pomoTimer.startTimer();
    }

    public void skipFocus() {
        this.repeats--;
        if(this.repeats == 0) {
            return;
        }
        this.currentFocus++;
        this.shortBreakDone = false;
        this.startFocus();
    }

    public void startShortBreak() {
        this.currentTask = TASK_SHORT_BREAK;
        this.pomoTimer.setCountdownTime(shortBreakTime * 60);
        this.pomoTimer.startTimer();
    }

    public void startLongBreak() {
        this.currentTask = TASK_LONG_BREAK;
        this.pomoTimer.setCountdownTime(longBreakTime * 60);
        this.pomoTimer.startTimer();
    }

    public void resetTask() {
        this.pomoTimer.pauseTimer();
        this.currentFocus = 1;
        this.setTaskRunning(false);
        this.currentTask = TASK_FOCUS;
    }

    public void startNextTask() {

        switch (currentTask) {
            case TASK_FOCUS:
                this.repeats--;
                this.currentFocus++;
                if(this.repeats == 0) {
                    resetTask();
                    return;
                }
                if(shortBreakDone) {
                    startLongBreak();
                } else {
                    startShortBreak();
                }
                break;
            case TASK_SHORT_BREAK:
                this.shortBreakDone = true;
                startFocus();
                break;
            case TASK_LONG_BREAK:
                this.shortBreakDone = false;
                startFocus();
                break;
        }

    }
}
