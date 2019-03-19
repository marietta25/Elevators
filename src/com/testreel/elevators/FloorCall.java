package com.testreel.elevators;

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
}
