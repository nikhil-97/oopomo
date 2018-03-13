package com.example.nikhilanj.oopomo_new.utils;

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.entity.PomoProfile;

import java.util.List;

public class PomoProfileManager {

    private final PomoDatabase pomoDatabase;

    public PomoProfileManager(final PomoDatabase pomoDatabase) {
        this.pomoDatabase = pomoDatabase;
    }

    public List<PomoProfile> getAllPomoProfiles() {
        return this.pomoDatabase.pomoProfileDao().getAllPomoProfiles();
    }

    public PomoProfile getProfileByName(String profileName) {
        return this.pomoDatabase.pomoProfileDao().getPomoProfileByName(profileName);
    }

    public void insertPomoProfile(String profileName,
                                  int focusTime,
                                  int shortBrekTime,
                                  int longBreakTime,
                                  int repeats,
                                  boolean editingAllowed) {

        PomoProfile pomoProfile = new PomoProfile(profileName, focusTime, shortBrekTime, longBreakTime, repeats, editingAllowed);
        this.pomoDatabase.pomoProfileDao().insert(pomoProfile);
    }

    public void insertPomoProfile(PomoProfile pomoProfile) {
        this.pomoDatabase.pomoProfileDao().insert(pomoProfile);
    }

    public void updatePomoProfile(PomoProfile pomoProfile) {
        this.pomoDatabase.pomoProfileDao().updatePomoProfile(pomoProfile);
    }
}
