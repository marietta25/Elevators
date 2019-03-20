package com.testreel.elevators;

public class Floor {

    private int floorNumber;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    // create new floor call to go up
    public FloorCall callToGoUp(int destinationFloor) {
        if (this.floorNumber == 13) {
            System.out.println("Cannot go up, final floor");
            return null;
        }
        return new FloorCall(this.floorNumber, destinationFloor, 1);
    }

    // create new floor call to go down
    public FloorCall callToGoDown(int destinationFloor) {
        if (this.floorNumber == 1) {
            System.out.println("Cannot go down, on first floor");
            return null;
        }
        return new FloorCall(this.floorNumber, destinationFloor, 0);
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }
}
