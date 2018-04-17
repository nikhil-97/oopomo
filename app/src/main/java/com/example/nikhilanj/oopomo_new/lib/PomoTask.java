package com.example.nikhilanj.oopomo_new.lib;

import android.content.Context;
import android.util.Log;

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.entity.PomoProfile;
import com.example.nikhilanj.oopomo_new.db.entity.StatObject;

public class PomoTask {

    private static final int TASK_FOCUS = 0;
    private static final int TASK_SHORT_BREAK = 1;
    private static final int TASK_LONG_BREAK = 2;

    private static PomoTask pomoTask = null;

    private int focusTime, shortBreakTime, longBreakTime, repeats;
    private int currentTask = TASK_FOCUS;
    private int currentFocus = 1;
    private boolean shortBreakDone;
    private boolean taskRunning = false;

    private PomoTimer pomoTimer;

    private StatObject statObject = null;
    private long statObjectId;

    PomoTask(int focusTime, int shortBreakTime, int longBreakTime, int repeats, PomoTimer pomoTimer){
        this.focusTime = focusTime;
        this.shortBreakTime = shortBreakTime;
        this.longBreakTime = longBreakTime;
        this.repeats = repeats;
        this.pomoTimer = pomoTimer;
    }

    private PomoTask(PomoProfile pomoProfile, PomoTimer pomoTimer) {
        this.focusTime = pomoProfile.getFocusTime();
        this.shortBreakTime = pomoProfile.getShortBreakTime();
        this.longBreakTime = pomoProfile.getLongBreakTime();
        this.repeats = pomoProfile.getRepeats();
        this.pomoTimer = pomoTimer;
    }

    public static PomoTask getPomoTask(PomoProfile pomoProfile, PomoTimer pomoTimer) {
        if( pomoTask != null) {
            return pomoTask;
        }
        pomoTask = new PomoTask(pomoProfile, pomoTimer);
        return pomoTask;
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
        this.statObject = null;
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

    public void updateStatObject(Context context) {
        PomoDatabase pomoDatabase = PomoDatabase.getPomoDatabaseInstance(context);

        if (this.statObject == null) {
            this.statObject = new StatObject(System.currentTimeMillis(), this.focusTime, 0);
            this.statObjectId = pomoDatabase.statDao().insert(this.statObject);
            this.statObject.setStatId(statObjectId);
            return;
        }

        if (this.currentTask == TASK_FOCUS) {
            this.statObject.setFocusTime(this.statObject.getFocusTime() + this.focusTime);
            pomoDatabase.statDao().updateStatObject(statObject);
            return;
        }

        else if (this.currentTask == TASK_LONG_BREAK) {
            this.statObject.setBreakTime(this.statObject.getBreakTime() + this.longBreakTime);
            pomoDatabase.statDao().updateStatObject(statObject);
            return;
        }

        else if (this.currentTask == TASK_SHORT_BREAK) {
            this.statObject.setBreakTime(this.statObject.getBreakTime() + this.shortBreakTime);
            pomoDatabase.statDao().updateStatObject(statObject);
            return;
        }
    }
}
