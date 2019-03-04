package com.sofwerx.usf.talosconfigurator;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junior on 10/27/2017.
 * This class holds most of the data associated with a mode
 * Except the buttons config data
 */

public class ModeData {
    private CharSequence name;
    private Fragment mfragment;
    private ArrayList<ModeElement> hudState;

    public ModeData(CharSequence nameMode, List<ModeElement> elements) {
        name = nameMode;
        hudState = (ArrayList<ModeElement>) elements;
        mfragment = ModesTabFragment.newInstance(hudState);
    }

    public  void setName(CharSequence newName) {
        name = newName;
    }

    public void clearState() {
        hudState = new ArrayList<>();
    }

    public Fragment getFragment() {
        return mfragment;
    }

    public CharSequence getName() {
        return name;
    }
}
