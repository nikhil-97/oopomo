package com.example.nikhilanj.oopomo_new.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.nikhilanj.oopomo_new.db.entity.StatObject;

import java.util.List;

@Dao
public interface StatDao {

    @Query("SELECT * FROM STATOBJECT WHERE stat_id = :id")
    List<StatObject> getStatById(long id);

    @Query("SELECT * FROM STATOBJECT WHERE timestamp BETWEEN :from AND :to ORDER BY timestamp")
    List<StatObject> getStatObjectsBetween(long from, long to);

    @Insert
    long insert(StatObject stat);
}
