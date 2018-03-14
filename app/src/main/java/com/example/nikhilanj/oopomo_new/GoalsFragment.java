package com.example.nikhilanj.oopomo_new;

import android.os.Bundle;
import android.support.constraint.solver.Goal;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment implements goalInteractionInterface{

    public List<GoalCardItem> goalsList = new ArrayList<>();
    public RecyclerView goalsRecyclerView;
    public RecyclerView.Adapter goalsRecyclerViewAdapter;
    public RecyclerView.LayoutManager goalsRecyclerViewLayoutMgr;

    private TextView emptyGoalsText;

    public GoalsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(goalsList.isEmpty()){
            goalsList.add(0,new GoalCardItem("1",""));
            goalsList.add(0,new GoalCardItem("2",""));
            goalsList.add(0,new GoalCardItem("3",""));
            goalsList.add(0,new GoalCardItem("4",""));
            goalsList.add(0,new GoalCardItem("5",""));
            goalsList.add(0,new GoalCardItem("6",""));
        }
        if (savedInstanceState != null) {
            System.out.println("savedinstancestate alive");
        }}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.goals_fragment_layout, container, false);
        goalsRecyclerView = view.findViewById(R.id.goals_recycler_view);
        goalsRecyclerViewAdapter = new GoalsCardAdapter(this);

        goalsRecyclerViewLayoutMgr = new LinearLayoutManager(getContext()){
            @Override
            public boolean supportsPredictiveItemAnimations() {return true;}
        };

        goalsRecyclerView.setLayoutManager(goalsRecyclerViewLayoutMgr);
        goalsRecyclerView.setAdapter(goalsRecyclerViewAdapter);

        emptyGoalsText = view.findViewById(R.id.tv_show_if_no_goals);
        setTextIfNoGoal();

        FloatingActionButton addGoalFab = view.findViewById(R.id.fab_add_goal);
        addGoalFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){ addGoal(); }});

        ItemTouchHelper.SimpleCallback simpleItemSwipeCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {return false;}

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        markGoalDone(viewHolder.getAdapterPosition(),true);
                    }

                    @Override
                    public int getSwipeDirs(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder){
                        GoalsCardAdapter.GoalsViewHolder goalsViewHolder = (GoalsCardAdapter.GoalsViewHolder) viewHolder;
                        if(!goalsViewHolder.swipeable){return 0;}
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }
                };

        ItemTouchHelper ith = new ItemTouchHelper(simpleItemSwipeCallback);
        ith.attachToRecyclerView(goalsRecyclerView);

        return view;
    }


    public void addGoal() {
        goalsList.add(0,new GoalCardItem());
        goalsRecyclerViewAdapter.notifyItemInserted(0);
        goalsRecyclerView .smoothScrollToPosition(0);
        setTextIfNoGoal();
    }

    @Override
    public void saveGoalToList(String goalTitle,String goalDesc,int pos) {
        if(getGoalAtListPosition(pos)!=null){
            GoalCardItem item = getGoalAtListPosition(pos);
            item.setGoalTitle(goalTitle);
            item.setGoalDesciption(goalDesc);
            //goalsRecyclerViewAdapter.notifyItemChanged(pos);
            //Calling notifyItemChanged(pos) results in same EditText visible for every new item.
        }
    }

    @Override
    public void deleteGoalFromList(int position) {
        goalsList.remove(position);
        goalsRecyclerViewAdapter.notifyItemRemoved(position);
        try {goalsRecyclerView.removeViewAt(position);}
        // NPE can occur when you're deleting items while scrolling. In that case, removeViewAt cannot find the view.
        //For these cases, notifyDataSetChanged() works.
        catch(NullPointerException e){
            Log.e("NPE @ deleteGoal","NPE occured at deleteGoalFromList position = "+position);
            goalsRecyclerView.removeAllViews();
            goalsRecyclerViewAdapter.notifyDataSetChanged();
        }
        // The .removeViewAt(position) removes the old ViewHolder at the given position.
        // Without this, the old ViewHolder would get repeated, i.e. after you delete the item, when
        // you add it back, the same view is generated, although the underlying list is different.
        setTextIfNoGoal();
    }

    @Override
    public GoalCardItem getGoalAtListPosition(int position) {return goalsList.get(position);}

    @Override
    public int getGoalsListSize() {return goalsList.size();}

    @Override
    public void markGoalDone(int position,boolean done) {
        getGoalAtListPosition(position).markGoalAsDone(done);
        //write back to file saying "done"
        deleteGoalFromList(position);
        // doesnt actually "delete"
        // removes the mentioned goal from current goals list and moves it to "completed" list
        //TODO : sort out this
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content),
                "Marked as done", Snackbar.LENGTH_LONG).setAction("UNDO",new SnackBarListener());

        //set snackbar layout params so that it shows above bottom nav bar.
        FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams) snack.getView().getLayoutParams();
        int BOTTOM_NAV_BAR_HEIGHT = 60;
        float d = getContext().getResources().getDisplayMetrics().density;
        int bottomnavBarMargin = (int)(BOTTOM_NAV_BAR_HEIGHT * d);
        parameters.setMargins(0, 0, 0, bottomnavBarMargin);
        snack.getView().setLayoutParams(parameters);

        snack.show();
        //TODO : Snackbar currently overlaps FAB. fix this.
    }

    public void setTextIfNoGoal() {
        if (goalsList.size() != 0) emptyGoalsText.setVisibility(View.INVISIBLE);
        else emptyGoalsText.setVisibility(View.VISIBLE);
    }

    public class SnackBarListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Toast.makeText(getContext(),"Undid",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {super.onDetach();}


}
