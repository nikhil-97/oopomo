package com.example.nikhilanj.oopomo_new.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.nikhilanj.oopomo_new.db.entity.PomoProfile;
import com.example.nikhilanj.oopomo_new.db.dao.PomoProfileDao;

import java.util.concurrent.Executors;

@Database(entities = {PomoProfile.class}, version = 1)
public abstract class PomoDatabase extends RoomDatabase {

    private static PomoDatabase pomoDatabaseInstance = null;

    public abstract PomoProfileDao pomoProfileDao();

    public synchronized static PomoDatabase getPomoDatabaseInstance(final Context context) {
        if(pomoDatabaseInstance == null) {
            Callback cb = new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            getPomoDatabaseInstance(context).pomoProfileDao().insertAll(PomoProfile.defaultProfiles);
                        }
                    });
                }
            };
            pomoDatabaseInstance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    PomoDatabase.class,
                    "pomo-db")
                    .allowMainThreadQueries()
                    .addCallback(cb)
                    .build();
        }

        return pomoDatabaseInstance;
    }
}
