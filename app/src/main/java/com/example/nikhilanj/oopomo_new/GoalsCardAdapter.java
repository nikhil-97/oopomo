package com.example.nikhilanj.oopomo_new;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


interface goalInteractionInterface{
    GoalCardItem getGoalAtListPosition(int position);
    int getGoalsListSize();
    void saveGoalToList(String goalTitle,String goalDesc,int position);
    void deleteGoalFromList(int position);
    void markGoalDone(int position,boolean done);
}


public class GoalsCardAdapter extends RecyclerView.Adapter<GoalsCardAdapter.GoalsViewHolder>{

    private GoalsFragment parentGoalFragment;
    private RecyclerView parentGoalsRecyclerView;
    private goalInteractionInterface goalInteractionListener;
    private List<Boolean> fadeOutList = new ArrayList<>();

    GoalsCardAdapter(GoalsFragment goalsFragment){
        setHasStableIds(true);
        this.parentGoalFragment = goalsFragment;
        this.parentGoalsRecyclerView = goalsFragment.goalsRecyclerView;
        this.goalInteractionListener = (goalInteractionInterface) this.parentGoalFragment;
    }

    class GoalsViewHolder extends RecyclerView.ViewHolder{
        private CardView goalCardView;
        private EditText goalTitleEditText;
        private EditText goalDescEditText;
        private TextView goalTitleTextView;
        private TextView goalDescTextView;
        private ViewSwitcher switchEditable;
        private FloatingActionButton goalSaveButton;
        private FloatingActionButton goalUndoButton;
        private FloatingActionButton showGoalEditMenuButton;
        boolean swipeable = true;
        boolean editable = false;

        GoalsViewHolder(final View itemView) {
            super(itemView);

            goalCardView = (CardView) itemView;
            goalTitleEditText = itemView.findViewById(R.id.et_goal_title);
            goalDescEditText = itemView.findViewById(R.id.et_goal_description);
            goalTitleTextView = itemView.findViewById(R.id.tv_show_goal_title_when_not_editing);
            goalDescTextView = itemView.findViewById(R.id.tv_show_goal_desc_when_not_editing);
            goalSaveButton = itemView.findViewById(R.id.btn_save_goal);
            goalUndoButton = itemView.findViewById(R.id.btn_undo_goal);
            showGoalEditMenuButton = itemView.findViewById(R.id.btn_show_goal_edit_menu);
            switchEditable = itemView.findViewById(R.id.goalViewSwitcher);
            final float defaultAlpha = itemView.getAlpha();
            final float defaultCardElevation = goalCardView.getCardElevation();
            fadeOutList.add(getAdapterPosition()+1,false);



            goalSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapter_pos = getAdapterPosition();
                    String goaltext = goalTitleEditText.getText().toString();
                    String goaldesc = goalDescEditText.getText().toString();
                    if(goaltext==null || goaltext.trim().isEmpty()){
                        goalTitleEditText.setError("Can't have empty goal");
                        return;}
                    else goalInteractionListener.saveGoalToList(goaltext,goaldesc,adapter_pos);
                    // below lines are kinda for rechecking :? . After we set the texts above,
                    // we again fetch the set data from the list, to make sure we set it correctly.
                    GoalCardItem goalItem = goalInteractionListener.getGoalAtListPosition(adapter_pos);
                    goalTitleTextView.setText(goalItem.getGoalTitle());
                    goalDescTextView.setText(goalItem.getGoalDescription());
                    System.out.println("goaltitletextview "+goalTitleTextView.getText());
                    System.out.println("goaltitledescview "+goalDescTextView.getText());
                    //int childCount =
                    Collections.fill(fadeOutList,Boolean.FALSE);//set all values to false

                    //parentGoalFragment.goalsRecyclerView.setAlpha(defaultAlpha);
                    notifyDataSetChanged();
                    goalCardView.setCardElevation(defaultCardElevation);
                    swipeable = true;
                    editable = false;
                    //switchEditable.showNext(); //switch view to non-editing layout
                    View currentFocus = parentGoalFragment.getActivity().getCurrentFocus();
                    currentFocus.clearFocus();
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) parentGoalFragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);

                }});

            goalUndoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


            showGoalEditMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popup = new PopupMenu(showGoalEditMenuButton.getContext(), showGoalEditMenuButton);
                    popup.getMenuInflater().inflate(R.menu.goal_card_menu,popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.btn_edit_goal:
                                    int pos = getAdapterPosition();
                                    GoalCardItem goalItem = goalInteractionListener.getGoalAtListPosition(pos);
                                    goalTitleEditText.setText(goalItem.getGoalTitle());
                                    goalDescEditText.setText(goalItem.getGoalDescription());
                                    switchEditable.showPrevious();
                                    goalTitleEditText.requestFocus();
                                    goalCardView.setCardElevation((float)10);
                                    GoalsViewHolder.this.swipeable = false;
                                    Collections.fill(fadeOutList,Boolean.TRUE);
                                    fadeOutList.set(pos,Boolean.FALSE);
                                    editable = true;
                                    notifyDataSetChanged();
                                    System.out.println("cardview elevation = "+goalCardView.getCardElevation());
                                    //ugly hack to blur the background when editing
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
        //gvh.switchEditable.setDisplayedChild(0);
        return gvh;
    }

    @Override
    public void onBindViewHolder(GoalsViewHolder holder,int position) {
        if(fadeOutList.get(position)){
            //set faded alpha i.e. 0.25f if fadeOut is true i.e. if you want it to fade
            holder.goalCardView.setAlpha(0.25f);
            holder.showGoalEditMenuButton.setEnabled(false);
        }
        else {
            holder.goalCardView.setAlpha(1.0f);
            holder.showGoalEditMenuButton.setEnabled(true);
        }

        GoalCardItem cardItem = goalInteractionListener.getGoalAtListPosition(position);
        String goalTitle = cardItem.getGoalTitle();
        String goalDesc = cardItem.getGoalDescription();

        if((goalTitle!=null && goalDesc!=null) && !holder.editable) {
            holder.goalTitleTextView.setText(goalTitle);
            holder.goalDescTextView.setText(goalDesc);
            holder.switchEditable.setDisplayedChild(1);
        }
        else holder.switchEditable.setDisplayedChild(0);

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

