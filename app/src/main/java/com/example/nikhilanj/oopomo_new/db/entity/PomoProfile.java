package com.example.nikhilanj.oopomo_new.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class PomoProfile {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "profile_name")
    private String profileName;

    @ColumnInfo(name = "focus_time")
    private int focusTime;

    @ColumnInfo(name = "short_break_time")
    private int shortBreakTime;

    @ColumnInfo(name = "long_break_time")
    private int longBreakTime;

    @ColumnInfo(name = "repeats")
    private int repeats;

    @ColumnInfo(name = "editing_allowed")
    private boolean editingAllowed;

    public static PomoProfile[] defaultProfiles = new PomoProfile[] {
            new PomoProfile("Pomodoro", 25, 5, 15, 4, false),
            new PomoProfile("Super Focus", 60, 10, 20, 6, false),
            new PomoProfile("Add Custom", 25, 5, 15, 4, true),
    };

    public PomoProfile(String profileName, int focusTime, int shortBreakTime, int longBreakTime, int repeats,
                       boolean editingAllowed) {
        this.profileName = profileName;
        this.focusTime = focusTime;
        this.shortBreakTime = shortBreakTime;
        this.longBreakTime = longBreakTime;
        this.repeats = repeats;
        this.editingAllowed = editingAllowed;
    }

    public PomoProfile(PomoProfile pomoProfile) {
        this.profileName = pomoProfile.getProfileName();
        this.focusTime = pomoProfile.getFocusTime();
        this.shortBreakTime = pomoProfile.getShortBreakTime();
        this.longBreakTime = pomoProfile.getLongBreakTime();
        this.repeats = pomoProfile.getRepeats();
    }
    /*
    * Setters and Getters
    */

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public int getFocusTime() {
        return focusTime;
    }

    public void setFocusTime(int focusTime) {
        this.focusTime = focusTime;
    }

    public int getShortBreakTime() {
        return shortBreakTime;
    }

    public void setShortBreakTime(int shortBreakTime) {
        this.shortBreakTime = shortBreakTime;
    }

    public int getLongBreakTime() {
        return longBreakTime;
    }

    public void setLongBreakTime(int longBreakTime) {
        this.longBreakTime = longBreakTime;
    }

    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public boolean isEditingAllowed() {
        return editingAllowed;
    }

    public void setEditingAllowed(boolean editingAllowed) {
        this.editingAllowed = editingAllowed;
    }
    /*
    * End of setters and getters
    */
    @Override
    public String toString() {
        return this.profileName;
    }
}
