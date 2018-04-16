package com.example.nikhilanj.oopomo_new.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.nikhilanj.oopomo_new.db.entity.Stat;

import java.util.List;

@Dao
public interface StatDao {

    @Query("SELECT * FROM STAT WHERE stat_id = :id")
    List<Stat> getStatById(long id);

    @Query("SELECT * FROM STAT WHERE timestamp BETWEEN :from AND :to ORDER BY timestamp")
    List<Stat> getStatObjectsBetween(long from, long to);

    @Insert
    long insert(Stat stat);
}
