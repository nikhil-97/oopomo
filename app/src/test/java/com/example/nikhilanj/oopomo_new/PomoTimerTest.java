package com.example.nikhilanj.oopomo_new;

import com.example.nikhilanj.oopomo_new.lib.PomoTimer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class PomoTimerTest {

    private PomoTimerListener pomoTimerListener;
    private PomoTimer pomoTimer;

    private int testCountDown;
    private String timerUpdateMessage;
    private String timerTickMessage;

    @Before
    public void init() {
        pomoTimerListener = new PomoTimerListener();
        testCountDown = 5;
        pomoTimer = new PomoTimer(testCountDown, pomoTimerListener);
    }

    @Test
    public void startTimerTest() {
        assertFalse("Timer should not be running initially", pomoTimer.isTimerRunning());
        pomoTimer.startTimer();
        assertTrue("Timer should be running after startTimer()", pomoTimer.isTimerRunning());
    }

    @Test
    public void pauseTimerTest() {
        pomoTimer.startTimer();
        pomoTimer.pauseTimer();
        assertFalse("Timer should not be running after pauseTimer()", pomoTimer.isTimerRunning());
    }

    @Test
    public void resumeTimerTest() {
        pomoTimer.startTimer();
        pomoTimer.pauseTimer();
        pomoTimer.resumeTimer();
        assertTrue("Timer should be running after resumeTimer()", pomoTimer.isTimerRunning());
    }

    @Test
    public void notifyTimerListenersTest() {
        pomoTimer.startTimer();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Assert.fail("notifyTimerListeners test interrupted");
        }
        assertEquals("onPomoTimerUpdate should be called after countdown is updated",
                "onPomoTimerUpdate() on listener is called", timerUpdateMessage);
    }

    @Test
    public void onPomoTimerTickTest() {
        pomoTimer.startTimer();
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            Assert.fail("notifyTimerListeners test interrupted");
        }
        assertEquals("onPomoTimerTick should be called after countdown is completed",
                "onPomoTimerTick() on listener is called", timerTickMessage);
    }

    @Test
    public void getTimeStringTest() {
        pomoTimer.startTimer();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Assert.fail("get test interrupted");
        }
        pomoTimer.pauseTimer();
        assertEquals("getTime() should return currentCountdown in mm:ss format",
                "00:03",
                pomoTimer.getTime());
        assertEquals("getTime(60) should return 01:00", "01:00", pomoTimer.getTime(60));
    }

    public class PomoTimerListener implements PomoTimer.TimerEventsListener {

        public void onPomoTimerUpdate() {
            timerUpdateMessage = "onPomoTimerUpdate() on listener is called";
        }

        public synchronized void onPomoTimerTick() {
            timerTickMessage = "onPomoTimerTick() on listener is called";
        }
    }
}
