package com.example.nikhilanj.oopomo_new;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;


public class TimeProfileSheetFragment extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener,getSetTimesInterface {

    private LinkedHashMap<String, List<Integer>> map;
    private EditText addcustomtextentry;
    private SeekBar focustimeseekbar;
    private TextView focustimeview;
    private SeekBar shortbreaktimeseekbar;
    private TextView shortbreaktimeview;
    private SeekBar longbreaktimeseekbar;
    private TextView longbreaktimeview;
    private SeekBar repeatseekbar;
    private TextView repeatsview;
    private Button savebutton;
    private Button deletebutton;

    private int focustime_set;
    private int shortbreaktime_set;
    private int longbreaktime_set;
    private int repeats_set;




    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_layout, null);
        dialog.setContentView(contentView);

        Spinner timeprofile_spinner = contentView.findViewById(R.id.time_profiles_spinner);
        map = new LinkedHashMap<String, List<Integer>>();
        List<Integer> classic = new ArrayList<Integer>();
        classic.addAll(Arrays.asList(0,25,5,15,4));
        map.put("Classic", classic);

        List<Integer> superfocus = new ArrayList<Integer>();
        superfocus.addAll(Arrays.asList(0,60,10,20,6));
        map.put("Super Focus", superfocus);

        List<Integer> custom = new ArrayList<Integer>();
        custom.addAll(Arrays.asList(1,25,5,15,4));
        map.put("Add Custom", custom);

        List<String> timeProfilesNamesList = new ArrayList<String>(map.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,timeProfilesNamesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeprofile_spinner.setAdapter(adapter);
        timeprofile_spinner.setOnItemSelectedListener(this);

        addcustomtextentry = contentView.findViewById(R.id.addcustomtext);
        addcustomtextentry.setHint("Custom1");

        focustimeseekbar = contentView.findViewById(R.id.focustimeseekbar);
        focustimeview    = contentView.findViewById(R.id.focustimeseekbarvalue);
        focustimeseekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int focustime;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        focustime = progresValue;
                        focustimeview.setText(getString(R.string.focustimeviewstring,focustime));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        setFocusTime(focustime);
                        setHomeFragmentTimeView(this.focustime);
                    }
                });

        shortbreaktimeseekbar = contentView.findViewById(R.id.shortbreaktimeseekbar);
        shortbreaktimeview    = contentView.findViewById(R.id.shortbreaktimeseekbarvalue);
        shortbreaktimeseekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int shortbreak;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        shortbreak = progresValue;
                        shortbreaktimeview.setText(getString(R.string.shortbreaktimeviewstring,shortbreak));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        setShortBreakTime(shortbreak);
                    }
                });

        longbreaktimeseekbar = contentView.findViewById(R.id.longbreaktimeseekbar);
        longbreaktimeview    = contentView.findViewById(R.id.longbreaktimeseekbarvalue);
        longbreaktimeseekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int longbreak;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        longbreak = progresValue;
                        longbreaktimeview.setText(getString(R.string.longbreaktimeviewstring,longbreak));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        setLongBreakTime(longbreak);
                    }
                });

        repeatseekbar = contentView.findViewById(R.id.intervalseekbar);
        repeatsview   = contentView.findViewById(R.id.intervalseekbarvalue);
        repeatseekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int repeats;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        repeats = progresValue+1;
                        repeatsview.setText(getString(R.string.repeatsviewstring, repeats));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        setRepeats(repeats);
                    }
                });

        savebutton   = contentView.findViewById(R.id.savetimeprofilebutton);
        deletebutton = contentView.findViewById(R.id.deletetimeprofilebutton);

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //saveTimeSettings();
                String savedstring = getString(R.string.timeprofilesavedstring,addcustomtextentry.getText());
                Toast infotoast = Toast.makeText(getContext(), savedstring, Toast.LENGTH_SHORT);
                infotoast.setGravity(Gravity.CENTER, 0, 0);
                infotoast.show();
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        String selected = parent.getItemAtPosition(pos).toString();
        List<Integer> selected_data = map.get(selected);
        System.out.println(selected_data);
        int changeTimeSettings = selected_data.get(0);

        if (changeTimeSettings == 0) {
            disableCustomTimeSetting();
            setConstantSeekBarValuesAndDisable(selected_data.get(1), selected_data.get(2),
                    selected_data.get(3), selected_data.get(4));
        }
        else if (changeTimeSettings == 1) enableCustomTimeSetting();
        setHomeFragmentTimeView(this.focustime_set);
    }

    private void disableCustomTimeSetting(){
        addcustomtextentry.setVisibility(View.INVISIBLE);
        addcustomtextentry.setEnabled(false);
        disableSaveAndDeleteButtons();
    }

    private void enableCustomTimeSetting(){
        addcustomtextentry.setVisibility(View.VISIBLE);
        addcustomtextentry.setEnabled(true);
        enableAllSeekbars();
        enableSaveAndDeleteButtons();
        addcustomtextentry.requestFocus();
    }


    private void enableAllSeekbars(){
        focustimeseekbar.setEnabled(true);
        shortbreaktimeseekbar.setEnabled(true);
        longbreaktimeseekbar.setEnabled(true);
        repeatseekbar.setEnabled(true);
    }

    private void disableSaveAndDeleteButtons(){
        savebutton.setEnabled(false);
        savebutton.setTextColor(Color.GRAY);
        deletebutton.setEnabled(false);
        deletebutton.setTextColor(Color.GRAY);
    }

    private void enableSaveAndDeleteButtons(){
        savebutton.setEnabled(true);
        savebutton.setTextColor(getResources().getColor(R.color.colorAccent,null));
        deletebutton.setEnabled(true);
        deletebutton.setTextColor(Color.BLACK);
        //TODO : somehow pass these colours also, so that they can be changed later if needed
    }

    private void setConstantSeekBarValuesAndDisable(int focustime,int shortbreaktime,int longbreaktime,int repeats) {
        try {
            this.focustime_set = focustime;
            focustimeseekbar.setProgress(focustime);
            focustimeview.setText(getString(R.string.focustimeviewstring, focustimeseekbar.getProgress()));
            focustimeseekbar.setEnabled(false);

            this.shortbreaktime_set = shortbreaktime;
            shortbreaktimeseekbar.setProgress(shortbreaktime);
            shortbreaktimeview.setText(getString(R.string.shortbreaktimeviewstring, shortbreaktimeseekbar.getProgress()));
            shortbreaktimeseekbar.setEnabled(false);

            this.longbreaktime_set = longbreaktime;
            longbreaktimeseekbar.setProgress(longbreaktime);
            longbreaktimeview.setText(getString(R.string.longbreaktimeviewstring, longbreaktimeseekbar.getProgress()));
            longbreaktimeseekbar.setEnabled(false);

            this.repeats_set = repeats;
            repeatseekbar.setProgress(repeats-1);
            repeatsview.setText(getString(R.string.repeatsviewstring, repeatseekbar.getProgress()+1));
            repeatseekbar.setEnabled(false);
        }
        catch (NullPointerException e) {return;}
    }

    private void setFocusTime(int focustime){
        this.focustime_set = focustime;
        System.out.println("focustime_set = "+this.focustime_set);
    }

    private void setShortBreakTime(int shortbreaktime){
        this.shortbreaktime_set = shortbreaktime;
        System.out.println("shortbreaktime_set = "+this.shortbreaktime_set);
    }

    private void setLongBreakTime(int longbreaktime){
        this.longbreaktime_set = longbreaktime;
        System.out.println("longbreaktime_set = "+this.longbreaktime_set);
    }

    private void setRepeats(int repeats){
        this.repeats_set = repeats;
        System.out.println("repeats_set = "+this.repeats_set);
    }

    public int getFocusTime(){return this.focustime_set;}
    public int getShortBreakTime(){return this.shortbreaktime_set;}
    public int getLongBreakTime(){return this.longbreaktime_set;}
    public int getRepeats(){return this.repeats_set;}

    private void setHomeFragmentTimeView(int data){
        MainActivity host = (MainActivity)this.getHost();
        host.updateTimeViewInHomeFragment(data);
    }

    public void onNothingSelected(AdapterView<?> parent) {}
}