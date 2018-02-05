package com.example.nikhilanj.oopomo_new;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.CircleProgress;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.example.nikhilanj.oopomo_new.appimer.getMinutes;
import static com.example.nikhilanj.oopomo_new.appimer.getSeconds;


public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {} //essential empty constructor
    boolean track;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Created View", "Not ded");
        track = false;}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);

        final Button profilesbutton = (Button) view.findViewById(R.id.timeProfilesButton);
        profilesbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(getContext(), "Under Construction !", Toast.LENGTH_SHORT).show();

            }
        });

        final Button starttimebutton = (Button) view.findViewById(R.id.startTimeButton);
        starttimebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                track=!track;
                if(track==true) {
                    trackTime();
                    starttimebutton.setText("STOP");
                    Toast.makeText(getContext(), "Starting Time !", Toast.LENGTH_SHORT).show();
                }
                else if (track==false){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.stoptrackingdialog_message).setTitle(R.string.stoptrackingdialog_title);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {starttimebutton.setText("START");}});
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            track = true;
                            Toast.makeText(getContext(), "Pressed NO", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
        return view;
    }

    private void trackTime(){
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
