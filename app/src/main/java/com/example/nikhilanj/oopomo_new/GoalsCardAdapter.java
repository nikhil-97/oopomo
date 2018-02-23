package com.example.nikhilanj.oopomo_new;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;

interface goalInteractionInterface{
    void saveGoalToList(String goalTitle,String goalDesc,int position);
    void deleteGoalFromList(int position);
    void markGoalDone(int position,boolean done);
}


public class GoalsCardAdapter extends RecyclerView.Adapter<GoalsCardAdapter.GoalsViewHolder>{

    private LinkedList<GoalCardItem> goalsLinkedList;
    private GoalsFragment parentGoalFragment;
    private goalInteractionInterface goalInteractionListener;
    private RecyclerView parentRecyclerView;

    public GoalsCardAdapter(LinkedList<GoalCardItem> goalsList,GoalsFragment goalsFragment){
        this.goalsLinkedList = goalsList;
        this.parentGoalFragment = goalsFragment;
        this.goalInteractionListener = (goalInteractionInterface) this.parentGoalFragment;
    }

    public static class GoalsViewHolder extends RecyclerView.ViewHolder{
        public EditText goalTitleEditText;
        public EditText goalDescEditText;
        public TextView goalTitleTextView;
        public TextView goalDescTextView;
        public FloatingActionButton goalDeleteButton;
        public FloatingActionButton goalSaveButton;
        public FloatingActionButton goalEditButton;

        public GoalsViewHolder(View itemView) {
            super(itemView);
            goalTitleEditText = itemView.findViewById(R.id.et_goal_title);
            goalDescEditText = itemView.findViewById(R.id.et_goal_description);
            goalTitleTextView = itemView.findViewById(R.id.tv_show_goal_title_when_not_editing);
            goalDescTextView = itemView.findViewById(R.id.tv_show_goal_desc_when_not_editing);
            goalDeleteButton = itemView.findViewById(R.id.btn_delete_goal);
            goalSaveButton = itemView.findViewById(R.id.btn_save_goal);
            goalEditButton = itemView.findViewById(R.id.btn_edit_goal);

        }
    }

    @Override
    public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_cardview_editable_layout,parent,false);
        parentRecyclerView = (RecyclerView) parent;
        GoalsViewHolder gvh = new GoalsViewHolder(view);
        return gvh;
    }

    @Override
    public void onBindViewHolder(final GoalsViewHolder holder, int position) {
        final LinkedList<GoalCardItem> goalListRef = this.goalsLinkedList;

        holder.goalDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlert(parentGoalFragment,goalInteractionListener,holder.getAdapterPosition());
            }
        });

        holder.goalSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp_pos = holder.getAdapterPosition();
                String goaltext = holder.goalTitleEditText.getText().toString();
                String goaldesc = holder.goalDescEditText.getText().toString();
                goalInteractionListener.saveGoalToList(goaltext,goaldesc,temp_pos);
                GoalCardItem goalItem = goalListRef.get(temp_pos);
                holder.goalTitleTextView.setText(goalItem.getGoalTitle());
                holder.goalDescTextView.setText(goalItem.getGoalDescription());
                System.out.println(goalItem.getGoalTitle()+goalItem.getGoalDescription());
                changeCardEditable(holder,false);
                //GoalsCardAdapter.super.notifyItemChanged(temp_pos);
                /*TODO : CRAZY BUG.
                 SOMEHOW THE PREVIOUSLY DELETED GOALS INTERFERE WITH NEW ADDED GOALS.
                 RANDOM STUFF*/

            }});

        holder.goalEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCardEditable(holder,true);
            }

        });

    }

    @Override
    public int getItemCount() {
        return goalsLinkedList.size();
    }

    private void changeCardEditable(GoalsViewHolder holder,boolean editable){
        holder.goalEditButton.setVisibility(editable ? View.GONE : View.VISIBLE);
        holder.goalTitleTextView.setVisibility(editable ? View.GONE : View.VISIBLE);
        holder.goalDescTextView.setVisibility(editable ? View.GONE : View.VISIBLE);

        holder.goalDeleteButton.setVisibility(editable ? View.VISIBLE : View.GONE);
        holder.goalSaveButton.setVisibility(editable ? View.VISIBLE : View.GONE);
        holder.goalTitleEditText.setVisibility(editable ? View.VISIBLE : View.GONE);
        holder.goalDescEditText.setVisibility(editable ? View.VISIBLE : View.GONE);

        if(editable) holder.goalTitleEditText.requestFocus();
        else holder.goalTitleEditText.clearFocus();

    }

    static void showDeleteAlert(GoalsFragment parentGoalFragment,goalInteractionInterface goalInteractionListener, int position){
        final goalInteractionInterface gil = goalInteractionListener;
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(parentGoalFragment.getContext());
        builder.setMessage("Really delete this goal ?").setTitle("Delete Goal ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gil.deleteGoalFromList(pos);}
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("NO");}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

