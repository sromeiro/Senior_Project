package com.sofwerx.usf.talosconfigurator;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.sofwerx.usf.talosconfigurator.listeners.EntityDragShadowBuilder;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Nicholas Maurer on 10/23/2017.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private final String COPY_DATA = "copy_data";
    private final String FORMAT = "format";
    private final String ELEMENT = "element";
    private List<String> _headers;
    private HashMap<String, List<String>> _children;
    private List<ConfigurableElement> _configElements;
    private Context context;

    public ExpandableListViewAdapter(Context context, TALOSConfiguration config) {
        this.context = context;
        _children = config.getModeMap(config);
        _headers = config.getGroupNames(_children);
        _configElements = config.getConfigurableElements();
    }

    public ExpandableListViewAdapter(Context context, List<String> headers, HashMap<String, List<String>> children) {

        this.context = context;
        this._headers = headers;
        this._children = children;
    }

    @Override
    public int getGroupCount() {
        return _headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<String> child = _children.get(_headers.get(groupPosition));
        if(child != null) return child.size();
        else return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<String> child = _children.get(_headers.get(groupPosition));
        if(child != null) return child.get(childPosition);
        else return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View View, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (View == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View = infalInflater.inflate(R.layout.list_group, null);
        }
        TextView headers = (TextView) View.findViewById(R.id.lblListHeader);
        headers.setTypeface(null, Typeface.BOLD);
        headers.setText(headerTitle);
        return View;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View View, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (View == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View = infalInflater.inflate(R.layout.list_item, null);
        }

        final TextView txtView = (TextView) View.findViewById(R.id.lblListItem);

        txtView.setText(childText);

//        txtView.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(context, Integer.toString(.get(groupP)), Toast.LENGTH_SHORT).show();
//            }
//        });

        Intent elementData = new Intent();
        String header = _headers.get(groupPosition);
        String child = _children.get(header).get(childPosition);
        ArrayList<Format> format = new ArrayList<Format>();
        format.add(getFormat(header, child));
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(FORMAT, format);
        bundle.putString(ELEMENT, _headers.get(groupPosition));
        elementData.putExtra(COPY_DATA, bundle);


        final ClipData dragData = ClipData.newIntent("data", elementData);

        txtView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View.DragShadowBuilder myShadow = new EntityDragShadowBuilder(txtView);
                v.startDrag(dragData, myShadow, v, 0);
                return true;

            }
        });
        return View;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private Format getFormat(String header, String child) {
        for(ConfigurableElement element: _configElements) {
            if(element.getName().equals(header)) {
                List<Format>  formats = element.getFormats();
                for(Format format: formats) {
                    String formatName = format.getName();
                    if (formatName.equals(child)) {
                        return format;
                    }
                }
            }
        }
        return null;
    }


}
