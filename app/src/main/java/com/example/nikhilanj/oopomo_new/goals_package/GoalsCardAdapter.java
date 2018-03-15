package com.example.nikhilanj.oopomo_new.goals_package;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.nikhilanj.oopomo_new.R;

import java.util.HashMap;


public class GoalsCardAdapter extends RecyclerView.Adapter<GoalsCardAdapter.GoalsViewHolder>{

    private GoalsFragment parentGoalFragment;
    private goalInteractionInterface goalInteractionListener;

    // What is fadeOutMap ?
    // When a particular goal("item") is selected, we want all other items to fade out, so that only
    // the current goal is shown. The fadeOut needs to be done in the onBindViewHolder.
    // We need to let the onBindViewHolder know which items to fadeOut and which to set on focus(i.e. not fadeOut).
    // So we use a HashMap, which holds the key as the .hashCode() of the items in the goalsList.
    // Essentially it is like :   GoalCardItem.hashCode() --> fadeOut?
    // fadeOut false implies don't fade them out. true implies they get faded out.

    // If you have any better ideas, raise an issue on GitHub.

    @SuppressLint("UseSparseArrays")
    private HashMap<Long,Boolean> fadeOutMap = new HashMap<>();
    //private LongSparseArray<Boolean> fadeOutMap = new LongSparseArray<>();

    // IDE would prompt you to use LongSparseArray(LSA) instead of HashMap here, as LSA is more memory efficient.
    // Although find the key is O(logN) in LSA, we would have quite a few items here (assume not more than 50)
    // But however, we find the keys every single time onBindViewHolder is called, i.e. every time view is generated.
    // There would be far more key accesses. Hence time overhead is more important than memory overhead here.
    // Also, as it's only a few items, the memory advantage is not considerable.


    GoalsCardAdapter(GoalsFragment goalsFragment){
        setHasStableIds(true);
        this.parentGoalFragment = goalsFragment;
        //this.parentGoalsRecyclerView = goalsFragment.goalsRecyclerView;
        this.goalInteractionListener = (goalInteractionInterface) this.parentGoalFragment;
        for(int i=0;i<goalInteractionListener.getGoalsListSize();i++){
            fadeOutMap.put(getItemId(i),false);
        }

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
        FloatingActionButton showGoalEditMenuButton;
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
            final float defaultCardElevation = goalCardView.getCardElevation();

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
                    goalCardView.setCardElevation(defaultCardElevation);
                    swipeable = true;
                    editable = false;
                    for (Long id : fadeOutMap.keySet()) {fadeOutMap.put(id,false);}
                    notifyDataSetChanged();
                    //switchEditable.showNext(); //switch view to non-editing layout
                    parentGoalFragment.addGoalFab.setEnabled(true);
                    parentGoalFragment.addGoalFab.setBackgroundTintList(parentGoalFragment.defaultAddFabEnabledColour);
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
                                    openGoalForEditing(getAdapterPosition());
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

        private void openGoalForEditing(int adapterPos){
            GoalsViewHolder holder = (GoalsViewHolder) parentGoalFragment.goalsRecyclerView.findViewHolderForAdapterPosition(adapterPos);
            GoalCardItem goalItem = goalInteractionListener.getGoalAtListPosition(adapterPos);
            parentGoalFragment.goalsRecyclerView.smoothScrollToPosition(adapterPos);
            parentGoalFragment.addGoalFab.setEnabled(false);
            parentGoalFragment.addGoalFab.setBackgroundTintList(parentGoalFragment.defaultAddFabDisabledColour);
            holder.goalTitleEditText.setText(goalItem.getGoalTitle());
            holder.goalDescEditText.setText(goalItem.getGoalDescription());
            holder.switchEditable.showPrevious();
            holder.goalTitleEditText.requestFocus();
            holder.goalCardView.setCardElevation((float)10);
            holder.swipeable = false;
            holder.editable = true;
            long currentItemId = GoalsCardAdapter.this.getItemId(adapterPos);
            for (Long id : fadeOutMap.keySet()) {fadeOutMap.put(id,id!=currentItemId);}
            //iterate through the map, set false for all other ids, true for the id which matches current adapterposition id
            notifyDataSetChanged();

        }
    }

    void addNewGoal(long newGoalId){
        //currentEditPosition = 0;
        //fadeOutOthers = true;
        for (Long id : fadeOutMap.keySet()) {fadeOutMap.put(id,true);}
        fadeOutMap.put(newGoalId,false);
        parentGoalFragment.addGoalFab.setEnabled(false);
        parentGoalFragment.addGoalFab.setBackgroundTintList(parentGoalFragment.defaultAddFabDisabledColour);
        notifyDataSetChanged();
        notifyDataSetChanged();
    }

    @Override
    public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.goal_cardview_editable_layout, parent,false);

        return new GoalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoalsViewHolder holder,int position) {
        long itemId = getItemId(position);
        if (fadeOutMap.get(itemId)) {
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

