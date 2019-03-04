package com.sofwerx.usf.talosconfigurator;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Junior on 11/29/2017.
 */

public class HudOnTouchListener implements View.OnTouchListener {

    private HudFragment mFragment;

    public HudOnTouchListener(HudFragment hudFragment) {
        mFragment = hudFragment;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        Grid hud = (Grid)v;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                Log.d("bruh", "tapping down");
                mFragment.removeElementFromHud(hud, event);
                break;
        }
        return false;
    }
}
