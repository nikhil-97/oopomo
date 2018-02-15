package com.example.nikhilanj.oopomo_new;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class TimeProfileSheetFragment extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener {

    private View contentView;
    private Spinner timeprofile_spinner;
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

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.bottom_sheet_layout, null);
        dialog.setContentView(contentView);

        timeprofile_spinner = contentView.findViewById(R.id.time_profiles_spinner);
        ArrayAdapter<CharSequence> adapter = null;
        try{
            adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.time_profiles_array, android.R.layout.simple_spinner_item);}
        catch(NullPointerException e){System.out.println("Missing owner activity");return;}
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeprofile_spinner.setAdapter(adapter);
        timeprofile_spinner.setOnItemSelectedListener(this);

        addcustomtextentry = contentView.findViewById(R.id.addcustomtext);

        focustimeseekbar = contentView.findViewById(R.id.focustimeseekbar);
        focustimeview    = contentView.findViewById(R.id.focustimeseekbarvalue);
        focustimeseekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        focustimeview.setText(Integer.toString(progresValue));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        shortbreaktimeseekbar = contentView.findViewById(R.id.shortbreaktimeseekbar);
        shortbreaktimeview    = contentView.findViewById(R.id.shortbreaktimeseekbarvalue);
        shortbreaktimeseekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        shortbreaktimeview.setText(Integer.toString(progresValue));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        longbreaktimeseekbar = contentView.findViewById(R.id.longbreaktimeseekbar);
        longbreaktimeview    = contentView.findViewById(R.id.longbreaktimeseekbarvalue);
        longbreaktimeseekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        longbreaktimeview.setText(Integer.toString(progresValue));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        repeatseekbar = contentView.findViewById(R.id.intervalseekbar);
        repeatsview   = contentView.findViewById(R.id.intervalseekbarvalue);
        repeatseekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        repeatsview.setText(Integer.toString(progresValue+1)+" times");
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        savebutton   = contentView.findViewById(R.id.savetimeprofilebutton);
        deletebutton = contentView.findViewById(R.id.deletetimeprofilebutton);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        String selected = parent.getItemAtPosition(pos).toString();

        if(selected.equals(getResources().getString(R.string.classictime))){
            System.out.println("classic");
            setConstantSeekBarValues(25,5,15,4);
            addcustomtextentry.setEnabled(false);
            disableSeekbars(focustimeseekbar,shortbreaktimeseekbar,longbreaktimeseekbar,repeatseekbar);
            disableButtons(savebutton,deletebutton);

        }
        else if (selected.equals(getResources().getString(R.string.superfocus))){
            setConstantSeekBarValues(60,10,30,6);
            addcustomtextentry.setEnabled(false);
            disableSeekbars(focustimeseekbar,shortbreaktimeseekbar,longbreaktimeseekbar,repeatseekbar);
            disableButtons(savebutton,deletebutton);}

        else if (selected.equals(getResources().getString(R.string.addcustom))){
            addcustomtextentry.setEnabled(true);
            enableSeekbars(focustimeseekbar,shortbreaktimeseekbar,longbreaktimeseekbar,repeatseekbar);
            enableButtons(savebutton,deletebutton);
            addcustomtextentry.requestFocus();
        }
    }

    private void disableSeekbars(SeekBar seekbar1,SeekBar seekbar2,SeekBar seekbar3,SeekBar seekbar4){
        seekbar1.setEnabled(false);
        seekbar2.setEnabled(false);
        seekbar3.setEnabled(false);
        seekbar4.setEnabled(false);
    }

    private void enableSeekbars(SeekBar seekbar1,SeekBar seekbar2,SeekBar seekbar3,SeekBar seekbar4){
        seekbar1.setEnabled(true);
        seekbar2.setEnabled(true);
        seekbar3.setEnabled(true);
        seekbar4.setEnabled(true);
    }

    private void disableButtons(Button button1,Button button2){
        button1.setEnabled(false);
        button1.setTextColor(Color.GRAY);
        button2.setEnabled(false);
        button2.setTextColor(Color.GRAY);
    }

    private void enableButtons(Button button1,Button button2){
        button1.setEnabled(true);
        button1.setTextColor(getResources().getColor(R.color.colorAccent));
        button2.setEnabled(true);
        button2.setTextColor(Color.BLACK);
        //TODO : somehow pass these colours also, so that they can be changed later if needed

    }
    private void setConstantSeekBarValues(int focustime,int shortbreaktime,int longbreaktime,int repeats) {
        try {

            focustimeseekbar.setProgress(focustime);
            focustimeview.setText(Integer.toString(focustimeseekbar.getProgress()));
            shortbreaktimeseekbar.setProgress(shortbreaktime);
            shortbreaktimeview.setText(Integer.toString(shortbreaktimeseekbar.getProgress()));
            longbreaktimeseekbar.setProgress(longbreaktime);
            longbreaktimeview.setText(Integer.toString(longbreaktimeseekbar.getProgress()));
            repeatseekbar.setProgress(repeats-1);
            repeatsview.setText(Integer.toString(repeatseekbar.getProgress()+1)+" times");
        }
        catch (NullPointerException e) {
            return;
        }
    }


    public void onNothingSelected(AdapterView<?> parent) {}
}