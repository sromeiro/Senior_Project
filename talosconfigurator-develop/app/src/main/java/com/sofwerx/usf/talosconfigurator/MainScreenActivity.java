package com.sofwerx.usf.talosconfigurator;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.*;

public class MainScreenActivity extends AppCompatActivity {

    private ModesAdapter modesAdapter;
    private static ViewPager mViewPager;
    private TabLayout tabLayout;
    private Button deleteTabBtn, renameModeBtn;
    private TALOSConfiguration currentConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        parseDefaultConfig();
      AlertDialog alertDialog = new AlertDialog.Builder(this).create();
      alertDialog.setTitle("Tutorial!");
      alertDialog.setMessage("In order to drag the items on the left onto the configurator: simply press and hold and wait for the square before moving them.");
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
      alertDialog.show();


        //setup spinner for HUD choice











        // Create the adapter that will return a fragment for each of the four
        // primary sections of the activity.
        modesAdapter = new ModesAdapter(getSupportFragmentManager(), currentConfiguration);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(modesAdapter);
      for (int i = 0; i < 4; i++){
        modesAdapter.addMode();
        mViewPager.setCurrentItem(modesAdapter.getCount());
        currentConfiguration.createNewMode();

      }
        // Disables swipe paging
        // https://stackoverflow.com/questions/9650265/how-do-disable-paging-by-swiping-with-finger-in-viewpager-but-still-be-able-to-s/13392198#13392198
        mViewPager.beginFakeDrag();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager, true);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Hide the delete mode button and rename mode button when on the File Screen
                if (tab.getPosition() == 0) {
                    deleteTabBtn.setVisibility(View.INVISIBLE);
                    renameModeBtn.setVisibility(View.INVISIBLE);
                   // buttonsConfig.setVisibility(View.INVISIBLE);

                } else {
                    deleteTabBtn.setVisibility(View.VISIBLE);
                    renameModeBtn.setVisibility(View.VISIBLE);
                   // buttonsConfig.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // New Tab Button
        Button newTabBtn = (Button) findViewById(R.id.btnNewTab);
        newTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modesAdapter.addMode();
                // Goto the new tab
                mViewPager.setCurrentItem(modesAdapter.getCount());
                currentConfiguration.createNewMode();
            }
        });

        // Delete Tab Button
        deleteTabBtn = (Button) findViewById(R.id.btnDelTab);
        deleteTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prompt the user if they are sure they want to delete the mode
                PromptDelete del = PromptDelete.newInstance("Delete Mode?");
                FragmentManager fm = getSupportFragmentManager();
                del.show(fm, "DEL");
            }
        });

        // Rename Tab Button
        renameModeBtn = (Button) findViewById(R.id.btnRename);
        renameModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Popup a dialog for the user to type in a new name
                PromptRename rename = PromptRename.newInstance("Rename Mode");
                FragmentManager fm = getSupportFragmentManager();
                rename.show(fm, "Rename");
                // Set the tab name
            }
        });

        // Start on the first mode tab
        mViewPager.setCurrentItem(1);
    }

    public void loadFile(FileInputStream fileIn) {
        parseXML(fileIn);
        modesAdapter.restart(currentConfiguration, getSupportFragmentManager());
        //tabLayout.setupWithViewPager(mViewPager, true);
    }

    public boolean doesFileExist(String fn) {
        File f = new File(Environment.getExternalStorageDirectory(), "talos");
        if (!f.exists()) {
            if (!f.mkdirs()) {
                // TODO: Error to user
                Log.e("File", "Failed to make directory");
            }
        }

        if (!f.canWrite()) {
            // TODO: Error to user
            Log.e("File", "Cannot write to app directory /talos");
        }

        if (!f.canRead()) {
            // TODO: Error to user
            Log.e("File", "Cannot read app directory /talos");
        }

        File checkFile = new File(f, fn);
        return checkFile.exists();
    }

    public void parseDefaultConfig() {
        if (doesFileExist("default.xml")) {
            // Load default.xml
            File f = new File(Environment.getExternalStorageDirectory(), "talos");
            File defaultFile = new File(f, "default.xml");
            try {
                FileInputStream fins = new FileInputStream(defaultFile);
                parseXML(fins);
                return;
            } catch (FileNotFoundException e) {
                // Error and allow function to contiune below and load the embedded default.xml
                Log.e("parseDefaultConfig", "Loading default.xml file from disk. " + e.getMessage());
                makeShortToast("Error loading default.xml from disk");
                e.printStackTrace();
            }
        }

        // Load one built-into app and save as default.xml
        parseXML(getResources().openRawResource(R.raw.defaultconfig));

        File f = new File(Environment.getExternalStorageDirectory(), "talos");
        File exportFile = new File(f, "default.xml");

        try {
            exportFile.createNewFile();
        } catch (IOException e) {
            Log.e("parseDefaultConfig", "Failed to create default.xml to disk. " + e.getMessage());
            makeShortToast("Failed to save default.xml to disk");
            e.printStackTrace();
            MainScreenActivity.this.finish();
        }

        Serializer ser = new Persister();
        try {
            ser.write(currentConfiguration, exportFile);
        } catch (Exception e) {
            Log.e("parseDefaultConfig", "Failed to write default.xml to disk from raw/default.xml. " + e.getMessage());
            exportFile.delete();
            e.printStackTrace();
            MainScreenActivity.this.finish();
        }

        return;
    }

    public void parseXML(InputStream ins) {
        Serializer serializer = new Persister();
        try {
            currentConfiguration = serializer.read(TALOSConfiguration.class, ins);
        } catch (Exception e) {
            e.printStackTrace();
            MainScreenActivity.this.finish();
        }
    }

    public TALOSConfiguration getCurrentConfiguration() {
        return currentConfiguration;
    }

    public TALOSConfiguration resetConfiguration() throws CloneNotSupportedException {
        parseDefaultConfig();
        return currentConfiguration;
    }


    public void deleteTab(int pos) {
        pos = pos - 1;
        currentConfiguration.removeModeByIdx(pos);
        modesAdapter.removeMode(pos);
    }

    public void renameTab(int pos, CharSequence name) {
        pos = pos - 1;
        modesAdapter.renameMode(name, pos);
        currentConfiguration.renameModeByIndex(pos, name.toString());
    }


    public static class PromptDelete extends DialogFragment {
        public static PromptDelete newInstance(String title) {
            PromptDelete frag = new PromptDelete();
            Bundle args = new Bundle();
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
        }

        // DELETE Mode dialog prompt
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
            .setMessage("Delete this Mode?")
            .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Call the calling Activity's deleteTab function
                    MainScreenActivity main = (MainScreenActivity) getActivity();
                    main.deleteTab(main.tabLayout.getSelectedTabPosition());
                }
            })
            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            // Create and return the Dialog
            return builder.create();
        }
    }


    public static class PromptRename extends DialogFragment {
        public static PromptRename newInstance(String title) {
            PromptRename frag = new PromptRename();
            Bundle args = new Bundle();
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
        }

        // Rename Mode dialog prompt
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            final EditText newName = new EditText(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setMessage("Enter a new name for this mode: ")
                    .setView(newName)
                    .setPositiveButton("RENAME", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Call the calling Activity's deleteTab function
                            MainScreenActivity main = (MainScreenActivity) getActivity();
                            CharSequence name = newName.getText();
                            int tabNumber = main.tabLayout.getSelectedTabPosition();
                            if( tabNumber > 0){
                                main.renameTab(tabNumber, name);
                            }
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

            // Create and return the Dialog
            return builder.create();
        }
    }

    public static class PromptNewFile extends DialogFragment {
        public static PromptNewFile newInstance(String title) {
            PromptNewFile frag = new PromptNewFile();
            Bundle args = new Bundle();
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
        }

        // PromptNewFile Mode dialog prompt
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                .setMessage("Start a new file?\nThis will erase all configuration.")
                .setPositiveButton("NEW FILE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Call the calling Activity's deleteTab function
                        MainScreenActivity main = (MainScreenActivity) getActivity();
                        main.parseDefaultConfig();
                        main.newFile();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
            // Create and return the Dialog
            return builder.create();
        }
    }

    // Deletes all Mode tabs
    public void deleteAllModes() {
        modesAdapter.restart(currentConfiguration);
    }

    // Creates defaults modes 1 -> 3
 //   public void createDefaultModes() {
 //       for (int i = 1; i <= 3; i++) {
  //          mModes.add(i + "");
  //      }
  //      mSectionsPagerAdapter.notifyDataSetChanged();

        // Reset config to default by reparsing
 //       parseDefaultConfig();
  //  }

    // Creates a new file and transitions to the first Mode tab
    public void newFile() {
        makeShortToast("New File Created\nReset to Default");
        deleteAllModes();
 //       createDefaultModes();
        mViewPager.setCurrentItem(1);
    }

    // Prompt the user if they are sure they want to create a new file
    public void promptNewFile() {
        PromptNewFile newFile = PromptNewFile.newInstance("Rename Mode");
        FragmentManager fm = getSupportFragmentManager();
        newFile.show(fm, "New File?");
    }

    public int getCurrentTabIndex() {
        return tabLayout.getSelectedTabPosition();
    }

    // The toaster!
    public void makeShortToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void makeLongToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public ModesAdapter getModesAdapter() {
        return modesAdapter;
    }

/*
    //Testing the creation of a new button - NOT WORKING
    Button testButton = (Button)findViewById(R.id.load_profile_button1);
    testButton.setOnClickListener(new MainScreenActivity(){
        public void onClick(View v){
            Intent i = new Intent(this, ActivityStore.class);
            startActivity(i);
        }
    });

    //Class made from tutorial on how to make buttons call other XML layouts - NOT WORKING
    public class ActivityStore extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_profile_selection);
        }
    }
*/

}




