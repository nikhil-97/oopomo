package com.example.nikhilanj.oopomo_new.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class StatObject {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stat_id")
    private int statId;

    @ColumnInfo(name = "focus_time")
    private int focusTime;

    @ColumnInfo(name = "break_time")
    private int breakTime;

    @ColumnInfo(name = "timestamp")
    private long timeStamp;

    public StatObject(long timeStamp,int focusTime,int breakTime){
        setTimeStamp(timeStamp);
        setFocusTime(focusTime);
        setBreakTime(breakTime);
    }

    public void setTimeStamp(long timestamp){ this.timeStamp = timestamp;}

    public void setFocusTime(int focusTime) {this.focusTime = focusTime;}

    public void setBreakTime(int breakTime) {this.breakTime = breakTime;}

    public void setStatId(@NonNull int statId) {
        this.statId = statId;
    }

    public long getTimeStamp() {return timeStamp;}

    public int getBreakTime() {return breakTime;}

    public int getFocusTime() {return focusTime;}

    public int getStatId() {
        return statId;
    }
}


