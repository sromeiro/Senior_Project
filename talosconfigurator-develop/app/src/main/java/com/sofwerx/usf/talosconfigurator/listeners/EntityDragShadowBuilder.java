package com.sofwerx.usf.talosconfigurator.listeners;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Junior on 10/24/2017.
 */

public class EntityDragShadowBuilder extends View.DragShadowBuilder {

    private static Drawable shadow;

    public EntityDragShadowBuilder(View v) {
        super(v);
        shadow = new ColorDrawable(Color.LTGRAY);
    }

    //defines a callback that sends the drag shadow dimensions and
    // touch back to system
    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        int width, height;

        //setting size of shadow half size of view
        //may not be necessary
        width = 100;
        height = 100;
        shadow.setBounds(0,0, width, height);

        size.set(width, height);
        touch.set(width / 2, height / 2);
    }

    //draws shadow in a canvas that the system constructs from the dimensions
    //passed in onProvideShadowMetrics()
    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}
