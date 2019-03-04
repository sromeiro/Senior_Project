package com.sofwerx.usf.talosconfigurator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class user_profile_selection extends AppCompatActivity {

    //Create new_load_profile object to get access to User object there for shared USER info b/w layouts
    public new_load_profile new_load_profile = new new_load_profile();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_user_profile_selection);

      // Create button objects to work with later
      final Button user1 = (Button) findViewById(R.id.user_button1);
      final Button user2 = (Button) findViewById(R.id.user_button2);
      Button user3 = (Button) findViewById(R.id.user_button3);
      Button user4 = (Button) findViewById(R.id.user_button4);

      // Create imageButton objects to work with later
      ImageButton editUser1 = (ImageButton) findViewById(R.id.edit_user_button1);
      ImageButton editUser2 = (ImageButton) findViewById(R.id.edit_user_button2);
      ImageButton editUser3 = (ImageButton) findViewById(R.id.edit_user_button3);
      ImageButton editUser4 = (ImageButton) findViewById(R.id.edit_user_button4);

      // Set button visibility
      user2.setVisibility(View.INVISIBLE);
      user3.setVisibility(View.INVISIBLE);
      user4.setVisibility(View.INVISIBLE);
      editUser2.setVisibility(View.INVISIBLE);
      editUser3.setVisibility(View.INVISIBLE);
      editUser4.setVisibility(View.INVISIBLE);


      if (getIntent().getExtras().getString("name").matches(""))
      {
          user2.setText("User2");
      }
      else
      {
          //Set the text for button USER2 to the new name
          user2.setText(getIntent().getExtras().getString("name"));
          //Save to the information gathered to the User class
          new_load_profile.setUser2.userName = user2.getText().toString();
          new_load_profile.setUser2.userTime = getIntent().getExtras().getString("userTime");
          new_load_profile.setUser2.userLoc = getIntent().getExtras().getString("userLoc");
          new_load_profile.setUser2.userHead = getIntent().getExtras().getString("userHead");
          new_load_profile.setUser2.userDist = getIntent().getExtras().getString("userDist");
      }

      // Make visible if button created by user
      if (!user2.getText().toString().equals("User2"))
      {
          user2.setVisibility(View.VISIBLE);
          editUser2.setVisibility(View.VISIBLE);
      }
      if (!user3.getText().toString().equals("User3"))
      {
          user3.setVisibility(View.VISIBLE);
          editUser3.setVisibility(View.VISIBLE);
      }
      if (!user4.getText().toString().equals("User4"))
      {
          user4.setVisibility(View.VISIBLE);
          editUser4.setVisibility(View.VISIBLE);
      }


      //Clicking USER1 PROFILE button advances to fragment_hud layout
      user1.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view){
          Intent load_fragment_hud = new Intent(user_profile_selection.this, MainScreenActivity.class);
          startActivity(load_fragment_hud);
        }
      });

      //Clicking EDIT button advances to New_Profile_Options layout
      editUser1.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent profile_options_intent = new Intent(user_profile_selection.this, New_Profile_Options.class);

              // Pass this information back to the New_Profile_Options layout to set the options NOT WORKING for User1
              profile_options_intent.putExtra("userName", user1.getText().toString());
              profile_options_intent.putExtra("userDist", new_load_profile.setUser1.userDist);
              profile_options_intent.putExtra("userLoc", new_load_profile.setUser1.userLoc);
              profile_options_intent.putExtra("userHead", new_load_profile.setUser1.userHead);
              profile_options_intent.putExtra("userTime", new_load_profile.setUser1.userTime);

              startActivity(profile_options_intent);
          }
      });

      //Clicking USER2 PROFILE button advances to fragment_hud layout
      user2.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view){
              Intent load_fragment_hud = new Intent(user_profile_selection.this, MainScreenActivity.class);
              startActivity(load_fragment_hud);
          }
      });

      //Clicking EDIT button advances to New_Profile_Options layout
      editUser2.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent profile_options_intent = new Intent(user_profile_selection.this, New_Profile_Options.class);

              // Pass this information back to the New_Profile_Options layout to set the options
              profile_options_intent.putExtra("userName", user2.getText().toString());
              profile_options_intent.putExtra("userDist", new_load_profile.setUser2.userDist);
              profile_options_intent.putExtra("userLoc", new_load_profile.setUser2.userLoc);
              profile_options_intent.putExtra("userHead", new_load_profile.setUser2.userHead);
              profile_options_intent.putExtra("userTime", new_load_profile.setUser2.userTime);

              startActivity(profile_options_intent);
          }
      });

      //Clicking USER3 PROFILE button advances to fragment_hud layout
      user3.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view){
              Intent load_fragment_hud = new Intent(user_profile_selection.this, MainScreenActivity.class);
              startActivity(load_fragment_hud);
          }
      });

      //Clicking EDIT button advances to New_Profile_Options layout
      editUser3.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent profile_options_intent = new Intent(user_profile_selection.this, New_Profile_Options.class);
              startActivity(profile_options_intent);
          }
      });

      //Clicking USER4 PROFILE button advances to fragment_hud layout
      user4.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view){
              Intent load_fragment_hud = new Intent(user_profile_selection.this, MainScreenActivity.class);
              startActivity(load_fragment_hud);
          }
      });

      //Clicking EDIT button advances to New_Profile_Options layout
      editUser4.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent profile_options_intent = new Intent(user_profile_selection.this, New_Profile_Options.class);
              startActivity(profile_options_intent);
          }
      });

      //Clicking BACK button will take you back to the main screen
      ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
      backButton.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view) {
              Intent load_profile_intent = new Intent(user_profile_selection.this, new_load_profile.class);
              startActivity(load_profile_intent);
          }
      });

  }
}
