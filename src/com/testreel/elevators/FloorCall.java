package com.testreel.elevators;

import java.util.Comparator;

public class FloorCall {

    private int startFloor;
    private int destinationFloor;
    private int direction; // 1 - up, 0 - down

    public FloorCall(int startFloor, int destinationFloor, int direction) {
        this.startFloor = startFloor;
        this.destinationFloor = destinationFloor;
        this.direction = direction;
    }

    public int getStartFloor() {
        return startFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setStartFloor(int startFloor) {
        this.startFloor = startFloor;
    }

    public static Comparator<FloorCall> FloorCallSort = new Comparator<FloorCall>() {
        @Override
        public int compare(FloorCall o1, FloorCall o2) {
            int startFloor1 = o1.getStartFloor();
            int startFloor2 = o2.getStartFloor();

            return startFloor1 - startFloor2;
        }
    };
}
