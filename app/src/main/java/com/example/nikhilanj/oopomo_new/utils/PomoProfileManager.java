package com.example.nikhilanj.oopomo_new.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.nikhilanj.oopomo_new.R;
import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.entity.PomoProfile;

import java.security.PublicKey;
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

    public void saveProfilePreference(Context context, String profileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.app_shared_preferences_key),
                Context.MODE_PRIVATE );
        SharedPreferences.Editor sharedPreferencesEditor =  sharedPreferences.edit();
        sharedPreferencesEditor.putString(
                context.getString(R.string.current_profile_key), profileName
        );
    }

    public PomoProfile getCurrentProfile(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.app_shared_preferences_key),
                Context.MODE_PRIVATE );
        String profileName = sharedPreferences.getString(
                context.getString(R.string.current_profile_key), "Pomodoro"
        );
        return this.getProfileByName(profileName);
    }
}
