package com.sofwerx.usf.talosconfigurator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Junior on 10/29/2017.
 */

public class FragmentState implements Parcelable {

    private String value;
    private float xLocation;
    private float yLocation;


    public FragmentState(String val, float x, float y ) {
        value = val;
        xLocation = x;
        yLocation = y;
    }

    public FragmentState(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<FragmentState> CREATOR = new Parcelable.Creator<FragmentState>() {
        public FragmentState createFromParcel(Parcel in) {
            return new FragmentState(in);
        }

        public FragmentState[] newArray(int size) {
            return new FragmentState[size];
        }
    };

    public void readFromParcel(Parcel in) {
        value = in.readString();
        xLocation = in.readFloat();
        yLocation = in.readFloat();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
        dest.writeFloat(xLocation);
        dest.writeFloat(yLocation);
    }

    public String getValue() {
        return value;
    }

    public float getXLocation() {
        return xLocation;
    }

    public float getYLocation() {
        return yLocation;
    }

    public void setValue(String val) {
        value = val;
    }

    public void setXLocation(float x) {
        xLocation = x;
    }

    public void setYLocation(float y) {
        yLocation = y;
    }
}
