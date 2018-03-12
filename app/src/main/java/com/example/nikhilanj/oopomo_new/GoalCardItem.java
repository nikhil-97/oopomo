package com.example.nikhilanj.oopomo_new;

/**
 * Created by Nikhilanj on 23-02-18.
 */

public class GoalCardItem {
    private String title;
    private String description;
    private boolean markedAsDone = false;

    public GoalCardItem(){} //empty constructor

    public void markGoalAsDone(boolean done){this.markedAsDone = done;}

    public void setGoalTitle(String title){this.title = title;}

    public void setGoalDesciption(String desc){this.description = desc;}

    public String getGoalTitle(){return this.title;}

    public String getGoalDescription(){return this.description;}

}
