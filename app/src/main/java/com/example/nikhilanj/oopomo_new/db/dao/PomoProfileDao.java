package com.example.nikhilanj.oopomo_new.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.nikhilanj.oopomo_new.db.entity.PomoProfile;

import java.util.List;

@Dao
public interface PomoProfileDao {

    @Query("SELECT * FROM PomoProfile")
    List<PomoProfile> getAllPomoProfiles();

    @Query("SELECT * FROM PomoProfile WHERE profile_name = :profileName")
    PomoProfile getPomoProfileByName(String profileName);

    @Insert
    void insert(PomoProfile pomoProfile);

    @Insert
    void insertAll(PomoProfile ...pomoProfiles);

    @Update
    void updatePomoProfile(PomoProfile pomoProfile);

    @Delete
    void deletePomoProfile(PomoProfile pomoProfile);
}
