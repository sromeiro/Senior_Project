package com.sofwerx.usf.talosconfigurator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junior on 10/27/2017.
 * This adapter loads mode data from the TAlosConfiguration object
 * into the tab layout and viewpager
 */

public class ModesAdapter extends FragmentStatePagerAdapter {

  private static final String TAG = "MODE";
  private List<ModeData> mModes;

    public ModesAdapter(FragmentManager fm, TALOSConfiguration talosConfig) {
        super(fm);
        mModes = new ArrayList<>();
        for(Mode mode: talosConfig.getModes()) {
            mModes.add(new ModeData(
                    mode.getName(),
                    mode.getElements()
            ));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return position == 0 ? FileTabFragment.newInstance(position) : mModes.get(position - 1).getFragment();
    }

    @Override
    public int getCount() {
        return mModes.size() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "FILE" : mModes.get(position - 1).getName();
    }

    @Override
    public int getItemPosition(Object object) {
        /* current solution works but needs better implementation
        since current is not memory and performance efficient
        check https://medium.com/inloop/adventures-with-fragmentstatepageradapter-4f56a643f8e0
        for details
         */
        return POSITION_NONE;
    }

    /*
        This function adds a new mode to the adapter which creates a new tab
     */
    public void addMode() {
        int numMode = mModes.size();
      Log.d(TAG, "addMode: " + numMode);
      switch (numMode){
        case 1:
          mModes.add(new ModeData(
           "Infiltration",
            new ArrayList<ModeElement>()
          ));
          break;
        case 2:
          mModes.add(new ModeData(
            "Mission 1",
            new ArrayList<ModeElement>()
          ));
          break;
        case 3:
          mModes.add(new ModeData(
            "Mission 2",
            new ArrayList<ModeElement>()
          ));
          break;
        case 4:
          mModes.add(new ModeData(
            "Exfiltration",
            new ArrayList<ModeElement>()
          ));
          break;
          default:
            mModes.add(new ModeData(
              Integer.toString(numMode),
              new ArrayList<ModeElement>()
            ));
      }


        this.notifyDataSetChanged();
    }

    /*
        This function removes a new mode from the adapter which removes a tab from the layout
     */
    public void removeMode(int position) {
        mModes.remove(position);
        this.notifyDataSetChanged();
        //add logic to re-index naming of modes
    }

    /*
        This function renames a mode in the tab layouts
     */
    public void renameMode(CharSequence name, int position) {
        mModes.get(position).setName(name);
        this.notifyDataSetChanged();
        ModesTabFragment currentTab = (ModesTabFragment) mModes.get(position).getFragment();
        HudFragment currentHud = currentTab.getHudFragment();
        currentHud.loadGridWhenReady();

    }

    /*
        This functions reloads the entire state of the application fresh from
        the TalosConfig file
     */
    public void restart(TALOSConfiguration talosConfig, FragmentManager fm)  {
        mModes = new ArrayList<ModeData>();
        for(Mode mode: talosConfig.getModes()) {
            mModes.add(new ModeData(
                    mode.getName(),
                    mode.getElements()
            ));
        }

        for(int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        this.notifyDataSetChanged();
    }

    public void clearMode(int index) {
        ModeData mode = mModes.get(index);
        mode.clearState();
        this.notifyDataSetChanged();
    }

    public void restart(TALOSConfiguration talosConfig) {
        for(ModeData mode: mModes) mode.getFragment().getArguments().clear();
        mModes = new ArrayList<ModeData>();
        for(Mode mode: talosConfig.getModes()) {
            mModes.add(new ModeData(
                    mode.getName(),
                    mode.getElements()
            ));
        }
        this.notifyDataSetChanged();
    }
}
