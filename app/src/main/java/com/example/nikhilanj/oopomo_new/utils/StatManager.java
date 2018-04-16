package com.example.nikhilanj.oopomo_new.utils;

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.entity.StatObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatManager {
    private final PomoDatabase pomoDatabase;

    public StatManager(PomoDatabase pomoDatabase) {
        this.pomoDatabase = pomoDatabase;
    }
    public void insertStat(int focusTime, int breakTime, long timestamp) {
        StatObject stat = new StatObject(timestamp,focusTime, breakTime);
        pomoDatabase.statDao().insert(stat);
    }
}
