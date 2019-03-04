package com.sofwerx.usf.talosconfigurator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class new_load_profile extends AppCompatActivity {
  private static final int RECORD_REQUEST_CODE = 101;
  private static final String TAG = "PERM";
  // Create user objects to work with in other layouts. Expected use is each button will have it's own object
    public User setUser1 = new User();
    public User setUser2 = new User();
    public User setUser3 = new User();
    public User setUser4 = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_load_profile);
      int writePermission = ContextCompat.checkSelfPermission(this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE);
      int readPermission = ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_EXTERNAL_STORAGE);

      if (writePermission != PackageManager.PERMISSION_GRANTED) {
        Log.i("Permission", "Permission to write external denied");
        makeRequest();

      }

      //Clicking NEW PROFILE button advances to new_profile_options layout
        Button advanceToNewProfileOptions = (Button) findViewById(R.id.new_profile_button1);
        advanceToNewProfileOptions.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent profile_options_intent = new Intent(new_load_profile.this, New_Profile_Options.class);
                  startActivity(profile_options_intent);
              }
        });

        //Clicking LOAD PROFILE button advances to user_profile_selection layout
        Button advanceToUserProfileSelection = (Button) findViewById(R.id.load_profile_button1);
        advanceToUserProfileSelection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent load_profile_intent = new Intent(new_load_profile.this, user_profile_selection.class);

                //Push USER2 as name for new button since it was not created in New Profile
                load_profile_intent.putExtra("name", "User2");
                startActivity(load_profile_intent);
            }
        });
    }

  protected void makeRequest() {
    ActivityCompat.requestPermissions(this,
      new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
      RECORD_REQUEST_CODE);
  }
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case RECORD_REQUEST_CODE: {

        if (grantResults.length == 0
          || grantResults[0] !=
          PackageManager.PERMISSION_GRANTED) {

          Log.i(TAG, "Permission has been denied by user");
        } else {
          Log.i(TAG, "Permission has been granted by user");
        }
        return;
      }
    }
  }
}
