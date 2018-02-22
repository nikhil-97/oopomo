package com.example.nikhilanj.oopomo_new;

public interface timerFragmentInterface{
    Timer startCountdown(int focustime, int shortbreaktime, int longbreaktime, int repeats);
    void pauseCountdown(Timer timerinstance);
    void resumeCountdown(Timer timerinstance);
    void stopFullCountdown(Timer timerinstance);
}
