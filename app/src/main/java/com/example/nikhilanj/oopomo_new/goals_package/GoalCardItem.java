package com.example.nikhilanj.oopomo_new.goals_package;

class GoalCardItem{
    private int id;
    private String title;
    private String description;
    private boolean markedAsDone;
    private boolean markedImportant;

    GoalCardItem(int id){setGoalId(id);} //empty constructor with just id

    GoalCardItem(int id,String title,String desc,boolean markedImportant){
        setGoalId(id);
        setGoalTitle(title);
        setGoalDesciption(desc);
        setMarkedImportant(markedImportant);
    }

    void setGoalId(int setId){this.id = setId;}

    void setGoalTitle(String title){ this.title = title;}

    void setGoalDesciption(String desc){ this.description = desc;}

    void markGoalAsDone(boolean done){ this.markedAsDone = done;}

    void setMarkedImportant(boolean important){this.markedImportant = important;}

    int getGoalId(){return this.id;}

    String getGoalTitle(){ return this.title;}

    String getGoalDescription(){ return this.description;}

    boolean isGoalMarkedDone(){ return this.markedAsDone;}

    boolean isMarkedImportant(){return this.markedImportant;}

    public String toString(){
        return ("GoalCardItem-- id = "+getGoalId()+", Title = "+getGoalTitle()+", Description = "+getGoalDescription()+",markedImportant = "+isMarkedImportant());
    }

}
