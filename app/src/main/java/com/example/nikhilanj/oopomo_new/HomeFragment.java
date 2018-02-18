package com.example.nikhilanj.oopomo_new;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Toast;


public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {} //essential empty constructor

    private FloatingActionButton edittimebutton;
    private FloatingActionButton startbutton;
    private FloatingActionButton pausebutton;
    private FloatingActionButton stopbutton;

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

                buttonFadeOutAnimation(startbutton,1000);
                //startCountdown();
                buttonFadeInAnimation(pausebutton,1200);
                buttonFadeInAnimation(stopbutton,1200);
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
    private void buttonFadeOutAnimation(FloatingActionButton somebutton,long fadeouttime){
        ViewPropertyAnimator buttonanimation = somebutton.animate().alpha((float)0.01).setDuration(fadeouttime);
        buttonanimation.start();
        somebutton.setEnabled(false);
    }

    private void buttonFadeInAnimation(FloatingActionButton somebutton,long fadeintime){
        ViewPropertyAnimator buttonanimation = somebutton.animate().alpha(1).setDuration(fadeintime);
        buttonanimation.start();
        somebutton.setEnabled(true);
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
        buttonFadeOutAnimation(pausebutton, 1000);
        buttonFadeOutAnimation(stopbutton, 1000);
        //TODO : stopCountdown();
        buttonFadeInAnimation(startbutton, 1000);
    }

    private void skipCurrentSession(){
        Toast.makeText(getContext(), "skipSession()", Toast.LENGTH_SHORT).show();
        //TODO : skipSession()
    }

    private void continueTimer(){
        Toast.makeText(getContext(), "resumeCountdown()", Toast.LENGTH_SHORT).show();
        //TODO : resumeCountdown()
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(FloatingActionButton fb) {
        if (mListener != null) {
            mListener.onFragmentInteraction(fb);
        }
    }*/




    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

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
