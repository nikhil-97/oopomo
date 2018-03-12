package com.example.nikhilanj.oopomo_new;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.constraint.solver.Goal;

/**
 * Created by Nikhilanj on 23-02-18.
 */

public class GoalCardItem implements Parcelable{
    private String title;
    private String description;
    private boolean markedAsDone = false;

    public GoalCardItem(){} //empty constructor

    private GoalCardItem(Parcel in){
        setGoalTitle(in.readString());
        setGoalDesciption(in.readString());
        markGoalAsDone(in.readInt()==1); //marks as true(i.e. done) if readint is 1, else false
    }

    public void markGoalAsDone(boolean done){this.markedAsDone = done;}

    public void setGoalTitle(String title){this.title = title;}

    public void setGoalDesciption(String desc){this.description = desc;}

    public String getGoalTitle(){return this.title;}

    public String getGoalDescription(){return this.description;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.description);
        parcel.writeInt((this.markedAsDone) ? 1 : 0);
        //writeInt as we can't write Boolean, and can't cast boolean to int directly.

    }

    public static final Parcelable.Creator<GoalCardItem> CREATOR = new Parcelable.Creator<GoalCardItem>() {
        public GoalCardItem createFromParcel(Parcel in) {
            return new GoalCardItem(in);
        }

        public GoalCardItem[] newArray(int size) {
            return new GoalCardItem[size];
        }
    };
}
