package com.testreel.elevators;

import java.util.Set;
import java.util.TreeSet;

public class Floor {

    private int floorNumber;
    private boolean upButtonPressed;
    private boolean downButtonPressed;
    private Set<Integer> floorCalls; // for keeping track of calls to go up and down - max 1 up and 1 down per floor

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.upButtonPressed = false;
        this.downButtonPressed = false;
        //this.floorCalls = new TreeSet<>();
    }

    public FloorCall callToGoUp(int destinationFloor) {
        if (upButtonPressed) {
            System.out.println("Already called elevator to go up");
            return null;
        }
        if (this.floorNumber == 13) {
            System.out.println("Cannot go up, final floor");
            return null;
        }
        this.upButtonPressed = true;
        FloorCall up = new FloorCall(this.floorNumber, destinationFloor, 1);
        //this.floorCalls.add(1);
        return up;
    }

    public FloorCall callToGoDown(int destinationFloor) {
        if (downButtonPressed) {
            System.out.println("Already called elevator to go down");
            return null;
        }
        if (this.floorNumber == 1) {
            System.out.println("Cannot go down, on first floor");
            return null;
        }
        this.downButtonPressed = true;
        FloorCall down = new FloorCall(this.floorNumber, destinationFloor, 0);
        //this.floorCalls.add(0);
        return down;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public boolean isUpButtonPressed() {
        return upButtonPressed;
    }

    public void setUpButtonPressed(boolean upButtonPressed) {
        this.upButtonPressed = upButtonPressed;
    }

    public boolean isDownButtonPressed() {
        return downButtonPressed;
    }

    public void setDownButtonPressed(boolean downButtonPressed) {
        this.downButtonPressed = downButtonPressed;
    }
}
