package com.testreel.elevators;

public class FloorCall {

    private int floorNumber;
    private int direction; // 1 - up, 0 - down

    public FloorCall(int floorNumber, int direction) {
        this.floorNumber = floorNumber;
        this.direction = direction;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
