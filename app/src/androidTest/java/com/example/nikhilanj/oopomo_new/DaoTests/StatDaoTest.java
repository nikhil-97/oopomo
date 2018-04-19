package com.example.nikhilanj.oopomo_new.DaoTests;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Root;
import android.support.test.runner.AndroidJUnit4;

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.dao.StatDao;
import com.example.nikhilanj.oopomo_new.db.entity.Stat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class StatDaoTest {
    private StatDao statDao;
    private PomoDatabase pomoDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        pomoDatabase = Room.inMemoryDatabaseBuilder(context, PomoDatabase.class).build();
        statDao = pomoDatabase.statDao();
    }

    @After
    public void closeDb() {
        pomoDatabase.close();
    }

    @Test
    public void writeAndReadStat() {
        Stat stat = new Stat(10, 5,  1523297504);
        long statId = statDao.insert(stat);
        List<Stat> statById = statDao.getStatById(statId);
        assertEquals("same timestamp must be retrieved", 1523297504, statById.get(0).getTimestamp());
    }
}
