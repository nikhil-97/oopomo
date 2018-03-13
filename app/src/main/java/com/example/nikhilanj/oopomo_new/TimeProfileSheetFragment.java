package com.example.nikhilanj.oopomo_new;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
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

import com.example.nikhilanj.oopomo_new.db.PomoDatabase;
import com.example.nikhilanj.oopomo_new.db.dao.PomoProfileDao;
import com.example.nikhilanj.oopomo_new.db.entity.PomoProfile;
import com.example.nikhilanj.oopomo_new.utils.PomoProfileManager;

public class TimeProfileSheetFragment extends BottomSheetDialogFragment
        implements AdapterView.OnItemSelectedListener,getSetTimesInterface {

    final private PomoDatabase pomoDatabase = PomoDatabase.getPomoDatabaseInstance(getContext());

    private PomoProfileManager pomoProfileManager = new PomoProfileManager(pomoDatabase);

    private PomoProfile selectedProfile;

    private EditText profileNameEditText;

    private SeekBar focusTimeSeekBar;
    private SeekBar shortBreakTimeSeekBar;
    private SeekBar longBreakTimeSeekBar;
    private SeekBar repeatsSeekBar;

    private TextView focusTimeTextView;
    private TextView shortBreakTimeTextView;
    private TextView longBreakTimeTextView;
    private TextView repeatsTextView;

    private Button saveButton;
    private Button deleteButton;

    private int focusTime;
    private int shortBreakTime;
    private int longBreakTime;
    private int repeats;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_layout, null);
        dialog.setContentView(contentView);

        Spinner timeProfileSpinner = contentView.findViewById(R.id.time_profiles_spinner);

        final List<PomoProfile> pomoProfileNames = this.pomoProfileManager.getAllPomoProfiles();

        ArrayAdapter<PomoProfile> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                pomoProfileNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeProfileSpinner.setAdapter(adapter);
        timeProfileSpinner.setOnItemSelectedListener(this);

        profileNameEditText = contentView.findViewById(R.id.addcustomtext);
        profileNameEditText.setHint("profile name");

        focusTimeSeekBar = contentView.findViewById(R.id.focustimeseekbar);
        focusTimeTextView = contentView.findViewById(R.id.focustimeseekbarvalue);
        focusTimeSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int focustime;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        focustime = progresValue;
                        focusTimeTextView.setText(getString(R.string.focustimeviewstring,focustime));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        selectedProfile.setFocusTime(focustime);
                        setFocusTime(focustime);
                        setHomeFragmentTimeView(this.focustime);
                    }
                });

        shortBreakTimeSeekBar = contentView.findViewById(R.id.shortbreaktimeseekbar);
        shortBreakTimeTextView = contentView.findViewById(R.id.shortbreaktimeseekbarvalue);
        shortBreakTimeSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int shortbreak;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        shortbreak = progresValue;
                        shortBreakTimeTextView.setText(getString(R.string.shortbreaktimeviewstring,shortbreak));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        selectedProfile.setShortBreakTime(shortbreak);
                        setShortBreakTime(shortbreak);
                    }
                });

        longBreakTimeSeekBar = contentView.findViewById(R.id.longbreaktimeseekbar);
        longBreakTimeTextView = contentView.findViewById(R.id.longbreaktimeseekbarvalue);
        longBreakTimeSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int longbreak;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        longbreak = progresValue;
                        longBreakTimeTextView.setText(getString(R.string.longbreaktimeviewstring,longbreak));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        selectedProfile.setLongBreakTime(longbreak);
                        setLongBreakTime(longbreak);
                    }
                });

        repeatsSeekBar = contentView.findViewById(R.id.intervalseekbar);
        repeatsTextView = contentView.findViewById(R.id.intervalseekbarvalue);
        repeatsSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int repeats;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        repeats = progresValue + 1;
                        repeatsTextView.setText(getString(R.string.repeatsviewstring, repeats));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        setRepeats(repeats);
                        selectedProfile.setRepeats(repeats);
                    }
                });


        saveButton = contentView.findViewById(R.id.savetimeprofilebutton);
        deleteButton = contentView.findViewById(R.id.deletetimeprofilebutton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PomoProfile newPomoProfile = new PomoProfile(selectedProfile);
                String profileName = profileNameEditText.getText().toString().trim();

                if( !selectedProfile.getProfileName().equals("Custom") ) {
                    pomoProfileManager.updatePomoProfile(selectedProfile);
                    Toast infoToast = Toast.makeText(getContext(), "Profile changes done successfully", Toast.LENGTH_SHORT);
                    infoToast.show();
                    return;
                }

                if( profileName.isEmpty() ) {
                    Toast infoToast = Toast.makeText(getContext(), "Profile name can't be empty", Toast.LENGTH_SHORT);
                    infoToast.show();
                    return;
                }

                if ( pomoProfileManager.getProfileByName(profileName) != null) {
                    Toast infoToast = Toast.makeText(getContext(), "Profile already exists", Toast.LENGTH_SHORT);
                    infoToast.show();
                    return;
                }

                newPomoProfile.setProfileName(profileName);
                newPomoProfile.setEditingAllowed(true);
                pomoProfileManager.insertPomoProfile(newPomoProfile);

                String savedString = getString(R.string.timeprofilesavedstring, profileName);
                Toast infoToast = Toast.makeText(getContext(), savedString, Toast.LENGTH_SHORT);
                infoToast.setGravity(Gravity.CENTER, 0, 0);
                infoToast.show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pomoDatabase.pomoProfileDao().deletePomoProfile(selectedProfile);
                Toast infoToast = Toast.makeText(getContext(), "Profile deleted successfully", Toast.LENGTH_SHORT);
                infoToast.show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        this.selectedProfile = (PomoProfile) parent.getItemAtPosition(pos);
        //Set Seekbars progress
        setAllSeekbarsProgress(this.selectedProfile);
        //set TimeTextViews values
        setAllTimeTextViews(this.selectedProfile);

        if(selectedProfile.isEditingAllowed()) {
            enableCustomTimeSetting();
        } else {
            disableCustomTimeSetting();
        }

        setHomeFragmentTimeView(this.focusTime);
    }


    private void setAllSeekbarsProgress(PomoProfile selectedProfile)
    {
        this.focusTimeSeekBar.setProgress(selectedProfile.getFocusTime());
        this.shortBreakTimeSeekBar.setProgress(selectedProfile.getShortBreakTime());
        this.longBreakTimeSeekBar.setProgress(selectedProfile.getLongBreakTime());
        this.repeatsSeekBar.setProgress(selectedProfile.getRepeats() - 1);
    }

    private void setAllTimeTextViews(PomoProfile selectedProfile)
    {
        this.focusTimeTextView.setText(
                getString(R.string.focustimeviewstring, selectedProfile.getFocusTime())
        );
        this.shortBreakTimeTextView.setText(
                getString(R.string.shortbreaktimeviewstring, selectedProfile.getShortBreakTime())
        );
        this.longBreakTimeTextView.setText(
                getString(R.string.longbreaktimeviewstring, selectedProfile.getLongBreakTime())
        );
        this.repeatsTextView.setText(
                getString(R.string.repeatsviewstring, selectedProfile.getRepeats())
        );
    }

    private void disableCustomTimeSetting(){
        profileNameEditText.setVisibility(View.INVISIBLE);
        profileNameEditText.setEnabled(false);
        disableSaveAndDeleteButtons();
        disableAllSeekbars();
    }

    private void enableCustomTimeSetting(){
        if( selectedProfile.getProfileName().equals("Custom") ) {
            profileNameEditText.setVisibility(View.VISIBLE);
            profileNameEditText.setEnabled(true);
        } else {
            profileNameEditText.setVisibility(View.INVISIBLE);
            profileNameEditText.setEnabled(false);
        }
        enableAllSeekbars();
        enableSaveAndDeleteButtons();
        profileNameEditText.requestFocus();
    }


    private void enableAllSeekbars(){
        focusTimeSeekBar.setEnabled(true);
        shortBreakTimeSeekBar.setEnabled(true);
        longBreakTimeSeekBar.setEnabled(true);
        repeatsSeekBar.setEnabled(true);
    }

    private void disableAllSeekbars() {
        focusTimeSeekBar.setEnabled(false);
        shortBreakTimeSeekBar.setEnabled(false);
        longBreakTimeSeekBar.setEnabled(false);
        repeatsSeekBar.setEnabled(false);
    }

    private void disableSaveAndDeleteButtons(){
        saveButton.setEnabled(false);
        saveButton.setTextColor(Color.GRAY);
        deleteButton.setEnabled(false);
        deleteButton.setTextColor(Color.GRAY);
    }

    private void enableSaveAndDeleteButtons(){
        saveButton.setEnabled(true);
        saveButton.setTextColor(getResources().getColor(R.color.colorAccent,null));
        deleteButton.setEnabled(true);
        deleteButton.setTextColor(Color.BLACK);
        //TODO : somehow pass these colours also, so that they can be changed later if needed
    }

    private void setFocusTime(int focustime){
        this.focusTime = focustime;
        System.out.println("focusTime = "+this.focusTime);
    }

    private void setShortBreakTime(int shortbreaktime){
        this.shortBreakTime = shortbreaktime;
        System.out.println("shortBreakTime = "+this.shortBreakTime);
    }

    private void setLongBreakTime(int longbreaktime){
        this.longBreakTime = longbreaktime;
        System.out.println("longBreakTime = "+this.longBreakTime);
    }

    private void setRepeats(int repeats){
        this.repeats = repeats;
        System.out.println("repeats = "+this.repeats);
    }

    public int getFocusTime(){return this.focusTime;}
    public int getShortBreakTime(){return this.shortBreakTime;}
    public int getLongBreakTime(){return this.longBreakTime;}
    public int getRepeats(){return this.repeats;}

    private void setHomeFragmentTimeView(int data){
        MainActivity host = (MainActivity)this.getHost();
        host.updateTimeViewInHomeFragment(data);
    }

    public void onNothingSelected(AdapterView<?> parent) {}
}