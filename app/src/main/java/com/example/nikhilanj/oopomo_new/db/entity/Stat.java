package com.example.nikhilanj.oopomo_new.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Stat {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stat_id")
    private int statId;

    @ColumnInfo(name = "focus_time")
    private int focusTime;

    @ColumnInfo(name = "break_time")
    private int breakTime;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public Stat(int focusTime, int breakTime, long timestamp) {
        this.focusTime = focusTime;
        this.breakTime = breakTime;
        this.timestamp = timestamp;
    }

    public int getStatId() {
        return statId;
    }

    public void setStatId(int statId) {
        this.statId = statId;
    }

    public int getFocusTime() {
        return focusTime;
    }

    public void setFocusTime(int focusTime) {
        this.focusTime = focusTime;
    }

    public int getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(int breakTime) {
        this.breakTime = breakTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
