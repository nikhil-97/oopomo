package com.example.nikhilanj.oopomo_new.goals_package;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.nikhilanj.oopomo_new.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Handler;


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
    HashMap<Long, Boolean> fadeOutMap = new HashMap<>();
    //LongSparseArray<Boolean> fadeOutMap = new LongSparseArray<>();

    // IDE would prompt you to use LongSparseArray(LSA) instead of HashMap here, as LSA is more memory efficient.
    // Fetching the keys in LSA takes more time { O(logN) I think ?
    // But however, we find the keys every single time onBindViewHolder is called, i.e. every time view is generated.
    // Also, the memory advantage is not considerable (from empirical testing) .

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
        /*for (int i = 0; i < interactWithGoalFragment.getGoalsActiveListSize(); i++) {
            fadeOutMap.put(getItemId(i), false);
        }*/
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
        private ImageButton markImportantButton;
        boolean isCurrentlySwipeable = true;
        boolean isCurrentlyEditable = false;
        boolean isImportant;
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
            markImportantButton = itemView.findViewById(R.id.btn_mark_important);

            switchEditable = itemView.findViewById(R.id.goalViewSwitcher);
            defaultCardElevation = goalCardView.getCardElevation();

            goalCardView.setOnClickListener(new View.OnClickListener() {
                //View changes to Edit mode on clicking the view.
                //Initial research showed that mosttt people tended to click on the goal directly,
                // instead of looking for the edit button. THe edit button still stays however.
                @Override
                public void onClick(View view) {
                    Runnable cardClickAction = new Runnable() {
                        @Override
                        public void run() {
                            previousAction = PREVIOUS_ACTION_IS_EDITING;
                            changeGoalViewMode(GoalsViewHolder.this,getAdapterPosition(), GOAL_VIEW_MODE_NOW_EDITING);
                            long currentItemId = GoalsCardAdapter.this.getItemId(getAdapterPosition());
                            for (Long id : fadeOutMap.keySet()) {fadeOutMap.put(id, id != currentItemId);}
                            notifyDataSetChanged();
                            parentGoalFragment.goalsRecyclerView.scrollToPosition(getAdapterPosition());
                            //calling notifyDataSetChanged because we have to change view of all other viewholders
                        }
                    };
                    new android.os.Handler().postDelayed(cardClickAction,250);
                    //added this delay so that the view is changed after some delay.
                    // immediate changing of the view is kinda "shocking" to the user...and jarring

                }
            });

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
                    //calling notifyDataSetChanged because we have to change view of all other viewholders

                }
            });

            goalUndoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoalsViewHolder holder;
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
                    //interactWithGoalFragment.clearFocusAndHideSoftInputKeyboard();
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
                                    parentGoalFragment.goalsRecyclerView.scrollToPosition(getAdapterPosition());
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

            markImportantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isImportant = !isImportant;
                    GoalCardItem cardItem = interactWithGoalFragment.getGoalAtListPosition(getAdapterPosition());
                    cardItem.setMarkedImportant(isImportant);
                    Collections.sort(parentGoalFragment.goalsActiveList, new Comparator<GoalCardItem>() {
                        @Override
                        public int compare(final GoalCardItem item1, final GoalCardItem item2) {
                            int result;
                            result = Boolean.compare(item2.isMarkedImportant(),item1.isMarkedImportant());
                            if(result==0)
                                result = Integer.compare(item2.getGoalId(),item1.getGoalId());
                                //descending sort
                            return result;
                        }
                    });
                    notifyDataSetChanged();
                }
            });
        }

        String getHolderTitleText(){return this.goalTitleTextView.getText().toString();}

    }


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

    void addNewGoalManually(long newGoalId) {
        for (Long id : fadeOutMap.keySet()) {fadeOutMap.put(id, true);}
        fadeOutMap.put(newGoalId, false);
        addingNewGoal = true;
        notifyItemInserted(0);
        notifyDataSetChanged();
        previousAction = PREVIOUS_ACTION_IS_ADDING;
        interactWithGoalFragment.enableAddGoalFab(false);
    }



    @Override
    public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.goal_cardview_layout, parent, false);

        return new GoalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoalsViewHolder holder, int position) {
        System.out.println("fadeoutMap = "+fadeOutMap);
        long itemId = getItemId(position);
        try {
            if (fadeOutMap.get(itemId) != null) {
                if (fadeOutMap.get(itemId)) {
                    //set faded alpha i.e. 0.25f if fadeOut is true i.e. if you want it to fade
                    holder.goalCardView.setAlpha(FADE_OUT_ALPHA);
                    holder.showGoalEditMenuButton.setEnabled(false);
                } else {
                    holder.goalCardView.setAlpha(DEFAULT_ALPHA);
                    holder.showGoalEditMenuButton.setEnabled(true);
                }
            }
        }
        catch (NullPointerException e){e.printStackTrace();}

        GoalCardItem cardItem = interactWithGoalFragment.getGoalAtListPosition(position);
        String goalTitle = cardItem.getGoalTitle();
        String goalDesc = cardItem.getGoalDescription();
        boolean markedImportant = cardItem.isMarkedImportant();

        if (addingNewGoal) {
            changeGoalViewMode(holder,position, GOAL_VIEW_MODE_NOW_EDITING);
            addingNewGoal = false;
        }

        if ((goalTitle != null && goalDesc != null) && !holder.isCurrentlyEditable) {
            holder.goalTitleTextView.setText(goalTitle);
            holder.goalDescTextView.setText(goalDesc);
            holder.isImportant = markedImportant;
            holder.markImportantButton.setImageResource(
                    cardItem.isMarkedImportant() ? R.drawable.ic_star_yellow_filled_30dp :R.drawable.ic_star_yellow_border_30dp);
            holder.switchEditable.setDisplayedChild(1);
        } else holder.switchEditable.setDisplayedChild(0);

    }



    @Override
    public int getItemCount() {return interactWithGoalFragment.getGoalsActiveListSize();}

    @Override
    public long getItemId(int position){return (long)interactWithGoalFragment.getGoalAtListPosition(position).getGoalId();
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
        interactWithGoalFragment.deleteGoalFromActiveList(pos);
        try{if(!fadeOutMap.isEmpty()) fadeOutMap.remove(itemId);}
        catch(IndexOutOfBoundsException e){
            Log.e("IOoBE@fadeOutMap.remove","IOoBE when removing from fadeoutmap -"+pos);
            Log.e("StackTrace",Log.getStackTraceString(e));
        }
        catch(NullPointerException e){
            Log.e("NPE @ deleteGoal","NPE occured at deleteGoalFromList position = "+pos);
            Log.e("StackTrace",Log.getStackTraceString(e));
        }
        try {notifyItemRemoved(pos);}
        // NPE can occur when you're deleting items while scrolling. In that case, removeViewAt cannot find the view.
        //For these cases, notifyDataSetChanged() works.
        catch(NullPointerException e){
            Log.e("NPE @ deleteGoal","NPE occured at deleteGoalFromList position = "+pos);
            Log.e("StackTrace",Log.getStackTraceString(e));
            parentGoalFragment.goalsRecyclerView.removeAllViews();
            notifyDataSetChanged();
        }
        // The .removeViewAt(position) removes the old ViewHolder at the given position.
        // Without this, the old ViewHolder would get repeated, i.e. after you delete the item, when
        // you add it back, the same view is generated, although the underlying list is different.
        parentGoalFragment.goalsRecyclerView.scrollToPosition(pos);
    }


}

