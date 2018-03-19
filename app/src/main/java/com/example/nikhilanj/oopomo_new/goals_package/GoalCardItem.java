package com.example.nikhilanj.oopomo_new.goals_package;

class GoalCardItem{
    private String title;
    private String description;
    private boolean markedAsDone;
    private boolean markedImportant;

    GoalCardItem(){} //empty constructor

    GoalCardItem(String title,String desc,boolean markedImportant){
        this.title = title;
        this.description = desc;
        this.markedImportant = markedImportant;
    }

    void markGoalAsDone(boolean done){ this.markedAsDone = done;}

    void setGoalTitle(String title){ this.title = title;}

    void setMarkedImportant(boolean important){this.markedImportant = important;}

    void setGoalDesciption(String desc){ this.description = desc;}

    String getGoalTitle(){ return this.title;}

    String getGoalDescription(){ return this.description;}

    boolean isGoalMarkedDone(){ return this.markedAsDone;}

    boolean isMarkedImportant(){return this.markedImportant;}
}
