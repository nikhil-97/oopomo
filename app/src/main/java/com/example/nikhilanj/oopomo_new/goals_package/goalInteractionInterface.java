package com.example.nikhilanj.oopomo_new.goals_package;

/**
 * Created by Nikhilanj on 15-03-18.
 */
interface goalInteractionInterface{
    GoalCardItem getGoalAtListPosition(int position);
    int getGoalsListSize();
    void saveGoalToList(String goalTitle, String goalDesc, int position);
    void deleteGoalFromList(int position);
    void markGoalDone(int position, boolean done);
}
