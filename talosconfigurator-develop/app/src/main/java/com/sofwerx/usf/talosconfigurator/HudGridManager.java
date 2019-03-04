package com.sofwerx.usf.talosconfigurator;

import android.view.DragEvent;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junior on 11/26/2017.
 * This class is in charge of making sure that hud2 and hud4 mirror each other
 * as well as dynamically loading new elements to the hud displays
 */

public class HudGridManager {

    private Grid hud1;
    private Grid hud2;
    private Grid hud3;
    private Grid hud4;

    public HudGridManager(Grid h1, Grid h2, Grid h3, Grid h4) {
        hud1 = h1;
        hud2 = h2;
        hud3 = h3;
        hud4 = h4;
    }

    /*
        This function add a new element to a given Mode and returns the cells coordinate
        on where on the grid the element is located. The function returns null if adding
        the element was unsuccessful
     */
    public Position addItemToMode(String name, DragEvent event, ViewGroup owner, HudFragment hudFragment) {
        int localId = hudFragment.getElementsIds();
        String hudName = name.toLowerCase();
        switch(name.toLowerCase()) {
            case "hud1": {
                List<TextView> elementViews = hud1.addItemToGrid(event);
                List<Integer> ids = new ArrayList<Integer>();
                if(elementViews != null && elementViews.size() > 0) {
                    for(TextView view: elementViews) {
                        view.setId(localId);
                        ids.add(localId);
                        localId++;
                        displayItem(owner, view);
                    }
                    hudFragment.setElementIds(localId);
                    //the first view is used since this contains the
                    //the origin of the element (top-left corner)
                    Position pos = hud1.getItemPosition(elementViews.get(0));
                    hudFragment.updateViewsTracker("hud1", pos, ids);
                    return pos;
                }
                return null;

            }

            case "hud2": {
                List<TextView> element2Views = hud2.addItemToGrid(event);
                List<TextView> element3Views = hud3.addItemToGrid(event);

                List<Integer> ids2 = new ArrayList<Integer>();
                List<Integer> ids3 = new ArrayList<Integer>();

                if(element2Views != null && element3Views != null && element2Views.size() > 0) {
                    for(TextView view: element2Views) {
                        view.setId(localId);
                        ids2.add(localId);
                        localId++;
                        displayItem(owner, view);

                    }
                    for(TextView view: element3Views) {
                        view.setId(localId);
                        ids3.add(localId);
                        localId++;
                        displayItem(owner, view);
                    }

                    hudFragment.setElementIds(localId);
                    //the first view is used since this contains the
                    //the origin of the element (top-left corner)
                    //Only the element on hud 2 is saved
                    Position pos2 = hud2.getItemPosition(element2Views.get(0));
                    Position pos3 = hud3. getItemPosition(element3Views.get(0));

                    hudFragment.updateViewsTracker("hud2", pos2, ids2);
                    hudFragment.updateViewsTracker("hud3", pos3, ids3);
                    return pos2;
                }
                return null;
            }

            case "hud3": {
                List<TextView> element3Views = hud3.addItemToGrid(event);
                List<TextView> element2Views = hud2.addItemToGrid(event);

                List<Integer> ids3 = new ArrayList<Integer>();
                List<Integer> ids2 = new ArrayList<Integer>();

                if(element2Views != null && element3Views != null && element3Views.size() > 0) {
                    for(TextView view: element3Views) {
                        view.setId(localId);
                        ids3.add(localId);
                        localId++;
                        displayItem(owner, view);

                    }
                    for(TextView view: element2Views) {
                        view.setId(localId);
                        ids2.add(localId);
                        localId++;
                        displayItem(owner, view);

                    }

                    hudFragment.setElementIds(localId);
                    Position pos3 = hud3.getItemPosition(element3Views.get(0));
                    Position pos2 = hud2.getItemPosition(element2Views.get(0));

                    hudFragment.updateViewsTracker("hud2", pos2, ids2);
                    hudFragment.updateViewsTracker("hud3", pos3, ids3);
                    return pos3;
                }
                return null;
            }

            case "hud4": {
                List<TextView> element4Views = hud4.addItemToGrid(event);
                List<Integer> ids = new ArrayList<Integer>();
                if(element4Views != null && element4Views.size() > 0) {
                    for(TextView view: element4Views) {
                        view.setId(localId);
                        ids.add(localId);
                        localId++;
                        displayItem(owner, view);
                    }

                    hudFragment.setElementIds(localId);
                    Position pos = hud4.getItemPosition(element4Views.get(0));
                    hudFragment.updateViewsTracker("hud4", pos, ids);
                    return pos;
                }
                return null;
            }

            default: {
                return null;
            }
        }
    }

    public void clearGrids() {
        hud1.clear();
        hud2.clear();
        hud3.clear();
        hud4.clear();
    }

    /*
        This function adds a new element to the screen so that it can be displayed
     */
    private void displayItem(ViewGroup owner, TextView view) {
        owner.addView(view, view.getWidth(), view.getLineHeight());
    }
}
