package com.example.nikhilanj.oopomo_new.goals_package;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.nikhilanj.oopomo_new.R;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;


public class GoalsCardAdapter extends RecyclerView.Adapter<GoalsCardAdapter.GoalsViewHolder> {

    private GoalsFragment parentGoalFragment;
    private IgoalFragmentAdapterInteraction interactWithGoalFragment;

    // What is fadeOutMap ?
    // When a particular goal("item") is selected, we want all other items to fade out, so that only
    // the current goal is shown. The fadeOut needs to be done in the onBindViewHolder.
    // We need to let the onBindViewHolder know which items to fadeOut and which to set on focus(i.e. not fadeOut).
    // So we use a HashMap, which holds the key as the .hashCode() of the items in the goalsList.
    // Essentially it is like :   GoalCardItem.hashCode() --> fadeOut?
    // fadeOut false implies don't fade them out. true implies they get faded out.

    // If you have any better ideas, raise an issue on GitHub.

    @SuppressLint("UseSparseArrays")
    private HashMap<Long, Boolean> fadeOutMap = new HashMap<>();
    //private LongSparseArray<Boolean> fadeOutMap = new LongSparseArray<>();

    // IDE would prompt you to use LongSparseArray(LSA) instead of HashMap here, as LSA is more memory efficient.
    // Although find the key is O(logN) in LSA, we would have quite a few items here (assume not more than 50)
    // But however, we find the keys every single time onBindViewHolder is called, i.e. every time view is generated.
    // There would be far more key accesses. Hence time overhead is more important than memory overhead here.
    // Also, as it's only a few items, the memory advantage is not considerable.

    final private float FADE_OUT_ALPHA = 0.25f;
    final private float DEFAULT_ALPHA = 1.0f;

    final private boolean GOAL_VIEW_MODE_NOW_EDITING = false;
    final private boolean GOAL_VIEW_MODE_NOW_VIEWING = true;
    final private boolean PREVIOUS_ACTION_IS_ADDING = true;
    final private boolean PREVIOUS_ACTION_IS_EDITING = false;
    private boolean addingNewGoal = false;
    private boolean previousAction;

    private Stack<GoalsViewHolder> viewHolderStack = new Stack<>();

    GoalsCardAdapter(GoalsFragment goalsFragment) {
        setHasStableIds(true);
        this.parentGoalFragment = goalsFragment;
        this.interactWithGoalFragment = (IgoalFragmentAdapterInteraction) this.parentGoalFragment;
        for (int i = 0; i < interactWithGoalFragment.getGoalsActiveListSize(); i++) {
            fadeOutMap.put(getItemId(i), false);
        }
    }

    class GoalsViewHolder extends RecyclerView.ViewHolder {
        private CardView goalCardView;
        private EditText goalTitleEditText;
        private EditText goalDescEditText;
        private TextView goalTitleTextView;
        private TextView goalDescTextView;
        private ViewSwitcher switchEditable;
        private FloatingActionButton goalSaveButton;
        private FloatingActionButton goalUndoButton;
        private FloatingActionButton showGoalEditMenuButton;
        boolean isCurrentlySwipeable = true;
        boolean isCurrentlyEditable = false;
        float defaultCardElevation = 1.0f;


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
            defaultCardElevation = goalCardView.getCardElevation();

            goalSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String goaltext = goalTitleEditText.getText().toString();
                    String goaldesc = goalDescEditText.getText().toString();
                    if (goaltext == null || goaltext.trim().isEmpty()) {
                        goalTitleEditText.setError("Can't have empty goal");
                        return;
                    } else
                        interactWithGoalFragment.saveGoalToList(goaltext, goaldesc, getAdapterPosition());

                    changeGoalViewMode(GoalsViewHolder.this,getAdapterPosition(), GOAL_VIEW_MODE_NOW_VIEWING);
                    for (Long id : fadeOutMap.keySet()) {fadeOutMap.put(id, false);}
                    notifyItemRangeChanged(0, interactWithGoalFragment.getGoalsActiveListSize());
                    //notifyItemChanged(getAdapterPosition());
                    //calling notifyDataSetChanged because we have to change view of all other viewholders

                }
            });

            goalUndoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoalsViewHolder holder = GoalsViewHolder.this;
                    if(previousAction==GOAL_VIEW_MODE_NOW_EDITING){
                        try{holder = viewHolderStack.pop();
                            changeGoalViewMode(holder,getAdapterPosition(), GOAL_VIEW_MODE_NOW_VIEWING);}
                        catch(EmptyStackException e){
                            Log.e("empty stack when undo",Log.getStackTraceString(e));}
                    }
                    else if(previousAction==PREVIOUS_ACTION_IS_ADDING){
                        deleteGoal(getAdapterPosition());
                    }
                    //changeGoalViewMode(holder,getItemCount(), GOAL_VIEW_MODE_NOW_VIEWING);
                    for (Long id : fadeOutMap.keySet()) {fadeOutMap.put(id, false);}
                    notifyDataSetChanged();
                    //notifyItemRangeChanged(0, interactWithGoalFragment.getGoalsListSize());
                    interactWithGoalFragment.enableAddGoalFab(true);
                }
            });


            showGoalEditMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popup = new PopupMenu(showGoalEditMenuButton.getContext(), showGoalEditMenuButton);
                    popup.getMenuInflater().inflate(R.menu.goal_card_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.btn_edit_goal:
                                    previousAction = PREVIOUS_ACTION_IS_EDITING;
                                    changeGoalViewMode(GoalsViewHolder.this,getAdapterPosition(), GOAL_VIEW_MODE_NOW_EDITING);
                                    long currentItemId = GoalsCardAdapter.this.getItemId(getAdapterPosition());
                                    for (Long id : fadeOutMap.keySet()) {fadeOutMap.put(id, id != currentItemId);}
                                    notifyDataSetChanged();
                                    //calling notifyDataSetChanged because we have to change view of all other viewholders
                                    return true;
                                case R.id.btn_delete_goal:
                                    showDeleteAlert(parentGoalFragment, getAdapterPosition());
                                    return true;
                            }
                            return true;
                        }
                    });
                    popup.show();
                }

            });
        }

        String getHolderTitleText(){return this.goalTitleTextView.getText().toString();}

    }

    //private void setAllAndNotifyRangeChanged(HashMap<Long, Boolean> fadeOutMap,boolean set){}

    private void changeGoalViewMode(GoalsViewHolder holder,int adapterPos, boolean viewMode) {
        GoalCardItem goalItem = interactWithGoalFragment.getGoalAtListPosition(adapterPos);
        if (viewMode == GOAL_VIEW_MODE_NOW_VIEWING) {
            holder.goalTitleTextView.setText(goalItem.getGoalTitle());
            holder.goalDescTextView.setText(goalItem.getGoalDescription());
            holder.goalCardView.setCardElevation(holder.defaultCardElevation);
            holder.isCurrentlySwipeable = true;
            holder.isCurrentlyEditable = false;
            holder.switchEditable.setDisplayedChild(1);
            interactWithGoalFragment.enableAddGoalFab(true);
            interactWithGoalFragment.clearFocusAndHideSoftInputKeyboard();

        } else if (viewMode == GOAL_VIEW_MODE_NOW_EDITING) {
            viewHolderStack.push(holder);
            parentGoalFragment.goalsRecyclerView.scrollToPosition(adapterPos);
            holder.goalTitleEditText.setText(goalItem.getGoalTitle());
            holder.goalDescEditText.setText(goalItem.getGoalDescription());
            holder.goalCardView.setCardElevation(10.0f);
            holder.goalTitleEditText.requestFocus();
            holder.isCurrentlySwipeable = false;
            holder.isCurrentlyEditable = true;
            interactWithGoalFragment.enableAddGoalFab(false);
            holder.switchEditable.setDisplayedChild(0);

        }
    }

    void addNewGoal(long newGoalId) {
        for (Long id : fadeOutMap.keySet()) {fadeOutMap.put(id, true);}
        fadeOutMap.put(newGoalId, false);
        addingNewGoal = true;
        notifyItemInserted(0);
        notifyDataSetChanged();
        System.out.println(parentGoalFragment.getGoalsActiveListSize());
        previousAction = PREVIOUS_ACTION_IS_ADDING;

        parentGoalFragment.enableAddGoalFab(false);
    }

    @Override
    public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.goal_cardview_editable_layout, parent, false);

        return new GoalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoalsViewHolder holder, int position) {
        long itemId = getItemId(position);
        if (fadeOutMap.get(itemId)) {
            //set faded alpha i.e. 0.25f if fadeOut is true i.e. if you want it to fade
            holder.goalCardView.setAlpha(FADE_OUT_ALPHA);
            holder.showGoalEditMenuButton.setEnabled(false);
        } else {
            holder.goalCardView.setAlpha(DEFAULT_ALPHA);
            holder.showGoalEditMenuButton.setEnabled(true);
        }

        GoalCardItem cardItem = interactWithGoalFragment.getGoalAtListPosition(position);
        String goalTitle = cardItem.getGoalTitle();
        String goalDesc = cardItem.getGoalDescription();

        if (addingNewGoal) {
            changeGoalViewMode(holder,position, GOAL_VIEW_MODE_NOW_EDITING);
            addingNewGoal = false;
        }

        if ((goalTitle != null && goalDesc != null) && !holder.isCurrentlyEditable) {
            holder.goalTitleTextView.setText(goalTitle);
            holder.goalDescTextView.setText(goalDesc);
            holder.switchEditable.setDisplayedChild(1);
        } else holder.switchEditable.setDisplayedChild(0);

    }

    @Override
    public int getItemCount() {return interactWithGoalFragment.getGoalsActiveListSize();}

    @Override
    public long getItemId(int position){return interactWithGoalFragment.getGoalAtListPosition(position).hashCode();
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
        long itemId = getItemId(pos);
        fadeOutMap.remove(itemId);
        interactWithGoalFragment.deleteGoalFromActiveList(pos);
        //notifyItemRemoved(pos);

    }


}

