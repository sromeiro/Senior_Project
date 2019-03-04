package com.sofwerx.usf.talosconfigurator;

import android.content.Intent;
import android.media.Image;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class New_Profile_Options extends AppCompatActivity {

    public EditText editTextName;
    public TextView textName;
    public Spinner spinDist, spinHead, spinLoc, spinTime;
    public User user1 = new User();
    public user_profile_selection user_profile_selection = new user_profile_selection();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__profile__options);

        editTextName = (EditText) findViewById(R.id.editTextName);
        textName = (TextView) findViewById(R.id.textViewName);
        spinDist = (Spinner) findViewById(R.id.SpinnerDist);
        spinHead = (Spinner) findViewById(R.id.SpinnerHead);
        spinLoc = (Spinner) findViewById(R.id.SpinnerLoc);
        spinTime = (Spinner) findViewById(R.id.SpinnerTime);


        //Get the current user1 editText and store it to pass on to another layout
        user1.userName = editTextName.getText().toString();

        //=============================Spinner Dist==========================================//

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
        R.array.distance_units, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinDist.setAdapter(adapter1);
        spinDist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
          Log.d(TAG, "onItemSelected: " + spinDist.getSelectedItem().toString());
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
          // TODO Auto-generated method stub
        }
        });
        //Logs the information in textName so we can verify it's contents
        Log.d(TAG, "onCreate: " + textName);
        user1.userDist = spinDist.getSelectedItem().toString();

        //=================================Spinner Head=======================================//

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
        R.array.heading_units, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinHead.setAdapter(adapter2);
        spinHead.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
                Log.d(TAG, "onItemSelected: " + spinHead.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        //Logs the information in textName so we can verify it's contents
        Log.d(TAG, "onCreate: " + textName);
        user1.userHead = spinHead.getSelectedItem().toString();

        //==============================Spinner Loc===========================================//

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
        R.array.location_units, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinLoc.setAdapter(adapter3);
        spinLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
                Log.d(TAG, "onItemSelected: " + spinLoc.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        //Logs the information in textName so we can verify it's contents
        Log.d(TAG, "onCreate: " + textName);
        user1.userLoc = spinLoc.getSelectedItem().toString();

        //================================Spinner Time========================================//

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
        R.array.time_units, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinTime.setAdapter(adapter4);
        spinTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
                Log.d(TAG, "onItemSelected: " + spinTime.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        //Logs the information in textName so we can verify it's contents
        Log.d(TAG, "onCreate: " + textName);
        user1.userTime = spinTime.getSelectedItem().toString();

        //==================================================================================//


        try
        {
            //Try and set proper names and spinner values from pre-created profiles
            editTextName.setText(getIntent().getExtras().getString("userName"));

            if(getIntent().getExtras().getString("userDist").matches("Miles"))
            {
                spinDist.setSelection(1);
            }
            if(getIntent().getExtras().getString("userLoc").matches("Latitude / Longitude"))
            {
                spinLoc.setSelection(1);
            }
            if(getIntent().getExtras().getString("userHead").matches("Mils"))
            {
                spinHead.setSelection(1);
            }
            if(getIntent().getExtras().getString("userTime").matches("Zulu"))
            {
                spinTime.setSelection(1);
            }


        }
        catch (Exception e)
        {
            //Do nothing if it fails. Would fail if user was never created
        }






        //Clicking SAVE button advances to user_profile_selection layout
        android.widget.Button advanceToUserProfileSelection = (Button) findViewById(R.id.new_save_options);
        advanceToUserProfileSelection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent load_profile_intent = new Intent(New_Profile_Options.this, user_profile_selection.class);

                //Pass the input text from the user to the new layout user_profile_selection
                load_profile_intent.putExtra("name", editTextName.getText().toString());
                load_profile_intent.putExtra("userDist", spinDist.getSelectedItem().toString());
                load_profile_intent.putExtra("userLoc", spinLoc.getSelectedItem().toString());
                load_profile_intent.putExtra("userHead", spinHead.getSelectedItem().toString());
                load_profile_intent.putExtra("userTime", spinTime.getSelectedItem().toString());

                startActivity(load_profile_intent);
            }
        });

        //Clicking BACK button will take you back to the main screen
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent load_profile_intent = new Intent(New_Profile_Options.this, new_load_profile.class);
                startActivity(load_profile_intent);
            }
        });

    }



}
