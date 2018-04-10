package com.example.nikhilanj.oopomo_new.utils;

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.entity.Stat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatManager {
    private final PomoDatabase pomoDatabase;

    public StatManager(PomoDatabase pomoDatabase) {
        this.pomoDatabase = pomoDatabase;
    }
    public void insertStat(int focusTime, int breakTime, long timestamp) {
        Stat stat = new Stat(focusTime, breakTime, timestamp);
        pomoDatabase.statDao().insert(stat);
    }
}
