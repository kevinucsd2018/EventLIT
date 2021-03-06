package com.cse110.eventlit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cse110.eventlit.db.Event;
import com.cse110.eventlit.db.Organization;
import com.cse110.eventlit.db.User;
import com.cse110.utils.EventUtils;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity {

    // Fields for Organizer User to fill in
    private TextInputLayout mTitle;
    private TextInputLayout mLocation;
    private TextInputLayout mDescription;
    private TextInputLayout mTag;
    private TextInputLayout mCapacity;

    // Orgs that the Organizer User manages
    private List<String> orgsManaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: FAB for adding event picture
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        // Gets the Orgs that the Organization User is managing
        User user = UserUtils.getCurrentUser();
        ArrayList<Organization> orgs = UserUtils.getOrgsManaging();
        orgsManaging = new ArrayList<>();
        for (Organization org: orgs) {
            orgsManaging.add(org.getName());
        }

        // Cancel and Create button functionality
        Button cancelBut = (Button) findViewById(R.id.cancelButton);
        Button createBut = (Button) findViewById(R.id.createButton);

        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Go back to organizer feed
                startActivity(new Intent(CreateEventActivity.this, OrganizerFeedActivity.class));
                finish();
            }
        });

        createBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Create event (Add event to database)
                addEventToDB();
            }
        });

        // Populate spinner for selecting org that the event is for
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setPrompt("Organization holding event");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1, orgsManaging);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // Input Fields
        mTitle = (TextInputLayout) findViewById(R.id.title);
        mLocation = (TextInputLayout) findViewById(R.id.locationtext);
        mDescription = (TextInputLayout) findViewById(R.id.descriptiontext);
        mTag = (TextInputLayout) findViewById(R.id.tagtext);
        mCapacity = (TextInputLayout) findViewById(R.id.peopletext);

        // Check for validity of input
        mTitle.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkTitle();
                }
            }
        });
        mLocation.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkLocation();
                }
            }
        });
        mDescription.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkDescription();
                }
            }
        });
        mTag.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkTag();
                }
            }
        });
        mCapacity.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    checkCapacity();
                }
            }
        });
    }

    /* Show the start time picker dialog */
    public void onStartTimeClicked(View v){
        DialogFragment newFragment = new TimePickerFragment();
        TextView startTimeText = (TextView) findViewById(R.id.starttimetext);
        startTimeText.setText("Start Time: ");

        // Pass in start time textview
        Bundle args = new Bundle();
        args.putInt("timetext", R.id.starttimetext);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"TimePicker");
    }

    /* Show the start date picker dialog */
    public void onStartDateClicked(View v){
        DialogFragment newFragment = new DatePickerFragment();
        TextView startDateText = (TextView) findViewById(R.id.startdatetext);
        startDateText.setText("Start Date: ");

        // Pass in start time textview
        Bundle args = new Bundle();
        args.putInt("datetext", R.id.startdatetext);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"DatePicker");
    }

    /* Show the end time picker dialog */
    public void onEndTimeClicked(View v){
        DialogFragment newFragment = new TimePickerFragment();
        TextView endTimeText = (TextView) findViewById(R.id.endtimetext);
        endTimeText.setText("End Time: ");

        // Pass in the end time textview
        Bundle args = new Bundle();
        args.putInt("timetext", R.id.endtimetext);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"TimePicker");
    }

    /* Show the end date picker dialog */
    public void onEndDateClicked(View v){
        DialogFragment newFragment = new DatePickerFragment();
        TextView startDateText = (TextView) findViewById(R.id.enddatetext);
        startDateText.setText("End Date: ");

        // Pass in start time textview
        Bundle args = new Bundle();
        args.putInt("datetext", R.id.enddatetext);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(),"DatePicker");
    }

    @Override
    public void onBackPressed() {
        // TODO: Go back to organizer feed
        finish();

    }

    /* Gets the text fields to make event in database */
    private void addEventToDB() {
        // TODO Get inputs and make event

        String title = mTitle.getEditText().getText().toString();
        String location = mLocation.getEditText().getText().toString();
        String description = mDescription.getEditText().getText().toString();
        String capacity = mCapacity.getEditText().getText().toString();

        Event event = new Event(title, description, "275", "0", location, "Uncateogorized", Integer.parseInt(capacity));
        EventUtils.createEvent(event, new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.w("Added event with id", task.getResult());
            }
        });


    }

    /* Check validity of title */
    protected boolean checkTitle() {
        EditText titleEditText = mTitle.getEditText();

        if (titleEditText != null && titleEditText.getError() != null) return false;

        String titleText = null;
        if (titleEditText != null) {
            titleText = titleEditText.getText().toString();
        }

        // Title Criteria
        if (titleText != null && titleText.length() < 1) {
            titleEditText.setError("Title must contain at least 2 characters");
        }

        return true;
    }

    // TODO write validity checks
    protected boolean checkLocation() {
        return true;
    }
    protected boolean checkDescription() {
        return true;
    }
    protected boolean checkCapacity() {
        return true;
    }
    protected boolean checkTag() {
        return true;
    }

}
