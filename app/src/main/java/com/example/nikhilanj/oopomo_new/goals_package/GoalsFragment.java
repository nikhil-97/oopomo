package com.example.nikhilanj.oopomo_new.goals_package;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
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
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikhilanj.oopomo_new.IgoalFragmentActivityInterface;
import com.example.nikhilanj.oopomo_new.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
interface IloadGoalFromDb{

    List loadGoalFromDb();
}
*/
interface IgoalFragmentAdapterInteraction{
    GoalCardItem getGoalAtListPosition(int position);
    int getGoalsActiveListSize();
    void saveGoalToList(String goalTitle, String goalDesc, int position);
    void deleteGoalFromActiveList(int position);
    void enableAddGoalFab(boolean enabled);
    void clearFocusAndHideSoftInputKeyboard();
}


public class GoalsFragment extends Fragment implements IgoalFragmentAdapterInteraction {

    public List<GoalCardItem> goalsActiveList = new ArrayList<>();
    private List<GoalCardItem> goalsDoneList = new ArrayList<>();
    static int activeGoalId = 0;


    private IgoalFragmentActivityInterface interactWithActivity;

    RecyclerView goalsRecyclerView;
    GoalsCardAdapter goalsRecyclerViewAdapter;
    RecyclerView.LayoutManager goalsRecyclerViewLayoutMgr;
    private TextView emptyGoalsText;
    private FloatingActionButton addGoalFab;
    private ColorStateList defaultAddFabEnabledColour;
    private ColorStateList defaultAddFabDisabledColour;
    private int goalMarkedDonePosition = -1;
    final int BOTTOM_NAV_BAR_HEIGHT = 60;


    public GoalsFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: goalsActiveList = readGoalsActiveListFromDb();
        //TODO: goalsDoneList = readGoalsDoneListFromDb();

/*
        //For testing purposes
        if(goalsActiveList.isEmpty()){
            for(int i = 1;i<=10;i++){
                int id = activeGoalId+=1;
                String t = String.format(Locale.getDefault(),"Goal %d",id);
                String d = String.format(Locale.getDefault(),"This is description for goal %d",i);
                addGoalToActiveList(0,new GoalCardItem(id,t,d,false));
            }
        }

*/
        if (savedInstanceState != null) {
            System.out.println("savedinstancestate alive");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.goals_fragment_layout, container, false);

        interactWithActivity = (IgoalFragmentActivityInterface) getActivity(); //asuming it's always mainactivity

        goalsRecyclerView = view.findViewById(R.id.goals_recycler_view);
        goalsRecyclerViewAdapter = new GoalsCardAdapter(this);

        goalsRecyclerViewLayoutMgr = new LinearLayoutManager(getContext()){
            @Override
            public boolean supportsPredictiveItemAnimations() {return true;}};

        goalsRecyclerView.setLayoutManager(goalsRecyclerViewLayoutMgr);
        goalsRecyclerView.setAdapter(goalsRecyclerViewAdapter);

        emptyGoalsText = view.findViewById(R.id.tv_show_if_no_goals);

        addGoalFab = view.findViewById(R.id.fab_add_goal);
        defaultAddFabEnabledColour = addGoalFab.getBackgroundTintList();
        defaultAddFabDisabledColour = ColorStateList.valueOf(getResources().getColor(R.color.addFabDisabledColour,null));

        addGoalFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){ addGoalManually(); }});

        ItemTouchHelper.SimpleCallback simpleItemSwipeCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {return false;}

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        GoalsCardAdapter.GoalsViewHolder holder = (GoalsCardAdapter.GoalsViewHolder) viewHolder;
                        goalMarkedDonePosition = viewHolder.getAdapterPosition();
                        String title = holder.getHolderTitleText();
                        showMarkGoalSnackBar(title);
                    }

                    @Override
                    public int getSwipeDirs(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder){
                        GoalsCardAdapter.GoalsViewHolder goalsViewHolder = (GoalsCardAdapter.GoalsViewHolder) viewHolder;
                        if(!goalsViewHolder.isCurrentlySwipeable){return 0;}
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }
                };

        ItemTouchHelper ith = new ItemTouchHelper(simpleItemSwipeCallback);
        ith.attachToRecyclerView(goalsRecyclerView);

        goalsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>5) if(addGoalFab.getVisibility()== View.VISIBLE) addGoalFab.setVisibility(View.GONE);
                if(dy<5) if(addGoalFab.getVisibility()== View.GONE) addGoalFab.setVisibility(View.VISIBLE);
            }
        });

        setTextIfNoGoal();

        return view;
    }

    @Override
    public void onDetach() {super.onDetach();}

    private long addGoalToActiveList(int position){
        //method to add empty goal
        int newGoalId = activeGoalId+=1;
        GoalCardItem newGoal = new GoalCardItem(newGoalId);
        goalsActiveList.add(position,newGoal);
        return newGoalId;
    }

    private int addGoalToActiveList(int position, GoalCardItem item){
        //overloaded method to add already created goals
        int id = item.getGoalId();
        goalsActiveList.add(position,item);
        goalsRecyclerViewAdapter.fadeOutMap.put((long)id,false);
        //catch(NullPointerException e){e.printStackTrace();}
        return id;
    }

    private void addGoalManually() {
        long newGoalId = addGoalToActiveList(0);
        goalsRecyclerView.scrollToPosition(0);
        goalsRecyclerViewAdapter.addNewGoalManually(newGoalId);
        setTextIfNoGoal();
    }

    private GoalCardItem deleteGoalFromDoneList(int position){
        //Only GoalsFragment handles the doneList (for now).
        try{
            GoalCardItem deletedItem = goalsDoneList.get(position);
            goalsDoneList.remove(deletedItem);
            return deletedItem;
        }
        catch(IndexOutOfBoundsException e){e.printStackTrace();}
        return null;
    }

    private void showMarkGoalSnackBar(String goalTitle){
        //set snackbar layout params so that it shows above bottom nav bar.

        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content),
                String.format(Locale.getDefault(), "Marked '%s' as done", goalTitle),
                Snackbar.LENGTH_LONG).setAction("UNDO", new SnackBarListener());
        snack.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar snackbar,int event){
                ViewPropertyAnimator animator = addGoalFab.animate().translationY(0);
                interactWithActivity.setBottomNavBarElevation(interactWithActivity.getBottomNavBarDefaultElevation());
                //set elevation to default on snackbar dismissed
                animator.start();
            }

            @Override
            public void onShown(Snackbar snackbar){
                int snackbarHeight = snackbar.getView().getHeight();
                ViewPropertyAnimator animator = addGoalFab.animate().translationY(-snackbarHeight);
                interactWithActivity.setBottomNavBarElevation(0.0f);
                animator.start();
                markGoalDone(goalMarkedDonePosition);
            }
        });

        FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams) snack.getView().getLayoutParams();
        float d = getContext().getResources().getDisplayMetrics().density;
        int bottomnavBarMargin = (int)(BOTTOM_NAV_BAR_HEIGHT * d);
        parameters.setMargins(0, 0, 0, bottomnavBarMargin);
        snack.getView().setLayoutParams(parameters);
        snack.show();
    }

    private class SnackBarListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Toast.makeText(getContext(),"Undid",Toast.LENGTH_SHORT).show();
            undoMarkGoalDone(goalMarkedDonePosition);
        }
    }

    private void markGoalDone(int position) {
        GoalCardItem item = getGoalAtListPosition(position);
        item.markGoalAsDone(true);
        //write back to file saying "done"
        deleteGoalFromActiveList(position);
        goalsDoneList.add(0,item);
        // removes the mentioned goal from current goals list and moves it to "completed" list
        goalsRecyclerViewAdapter.notifyDataSetChanged();

    }

    private void undoMarkGoalDone(int position) {
        //write back to file saying "done"
        GoalCardItem deletedGoal = deleteGoalFromDoneList(0);
        addGoalToActiveList(position,deletedGoal);
        getGoalAtListPosition(position).markGoalAsDone(false);
        goalsRecyclerViewAdapter.notifyDataSetChanged();
        try{goalsRecyclerView.scrollToPosition(position);}
        catch(Exception e){goalsRecyclerView.scrollToPosition(position);}
        setTextIfNoGoal();
    }

    private void setTextIfNoGoal() {
        if (goalsActiveList.size() != 0) emptyGoalsText.setVisibility(View.INVISIBLE);
        else {
            emptyGoalsText.setVisibility(View.VISIBLE);
            enableAddGoalFab(true);
        }
    }


    //Interface methods
    @Override
    public GoalCardItem getGoalAtListPosition(int position) {
        System.out.println("goalsActive list = "+goalsActiveList);
        return goalsActiveList.get(position);}

    @Override
    public int getGoalsActiveListSize() {return goalsActiveList.size();}

    @Override
    public void saveGoalToList(String goalTitle,String goalDesc,int pos) {
        if(getGoalAtListPosition(pos)!=null){
            GoalCardItem item = getGoalAtListPosition(pos);
            item.setGoalTitle(goalTitle);
            item.setGoalDesciption(goalDesc);
        }
    }

    @Override
    public void deleteGoalFromActiveList(int position) {
        goalsActiveList.remove(position);
        setTextIfNoGoal();
    }

    @Override
    public void enableAddGoalFab(boolean enabled){
        addGoalFab.setEnabled(enabled);
        addGoalFab.setBackgroundTintList(enabled?defaultAddFabEnabledColour:defaultAddFabDisabledColour);
    }

    @Override
    public void clearFocusAndHideSoftInputKeyboard(){
        View currentFocus = getActivity().getCurrentFocus();
        try{ currentFocus.clearFocus(); }
        catch(NullPointerException e){ e.printStackTrace(); }

        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

}
