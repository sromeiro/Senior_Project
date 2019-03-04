package com.sofwerx.usf.talosconfigurator;

/**
 * Created by Junior on 11/15/2017.
 * This class contains the x and y coordinates of a cell position
 * in the screen (pixels), not the x and y coordinates relative to the grid (i,j)
 */

public class GridPosition {
    private int xPos;
    private int yPos;

    public GridPosition(int x, int y) {
        xPos = x;
        yPos = y;
    }

    int getXPos() {
        return xPos;
    }

    int getYPos() {
        return yPos;
    }
}
