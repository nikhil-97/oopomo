package com.example.nikhilanj.oopomo_new;

/**
 * Created by Nikhilanj on 23-02-18.
 */

public class GoalCardItem {
    private String title;
    private String description;
    private boolean markedAsDone = false;

    public GoalCardItem(String title,String description){
        this.title = title;
        this.description = description;
    }

    public GoalCardItem(){} //Overloaded empty constructor

    public void markGoalAsDone(boolean done){this.markedAsDone = done;}

    public void editGoalTitle(String title){this.title = title;}

    public void editGoalDesciption(String desc){this.description = desc;}

    public String getGoalTitle(){return this.title;}

    public String getGoalDescription(){return this.description;}

}
