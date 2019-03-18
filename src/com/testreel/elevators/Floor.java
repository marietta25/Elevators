package com.testreel.elevators;

public class Floor {

    private int floorNumber;
    private boolean upButtonPressed;
    private boolean downButtonPressed;
    private FloorCall floorCall;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.upButtonPressed = false;
        this.downButtonPressed = false;
        this.floorCall = null;
    }

    public FloorCall callToGoUp() {
        if (upButtonPressed) {
            System.out.println("Already called elevator to go up");
            return null;
        }
        if (this.floorNumber == 13) {
            System.out.println("Cannot go up, final floor");
            return null;
        }
        this.upButtonPressed = true;
        this.floorCall = new FloorCall(this.floorNumber, 1);
        return this.floorCall;
    }

    public FloorCall callToGoDown() {
        if (downButtonPressed) {
            System.out.println("Already called elevator to go down");
            return null;
        }
        if (this.floorNumber == 1) {
            System.out.println("Cannot go down, on first floor");
            return null;
        }
        this.downButtonPressed = true;
        this.floorCall = new FloorCall(this.floorNumber, 0);
        return this.floorCall;
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
