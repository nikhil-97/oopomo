package com.example.nikhilanj.oopomo_new.stats_package;

class StatObject {
    private long timeStamp;
    private int focusTime;
    private int breakTime;

    StatObject(long timestamp,int focustime,int breaktime){
        setTimeStamp(timestamp);
        setFocusTime(focustime);
        setBreakTime(breaktime);
    }

    public void setTimeStamp(long timestamp){ this.timeStamp = timestamp;}

    public void setFocusTime(int focusTime) {this.focusTime = focusTime;}

    public void setBreakTime(int breakTime) {this.breakTime = breakTime;}

    public long getTimeStamp() {return timeStamp;}

    public int getBreakTime() {return breakTime;}

    public int getFocusTime() {return focusTime;}
}
