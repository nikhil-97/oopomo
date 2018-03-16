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
    //void markGoalDone(int position, boolean done);
}


public class GoalsFragment extends Fragment implements IgoalFragmentAdapterInteraction {

    private List<GoalCardItem> goalsActiveList = new ArrayList<>();
    private List<GoalCardItem> goalsDoneList = new ArrayList<>();

    private IgoalFragmentActivityInterface interactWithActivity;

    RecyclerView goalsRecyclerView;
    GoalsCardAdapter goalsRecyclerViewAdapter;
    RecyclerView.LayoutManager goalsRecyclerViewLayoutMgr;
    private TextView emptyGoalsText;
    private FloatingActionButton addGoalFab;
    private ColorStateList defaultAddFabEnabledColour;
    private ColorStateList defaultAddFabDisabledColour;
    private Snackbar snack;
    private boolean goalMarkedAsDone = false;
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
            for(int i = 1;i<=6;i++){
                String t = String.format(Locale.getDefault(),"Goal %d",i);
                String d = String.format(Locale.getDefault(),"This is description for goal %d",i);
                addGoalToActiveList(t,d);
            }
        }
        */

        if (savedInstanceState != null) {
            System.out.println("savedinstancestate alive");
        }}

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
                        goalMarkedAsDone = true;
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
                if(dy>1) if(addGoalFab.getVisibility()== View.VISIBLE) addGoalFab.setVisibility(View.GONE);
                if(dy<0) if(addGoalFab.getVisibility()== View.GONE) addGoalFab.setVisibility(View.VISIBLE);
            }
        });

        setTextIfNoGoal();

        return view;
    }

    public long addGoalToActiveList(int position){
        //method to add empty goal, i.e. a new goal
            GoalCardItem newGoal = new GoalCardItem();
            goalsActiveList.add(position,newGoal);
            return newGoal.hashCode();
            }

    public long addGoalToActiveList(String title,String desc){
        //overloaded method to add one with pre-existing title, description
        GoalCardItem newGoal = new GoalCardItem(title,desc);
        goalsActiveList.add(0,newGoal);
        return newGoal.hashCode();
    }


    public void addGoalManually() {
        long newGoalId = addGoalToActiveList(0);
        goalsRecyclerView.scrollToPosition(0);
        goalsRecyclerViewAdapter.addNewGoal(newGoalId);
        setTextIfNoGoal();
    }

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
        try {goalsRecyclerView.removeViewAt(position);}
        // NPE can occur when you're deleting items while scrolling. In that case, removeViewAt cannot find the view.
        //For these cases, notifyDataSetChanged() works.
        catch(NullPointerException e){
            Log.e("NPE @ deleteGoal","NPE occured at deleteGoalFromList position = "+position);
            Log.e("StackTrace",Log.getStackTraceString(e));
            goalsRecyclerView.removeAllViews();
            goalsRecyclerViewAdapter.notifyDataSetChanged();
        }
        // The .removeViewAt(position) removes the old ViewHolder at the given position.
        // Without this, the old ViewHolder would get repeated, i.e. after you delete the item, when
        // you add it back, the same view is generated, although the underlying list is different.
        setTextIfNoGoal();
    }

    @Override
    public GoalCardItem getGoalAtListPosition(int position) {return goalsActiveList.get(position);}

    @Override
    public int getGoalsActiveListSize() {return goalsActiveList.size();}

    public void showMarkGoalSnackBar(String goalTitle){
        //set snackbar layout params so that it shows above bottom nav bar.

        snack = Snackbar.make(getActivity().findViewById(android.R.id.content),
                String.format(Locale.getDefault(),"Marked '%s' as done",goalTitle),
                Snackbar.LENGTH_LONG).setAction("UNDO",new SnackBarListener());
        snack.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar snackbar,int event){
                int snackbarHeight = snackbar.getView().getHeight();
                ViewPropertyAnimator animator = addGoalFab.animate().translationYBy(+snackbarHeight);
                interactWithActivity.setBottomNavBarElevation(interactWithActivity.getBottomNavBarDefaultElevation());
                //set elevation to default on snackbar dismissed
                animator.start();
                markGoalDone(goalMarkedDonePosition);
            }

            @Override
            public void onShown(Snackbar snackbar){
                int snackbarHeight = snackbar.getView().getHeight();
                ViewPropertyAnimator animator = addGoalFab.animate().translationYBy(-snackbarHeight);
                interactWithActivity.setBottomNavBarElevation(0.0f);
                animator.start();
            }
        });

        FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams) snack.getView().getLayoutParams();
        float d = getContext().getResources().getDisplayMetrics().density;
        int bottomnavBarMargin = (int)(BOTTOM_NAV_BAR_HEIGHT * d);
        parameters.setMargins(0, 0, 0, bottomnavBarMargin);
        snack.getView().setLayoutParams(parameters);

        snack.show();
        //TODO : Snackbar currently overlaps FAB. fix this.
    }

    public class SnackBarListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Toast.makeText(getContext(),"Undid",Toast.LENGTH_SHORT).show();
            //undoMarkAsDone();
        }
    }

    public void markGoalDone(int position) {
        GoalCardItem item = getGoalAtListPosition(position);
        item.markGoalAsDone(true);
        //write back to file saying "done"
        deleteGoalFromActiveList(position);
        goalsDoneList.add(0,item);
        goalMarkedAsDone = false;
        // removes the mentioned goal from current goals list and moves it to "completed" list

    }

    public void undoMarkGoalDone(int position,boolean done) {
        getGoalAtListPosition(position).markGoalAsDone(false);
        //write back to file saying "done"
        deleteGoalFromDoneList(position);
        addGoalToActiveList(position);
        // doesnt actually "delete"
        // removes the mentioned goal from current goals list and moves it to "completed" list
        //TODO : sort out this
    }

    public void deleteGoalFromDoneList(int position){
        goalsDoneList.remove(position);
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

    public void setTextIfNoGoal() {
        if (goalsActiveList.size() != 0) emptyGoalsText.setVisibility(View.INVISIBLE);
        else {
            emptyGoalsText.setVisibility(View.VISIBLE);
            enableAddGoalFab(true);
        }
    }

    @Override
    public void onDetach() {super.onDetach();}


}
