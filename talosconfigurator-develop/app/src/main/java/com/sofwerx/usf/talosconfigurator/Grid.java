package com.sofwerx.usf.talosconfigurator;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junior on 11/8/2017.
 * This file defines a custom view call Grid. This view acts as the hud grid that manages
 * the location of the Mode Elements
 */

public class Grid extends View {

    private final String COPY_DATA = "copy_data";
    private final String FORMAT = "format";
    private boolean cellsTaken[][];
    private GridPosition cellPositions[][];
    private int rows;
    private int columns;
    private int cellWidth;
    private int cellHeight;
    private String hudName;

    public Grid(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Grid,
                0,0);

        try {
            rows = a.getInteger(R.styleable.Grid_rows, 0);
            columns = a.getInteger(R.styleable.Grid_columns, 0);
            hudName = a.getString(R.styleable.Grid_name);
            cellsTaken = new boolean[rows][columns];
        } catch (Exception e) {
            cellsTaken = new boolean[0][0];
        }
    }

    /*
       This function is used to set the size of the grid and set the display positions of
       each cell. This function is needed since the size of the grid is dynamic. For instance,
       width and height of the view in the layout are not known when the view is created.
     */
    public void setGridDimentions(int r, int c, int topGrid, int leftGrid, int width, int height) {
        rows = r;
        columns = c;
        cellsTaken = new boolean[rows][columns];
        cellPositions = new GridPosition[rows][columns];

        cellWidth = width / columns;
        cellHeight = height / rows;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cellPositions[i][j] = new GridPosition(leftGrid + (j * cellWidth), topGrid + (i * cellHeight));
            }
        }
    }

    /*
        This function checks if an element can be added to a grid and if it does, the cells
        the given format provides are set to occupied and a list of textviews is returned
        witht the given style of the format
     */
    public List<TextView> addItemToGrid(DragEvent event){
        boolean spaceAvailable = false;
        ClipData.Item item = event.getClipData().getItemAt(0);
        Bundle bundle = item.getIntent().getBundleExtra(COPY_DATA);
        bundle.setClassLoader(Format.class.getClassLoader());
        ArrayList<Format> format = bundle.getParcelableArrayList(FORMAT);
        Format formatItem = format.get(0);
        int formatWidth = formatItem.getxPos();
        int formatHeight = formatItem.getyPos();
        List<TextView> elementStyleDisplay = new ArrayList<TextView>();
        int itemX = (int)event.getX() + (int)getX();
        int itemY = (int)event.getY() + (int)getY();
        int xDelta = cellWidth / 2;
        int yDelta = cellHeight / 2;

        if(formatWidth <= 0 && formatHeight <= 0) return null;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(isDropPositionFree(cellPositions[i][j], itemX, itemY, xDelta, yDelta)) {
                    if(!cellsTaken[i][j]) {
                        if(i + formatHeight < rows && j + formatWidth < columns) {
                            boolean objectsAround = false;
                            for(int k = 0; k < formatHeight; k++) {
                                for(int l = 0; l < formatWidth; l++) {
                                    if(cellsTaken[i + k][j + l]) {
                                        objectsAround = true;
                                        break;
                                    }
                                }
                                if(objectsAround) break;
                            }
                            if(!objectsAround) {
                                for(int k = 0; k < formatHeight; k++) {
                                    for(int l = 0; l < formatWidth; l++) {
                                        cellsTaken[i + k][j + l] = true;
                                    }
                                }
                                spaceAvailable = true;
                                List<String> styles = formatItem.getStyle();
                                for(int k = 0; k < formatHeight; k++) {
                                    String style = styles.get(k);
                                    TextView view = new TextView(getContext());
                                    GridPosition position = cellPositions[i + k][j];

                                    //snapping view on grid
                                    view.setX((float)position.getXPos());
                                    view.setY((float) position.getYPos());
                                    view.setText(style);
                                    view.setWidth(formatWidth * cellWidth);
                                    view.setHeight(cellHeight);
                                    elementStyleDisplay.add(view);
                                }

                                break;
                            }
                        }

                    }
                }
            }
            if(spaceAvailable) break;
        }

        if(spaceAvailable) return elementStyleDisplay;
        else return null;
    }

    /*
        This functions also adds item to the grid, but it should be used only on when
        loading elements from the xml, not when the user drags element on the grid
     */
    public List<TextView> addItemToGrid(ModeElement element, Format format) {
        if(format == null) return null;
        if(format.getxPos() <= 0 && format.getyPos() <= 0) return null;

        int column = element.getPosition().getxPos();
        int row = element.getPosition().getyPos();
        int width = format.getxPos() * cellWidth;

//        Log.d("bruh", "column: " + column);
//        Log.d("bruh", "row: " + row);
        List<TextView> elementDisplay = new ArrayList<TextView>();
        int i = 0;
        if(column + format.getxPos() > rows || row + format.getyPos() > columns) return null;

        for(String style: format.getStyle()) {
            TextView view = new TextView(getContext());
            GridPosition position = cellPositions[row + i][column];
            view.setText(style);
            view.setX(position.getXPos());
            view.setY(position.getYPos());
            view.setHeight(cellHeight);
            view.setWidth(width);
            elementDisplay.add(view);
            i++;
        }

        for(int j = row; j < row + format.getyPos(); j++) {
            for(int k = column; k < column + format.getxPos(); k++) {
                cellsTaken[j][k] = true;
            }
        }
        return elementDisplay;
    }

    /*
        This function adds gets the cell location of a view in the grid
     */
    public Position getItemPosition(View item) {
        Position position;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                GridPosition pos = cellPositions[i][j];
                if(item.getX() == pos.getXPos() && item.getY() == pos.getYPos()) {
                    List<String> huds = new ArrayList<String>();
                    huds.add(hudName);
                    position = new Position(huds,j,i);
                    return position;
                }
            }
        }
        return null;
    }

    /*
        This function checks if a location a user is dragging over is a valid location
        in the grid (i.e. a given location is already used by an element or the element won't fit
        in the grid if it's drop on the given location)
     */
    public boolean isLocationValid(DragEvent event) {
        int dropX = (int) event.getX() + (int)getX();
        int dropY = (int) event.getY() + (int)getY();

        int xDelta = cellWidth / 2;
        int yDelta = cellHeight / 2;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(isDropPositionFree(cellPositions[i][j], dropX, dropY, xDelta, yDelta)) {
                    if(cellsTaken[i][j]) {
                        return false;
                    }
                    else return true;
                }
            }
        }
        return false;
    }

    /*
    This function checks if a location a user is dragging over is a valid location
    in the grid (i.e. a given location is already used by an element or the element won't fit
    in the grid if it's drop on the given location)
 */
    public boolean isLocationValid(DragEvent event, Format formatItem) {
        if(formatItem == null) return false;

        int formatWidth = formatItem.getxPos();
        int formatHeight = formatItem.getyPos();
        int itemX = (int)event.getX() + (int)getX();
        int itemY = (int)event.getY() + (int)getY();
        int xDelta = cellWidth / 2;
        int yDelta = cellHeight / 2;

        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(isDropPositionFree(cellPositions[i][j], itemX, itemY, xDelta, yDelta)) {
                    if(!cellsTaken[i][j]) {
                        if (i + formatHeight < rows && j + formatWidth < columns) {
                            for (int k = 0; k < formatHeight; k++) {
                                for (int l = 0; l < formatWidth; l++) {
                                    if (cellsTaken[i + k][j + l]) {
                                        return false;
                                    }
                                }
                            }
                        }
                        else return false;
                    }
                }
            }
        }
        return true;
    }


    /*This function checks if a given position in the grid is close enough
     to the position an element was drop.
    */
    private  boolean isDropPositionFree(GridPosition position, int itemX, int itemY, int xDelta, int yDelta) {
        boolean xBound = position.getXPos() - xDelta <= itemX && position.getXPos() + xDelta >= itemX;
        boolean yBound = position.getYPos() - yDelta <= itemY && position.getYPos() + yDelta >= itemY;
        return xBound && yBound;
    }

    public void removeElement(Position position){
//        Log.d("bruh", "removing element from grid");
        int j = position.getxPos();
        int i = position.getyPos();
//        Log.d("bruh", "i: " + i);
//        Log.d("bruh", "j: " + j);

        for(int k = i; k < rows; k++) {
            for(int l = j; l < columns; l++) {
                if(cellsTaken[k][l]) {
//                    Log.d("bruh", "clearing cell at");
//                    Log.d("bruh", "k: " + k);
//                    Log.d("bruh", "l: " + l);
                    cellsTaken[k][l] = false;
                }

                else {
                    break;
                }
            }

        }
    }

    public void clear() {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cellsTaken[i][j] = false;
            }
        }
    }

    public Position findElement(int x, int y) {
        Position pos = findTouchedCell(x, y);

        if(pos != null && cellsTaken[pos.getyPos()][pos.getxPos()]) {
            //find top left corner of object
            return findElementPosition(pos);
        }
        return null;
    }

    private Position findElementPosition(Position position) {
        Position pos = new Position();
        int currentI = position.getyPos();
        int currentJ = position.getxPos();
        int originalI = position.getyPos();

        if(currentI == 0) {
            pos.setyPos(currentI);
        }
        else {
            while(cellsTaken[currentI][currentJ]) {
                currentI--;
                if(currentI == -1) break;
            }
            pos.setyPos(currentI + 1);
            currentI = originalI;
        }

        if(currentJ == 0) {
            pos.setxPos(currentJ);
        }
        else {
            while(cellsTaken[currentI][currentJ]) {
                currentJ--;
                if(currentJ == -1) break;
            }
            pos.setxPos(currentJ + 1);
        }

        return pos;
    }

    private Position findTouchedCell(int x, int y) {
        Position pos = new Position();
        int xDelta = cellWidth / 2;
        int yDelta = cellHeight / 2;
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                GridPosition gridPosition = cellPositions[i][j];
                if(isDropPositionFree(gridPosition, x, y, xDelta, yDelta) && cellsTaken[i][j]) {
                    pos.setxPos(j);
                    pos.setyPos(i);
                    return pos;
                }
            }
        }
        return null;
    }



    public String getName() {
        return hudName;
    }
}