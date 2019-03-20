package com.testreel.elevators;

import java.util.Comparator;

public class FloorSort implements Comparator<FloorCall> {

    @Override
    public int compare(FloorCall call1, FloorCall call2) {
        if (call1.getStartFloor() > call2.getStartFloor()) {
            return 1;
        } else {
            return -1;
        }
    }
}
