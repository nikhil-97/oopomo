package com.example.nikhilanj.oopomo_new;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;
import com.github.lzyzsd.circleprogress.ArcProgress;

import static com.example.nikhilanj.oopomo_new.appimer.getMinutes;
import static com.example.nikhilanj.oopomo_new.appimer.getSeconds;


public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {} //essential empty constructor
    private BottomSheetBehavior mBottomSheetBehavior;
    private Button profilesbutton;
    private Button startbutton;
    private Button pausebutton;
    private Button stopbutton;

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

        mBottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet));
        profilesbutton = (Button) view.findViewById(R.id.timeProfilesButton);
        startbutton = (Button) view.findViewById(R.id.startTimeButton);
        pausebutton = (Button) view.findViewById(R.id.pauseTimeButton);
        stopbutton = (Button) view.findViewById(R.id.stopTimeButton);

        profilesbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getContext(), "Under Construction !", Toast.LENGTH_SHORT).show();
                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    profilesbutton.animate().rotation(0).start();
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    profilesbutton.animate().rotation(180).start();
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }


            }
        });

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pausebutton.setAlpha((float)0.001);
                pausebutton.setVisibility(View.VISIBLE);
                pausebutton.setAlpha((float)0.001);
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
                if(pausebutton.getText().equals("PAUSE")){
                    //TODO : pauseCountdown;
                    Toast.makeText(getContext(), "Time Paused!", Toast.LENGTH_SHORT).show();

                }
                else if(pausebutton.getText().equals("RESUME")){
                    //TODO : resumeCountdown;
                    Toast.makeText(getContext(), "Time Resumed!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //TODO : pauseCountdown;
                showStopAlert();
            }
        });

        return view;
    }

    private void buttonFadeOutAnimation(Button somebutton,long fadeouttime){
        ViewPropertyAnimator buttonanimation = somebutton.animate().alpha((float)0.01).setDuration(fadeouttime);
        buttonanimation.start();
        somebutton.setEnabled(false);
    }

    private void buttonFadeInAnimation(Button somebutton,long fadeintime){
        ViewPropertyAnimator buttonanimation = somebutton.animate().alpha(1).setDuration(fadeintime);
        buttonanimation.start();
        somebutton.setEnabled(true);
    }

    private void showStopAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.stoptrackingdialog_message).setTitle(R.string.stoptrackingdialog_title);
        boolean response;
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {stopTimerYes();}
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {stopTimerNo();}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void stopTimerYes(){
        Toast.makeText(getContext(), "Pressed YES", Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "stopCountdown()", Toast.LENGTH_SHORT).show();
        buttonFadeOutAnimation(pausebutton, 1000);
        buttonFadeOutAnimation(stopbutton, 1000);
        //TODO : stopCountdown();
        buttonFadeInAnimation(startbutton, 1000);
        Toast.makeText(getContext(), "Time Stopped!", Toast.LENGTH_SHORT).show();
    }

    private void stopTimerNo(){
        Toast.makeText(getContext(), "Pressed NO", Toast.LENGTH_SHORT).show();
        //TODO : resumeCountdown()
    }

    /*private void trackTime(){
        final String[] message = new String[]{"hello","bello","mello"};
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (track) {
                        message[0] = String.format("%02d", getSeconds());
                        message[1] = String.format("%02d", getMinutes());
                        message[2] = message[1] + ':' + message[0];
                        try {
                            getView().post(new Runnable() {
                                public void run() {
                                    try {
                                        TextView tv = getView().findViewById(R.id.timeView);
                                        int secondstime = Integer.parseInt(message[0]) * 60;
                                        int progress = (int) (((float) secondstime / 60.0));
                                        //TODO ; sort out this random shit
                                        tv.setText(message[2]);
                                        ArcProgress arcprog = getView().findViewById(R.id.arc_progress);
                                        arcprog.setProgress(progress);
                                    } catch (NullPointerException n) {
                                        ;
                                    }
                                }
                            });
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e2) {
                                System.out.println("thread interrupted");
                                Thread.currentThread().interrupt();
                            }

                        } catch (NullPointerException e1) {
                            ;
                        }

                    }
                }
            }
        }).start();
    }*/


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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
