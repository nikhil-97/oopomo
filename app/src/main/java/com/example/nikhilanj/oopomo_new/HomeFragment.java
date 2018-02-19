package com.example.nikhilanj.oopomo_new;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;
import android.widget.Toast;

interface timeChangeListenerInterface{
    void updateTimeView(int data);
}


public class HomeFragment extends Fragment implements timeChangeListenerInterface{

    private OnFragmentInteractionListener mListener;

    MainActivity mainactivity = (MainActivity) getActivity();

    public HomeFragment() {} //essential empty constructor

    private FloatingActionButton edittimebutton;
    private FloatingActionButton startbutton;
    private FloatingActionButton pausebutton;
    private FloatingActionButton stopbutton;

    timeChangeListenerInterface tcli;
    private Handler uiHandler;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Created View", "Not ded");}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);

        //timeSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet));
        //profilesbutton = (Button) view.findViewById(R.id.timeProfilesButton);

        edittimebutton = view.findViewById(R.id.edit_time_button);
        startbutton = view.findViewById(R.id.startTimeButton);
        pausebutton = view.findViewById(R.id.pauseTimeButton);
        stopbutton = view.findViewById(R.id.stopTimeButton);

        edittimebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showTimeSettingsFragment();
            }
        });

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pausebutton.setAlpha((float)0.001);
                pausebutton.setVisibility(View.VISIBLE);
                stopbutton.setAlpha((float)0.001);
                stopbutton.setVisibility(View.VISIBLE);

                buttonFadeAnimation(startbutton,(float)0.001,1000,false);
                //startCountdown();
                buttonFadeAnimation(pausebutton,(float)1,1200,true);
                buttonFadeAnimation(stopbutton,(float)1,1200,true);
                Toast.makeText(getContext(), "Starting Time !", Toast.LENGTH_SHORT).show();
            }
        });

        pausebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                showStopAlert();
            }
        });

        return view;
    }

    public void showTimeSettingsFragment() {
        BottomSheetDialogFragment timeProfileFragment = new TimeProfileSheetFragment();
        getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        timeProfileFragment.show(getFragmentManager(), timeProfileFragment.getTag());

    }

    private void buttonFadeAnimation(FloatingActionButton somebutton,float toAlpha,long fadetime,boolean setenable){
        ViewPropertyAnimator buttonanimation = somebutton.animate().alpha(toAlpha).setDuration(fadetime);
        buttonanimation.start();
        somebutton.setEnabled(setenable);
    }

    private void showStopAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage( R.string.stoptrackingdialog_message).setTitle(R.string.stoptrackingdialog_title);
        boolean response;
        builder.setPositiveButton(R.string.stoptrackingdialog_quitmsg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {quitTimer();}
        });
        builder.setNeutralButton(R.string.stoptrackingdialog_skipcurrent, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {skipCurrentSession();}
        });
        builder.setNegativeButton(R.string.stoptrackingdialog_nogoback, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {continueTimer();}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void quitTimer(){
        Toast.makeText(getContext(), "stopCountdown()", Toast.LENGTH_SHORT).show();
        buttonFadeAnimation(pausebutton, (float)0.001,1000,false);
        buttonFadeAnimation(stopbutton, (float)0.001,1000,false);
        //TODO : stopCountdown();
        buttonFadeAnimation(startbutton,1, 1000,true);
    }

    private void skipCurrentSession(){
        Toast.makeText(getContext(), "skipSession()", Toast.LENGTH_SHORT).show();
        //TODO : skipSession()
    }

    private void continueTimer(){
        Toast.makeText(getContext(), "resumeCountdown()", Toast.LENGTH_SHORT).show();
        //TODO : resumeCountdown()
    }


    public void updateTimeView(int data) {
        final String[] placeholder_string = new String[]{"placeholder"};
        System.out.println("updateTimeView");
        placeholder_string[0] = Integer.toString(data);
        try {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("in uiHandler.post");
                        TextView timeview = getView().findViewById(R.id.timeView);
                        timeview.setText(placeholder_string[0]);
                    }
                    catch (NullPointerException e) {
                        Log.v("Cannot find timeView", "NPE @ find timeView");
                    }
                }
            });

        }
        catch (NullPointerException e) {
            Log.v("Cannot find View", "NPE @ getView().post()");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
