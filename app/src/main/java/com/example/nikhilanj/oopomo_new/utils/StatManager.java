package com.example.nikhilanj.oopomo_new.utils;

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.entity.StatObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatManager {
    private final PomoDatabase pomoDatabase;

    public StatManager(PomoDatabase pomoDatabase) {
        this.pomoDatabase = pomoDatabase;
    }

    public void insertStat(int focusTime, int breakTime, long timestamp) {
        StatObject stat = new StatObject(timestamp,focusTime, breakTime);
        pomoDatabase.statDao().insert(stat);
    }

    public List<StatObject> getDailyStats() {
        long toTimestamp = System.currentTimeMillis();
        long fromTimestamp = System.currentTimeMillis() - 10 * 24 * 60 * 60;
        List<StatObject> statObjects = pomoDatabase.statDao().getStatObjectsBetween(fromTimestamp, toTimestamp);
        ArrayList<StatObject> statObjectsResult = new ArrayList<>();
        int date = 0;
        int breakTime = 0, focusTime = 0;
        long timestamp = 0;
        for (StatObject statObject: statObjects) {
            int currentDate = DateFormat.getDate(statObject.getTimeStamp());
            if(date == 0) {
                date = currentDate;
                breakTime += statObject.getBreakTime();
                focusTime += statObject.getFocusTime();
                timestamp = statObject.getTimeStamp();
                continue;
            }
            if(currentDate == date) {
                breakTime += statObject.getBreakTime();
                focusTime += statObject.getFocusTime();
                timestamp = statObject.getTimeStamp();
                continue;
            }
            if(currentDate != date) {
                statObjectsResult.add(new StatObject(timestamp, focusTime, breakTime));
            }
        }
        return statObjectsResult;
    }
}

class DateFormat {
    public static int getDate(long timeInMillis) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d");
        int dateInt =  Integer.parseInt(simpleDateFormat.format(date));
        return dateInt;
    }
}