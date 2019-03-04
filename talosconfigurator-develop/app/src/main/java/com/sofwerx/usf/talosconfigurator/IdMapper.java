package com.sofwerx.usf.talosconfigurator;

import java.util.List;

/**
 * Created by Junior on 11/30/2017.
 */

public class IdMapper {

    private int x;
    private int y;
    private List<Integer> ids;

    public IdMapper(int x, int y, List<Integer> ids) {
        this.x = x;
        this.y = y;
        this.ids = ids;
    }

    public IdMapper(Position pos, List<Integer> ids) {
        x = pos.getxPos();
        y = pos.getyPos();
        this.ids = ids;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
