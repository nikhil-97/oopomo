package com.example.nikhilanj.oopomo_new;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;


interface goalInteractionInterface{
    GoalCardItem getGoalAtListPosition(int position);
    int getGoalsListSize();
    void saveGoalToList(String goalTitle,String goalDesc,int position);
    void deleteGoalFromList(int position);
    void markGoalDone(int position,boolean done);
}


public class GoalsCardAdapter extends RecyclerView.Adapter<GoalsCardAdapter.GoalsViewHolder>{

    private GoalsFragment parentGoalFragment;
    private goalInteractionInterface goalInteractionListener;

    GoalsCardAdapter(GoalsFragment goalsFragment){
        setHasStableIds(true);
        this.parentGoalFragment = goalsFragment;
        this.goalInteractionListener = (goalInteractionInterface) this.parentGoalFragment;
    }

    class GoalsViewHolder extends RecyclerView.ViewHolder{
        private EditText goalTitleEditText;
        private EditText goalDescEditText;
        private TextView goalTitleTextView;
        private TextView goalDescTextView;
        private ViewSwitcher switchEditable;
        private FloatingActionButton goalSaveButton;
        private FloatingActionButton goalEditButton;

        GoalsViewHolder(View itemView) {
            super(itemView);
            goalTitleEditText = itemView.findViewById(R.id.et_goal_title);
            goalDescEditText = itemView.findViewById(R.id.et_goal_description);
            goalTitleTextView = itemView.findViewById(R.id.tv_show_goal_title_when_not_editing);
            goalDescTextView = itemView.findViewById(R.id.tv_show_goal_desc_when_not_editing);
            goalSaveButton = itemView.findViewById(R.id.btn_save_goal);
            goalEditButton = itemView.findViewById(R.id.btn_show_goal_edit_menu);
            switchEditable = itemView.findViewById(R.id.goalViewSwitcher);

            goalSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int temp_pos = getAdapterPosition();
                    String goaltext = goalTitleEditText.getText().toString();
                    String goaldesc = goalDescEditText.getText().toString();
                    goalInteractionListener.saveGoalToList(goaltext,goaldesc,temp_pos);

                    // below lines are kinda for rechecking :? . After we set the texts above,
                    // we again fetch the set data from the list, to make sure we set it correctly.
                    GoalCardItem goalItem = goalInteractionListener.getGoalAtListPosition(temp_pos);
                    goalTitleTextView.setText(goalItem.getGoalTitle());
                    goalDescTextView.setText(goalItem.getGoalDescription());
                    System.out.println("goaltitletextview "+goalTitleTextView.getText());
                    System.out.println("goaltitledescview "+goalDescTextView.getText());

                    switchEditable.showNext(); //switch view to non-editing layout

                }});


            goalEditButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View view) {
                    PopupMenu popup = new PopupMenu(goalEditButton.getContext(), goalEditButton);
                    popup.getMenuInflater().inflate(R.menu.goal_card_menu,popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.btn_edit_goal:
                                    if(switchEditable.getCurrentView()!=view.findViewById(R.id.noteditable_goal)){
                                        switchEditable.showPrevious();
                                    }
                                    return true;

                                case R.id.btn_delete_goal:
                                    showDeleteAlert(parentGoalFragment,getAdapterPosition());
                                    return true;
                            }
                            return true;
                        }
                    });
                    popup.show();
                }

            });

        }
    }

    @Override
    public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.goal_cardview_editable_layout, parent,false);
        GoalsViewHolder gvh = new GoalsViewHolder(view);
        gvh.switchEditable.setDisplayedChild(0);
        return gvh;
    }

    @Override
    public void onBindViewHolder(GoalsViewHolder holder,int position) {
        GoalCardItem cardItem = goalInteractionListener.getGoalAtListPosition(position);
        holder.goalTitleTextView.setText(cardItem.getGoalTitle());
        holder.goalDescTextView.setText(cardItem.getGoalDescription());

    }

    @Override
    public int getItemCount() {return goalInteractionListener.getGoalsListSize();}

    @Override
    public long getItemId(int position){return goalInteractionListener.getGoalAtListPosition(position).hashCode();
    // returning unique hashcode here because we have set setStableIds(true) for the adapter.
    // this is to solve IndexOutOfBoundsException which occurs when removing items.
    // https://stackoverflow.com/a/41659302/6200378 <- suggested here
    }


    private void showDeleteAlert(GoalsFragment parentGoalFragment, int position){
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(parentGoalFragment.getContext());
        builder.setMessage("Really delete this goal ?").setTitle("Delete Goal ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {deleteGoal(pos);}
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("NO");}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteGoal(int pos){
        goalInteractionInterface gil = goalInteractionListener;
        gil.deleteGoalFromList(pos);
    }


}

