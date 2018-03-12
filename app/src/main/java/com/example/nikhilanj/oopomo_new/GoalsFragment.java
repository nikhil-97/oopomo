package com.example.nikhilanj.oopomo_new;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.LinkedList;

public class GoalsFragment extends Fragment implements goalInteractionInterface{

    public LinkedList<GoalCardItem> goalsList = new LinkedList<>();
    private RecyclerView goalsRecyclerView;
    private RecyclerView.Adapter goalsRecyclerViewAdapter;

    private TextView emptyGoalsText;
    public GoalsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.goals_fragment_layout, container, false);
        goalsRecyclerView = view.findViewById(R.id.goals_recycler_view);
        RecyclerView.LayoutManager goalsRecyclerViewLayoutMgr = new LinearLayoutManager(getContext());
        goalsRecyclerViewAdapter = new GoalsCardAdapter(this);

        goalsRecyclerView.setLayoutManager(goalsRecyclerViewLayoutMgr);
        goalsRecyclerView.setAdapter(goalsRecyclerViewAdapter);

        emptyGoalsText = view.findViewById(R.id.tv_show_if_no_goals);
        //System.out.println("goalslistsize = "+goalsList.size());
        setTextIfNoGoal();

        FloatingActionButton addGoalFab = view.findViewById(R.id.fab_add_goal);
        addGoalFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                addGoal();
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemSwipeCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        markGoalDone(viewHolder.getAdapterPosition(),true);
                    }
                };

        ItemTouchHelper ith = new ItemTouchHelper(simpleItemSwipeCallback);
        ith.attachToRecyclerView(goalsRecyclerView);

        return view;
    }

    @Override
    public void onDetach() {super.onDetach();}

    public void addGoal() {
        goalsList.addFirst(new GoalCardItem());
        goalsRecyclerViewAdapter.notifyItemInserted(0);
        goalsRecyclerView .smoothScrollToPosition(0);
        setTextIfNoGoal();
        System.out.println("goalsize = "+goalsList.size());
    }

    @Override
    public void saveGoalToList(String goalTitle,String goalDesc,int pos) {
        if(goalsList.get(pos)!=null){
            GoalCardItem item = goalsList.get(pos);
            item.setGoalTitle(goalTitle);
            item.setGoalDesciption(goalDesc);
        }
        System.out.println("saveGoaltolist"+goalsList.peek());
    }

    @Override
    public void deleteGoalFromList(int position) {
        goalsList.remove(position);
        goalsRecyclerView.removeViewAt(position);
        goalsRecyclerViewAdapter.notifyItemRemoved(position);
        goalsRecyclerViewAdapter.notifyDataSetChanged();
        System.out.println("deletegoalfromlist"+goalsList);
        setTextIfNoGoal();
    }


    @Override
    public void markGoalDone(int position,boolean done) {
        goalsList.get(position).markGoalAsDone(done);
        //write back to file
        deleteGoalFromList(position);
        //doesnt actually "delete"
        //TODO : sort out this
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content),
                "Marked as done", Snackbar.LENGTH_LONG).setAction("UNDO",null);
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
}
