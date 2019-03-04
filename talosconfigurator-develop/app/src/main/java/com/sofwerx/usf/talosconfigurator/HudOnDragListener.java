package com.sofwerx.usf.talosconfigurator;

import android.content.ClipData;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.ContentValues.TAG;


/**
 * Created by Junior on 10/24/2017.
 */

public class HudOnDragListener implements View.OnDragListener {

    private final String COPY_DATA = "copy_data";
    private final String ELEMENT = "element";
    private HudFragment mFragment;
    public HudOnDragListener(HudFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        Grid grid = (Grid) v;
        TextView draggedView = (TextView) event.getLocalState();

        switch(action) {
            case DragEvent.ACTION_DRAG_STARTED:
                //ignore event
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                //ignore event
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                //Log.d("bruh", "I can check for items already in grid");
                Format elementFormat = mFragment.getFormat(draggedView.getText().toString());
                if(!grid.isLocationValid(event)) {
                    v.setBackgroundColor(Color.RED);
                }
                else if(!grid.isLocationValid(event, elementFormat)){
                    v.setBackgroundColor(Color.RED);
                }

                else {
                  Log.d(TAG, "onDrag: " + mFragment.Vision);
                  if (mFragment.Vision == "Nightvision Green") {
                    v.setBackgroundColor(v.getResources().getColor(android.R.color.holo_green_dark));
                  }
                  else
                    v.setBackgroundColor(v.getResources().getColor(android.R.color.white));

                }

                break;

            case DragEvent.ACTION_DRAG_EXITED:
                //ignore event
              if (mFragment.Vision == "Nightvision Green") {
                v.setBackgroundColor(v.getResources().getColor(android.R.color.holo_green_dark));
              }
              else
                v.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                break;

            case DragEvent.ACTION_DROP:
                //get container of all the views in hud fragment

                grid = (Grid) v;
                ViewGroup owner = (ViewGroup) v.getParent();

                Position initPosition = mFragment.getGridManager().addItemToMode(grid.getName(), event, owner, mFragment);

                if(initPosition != null) {
                    String format = draggedView.getText().toString();
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String elementName = item.getIntent().getBundleExtra(COPY_DATA).getString(ELEMENT);
                    mFragment.addItemToMode(elementName, initPosition, format);


                }
                //List<TextView> elementDisplay = grid.addItemToGrid(event);

//                if(elementDisplay != null) {
//                    for(TextView mText: elementDisplay) owner.addView(mText);
//                    ClipData.Item item = event.getClipData().getItemAt(0);
//                    String elementName = item.getIntent().getBundleExtra(COPY_DATA).getString(ELEMENT);
//
//                    Position position = grid.getItemPosition(elementDisplay.get(0));
//                    mFragment.addItemToMode(elementName, position, format);
//                }


                break;

            case DragEvent.ACTION_DRAG_ENDED:
                //ignore event
              if (mFragment.Vision == "Nightvision Green") {
                v.setBackgroundColor(v.getResources().getColor(android.R.color.holo_green_dark));
              }
              else
                v.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                break;


            default:
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                break;
        }
        return true;
    }
}
