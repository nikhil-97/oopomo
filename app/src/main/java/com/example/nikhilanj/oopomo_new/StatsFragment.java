package com.example.nikhilanj.oopomo_new;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StatsFragment extends Fragment{

    private OnFragmentInteractionListener mListener;

    public StatsFragment() {}

    private TextView statstextview;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.stats_fragment_layout, container, false);
        RadioGroup rangeRadioGroup = (RadioGroup)view.findViewById(R.id.rangeradiogroup);
        RadioButton dailyradiobutton = view.findViewById(R.id.radio_daily);
        statstextview = view.findViewById(R.id.statstext);
        rangeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked_id) {
                if(checked_id==R.id.radio_daily){showDailyStatsFragment();}

                else if(checked_id==R.id.radio_weekly){showWeeklyStatsFragment();}

                else if(checked_id==R.id.radio_monthly){showMonthlyStatsFragment();}
            }
        });
        return view;
    }

    private void showDailyStatsFragment(){
        //Toast.makeText(getContext(),"Daily Stats",Toast.LENGTH_SHORT).show();
        statstextview.setText("Daily Stats");
    }

    private void showWeeklyStatsFragment(){
        //Toast.makeText(getContext(),"Weekly Stats",Toast.LENGTH_SHORT).show();
        statstextview.setText("Weekly Stats");
    }

    private void showMonthlyStatsFragment(){
        //Toast.makeText(getContext(),"Monthly Stats",Toast.LENGTH_SHORT).show();
        statstextview.setText("Monthly Stats");
    }



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