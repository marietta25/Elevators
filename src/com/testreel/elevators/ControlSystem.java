package com.testreel.elevators;

import java.util.ArrayList;
import java.util.List;

public class ControlSystem {

    private List<Elevator> elevators;
    private List<Floor> floors;
    private List<FloorCall> calledFloors;

    public ControlSystem(List<Elevator> elevators, List<Floor> floors) {
        this.elevators = elevators;
        this.floors = floors;
        this.calledFloors = new ArrayList<>();
    }

    public Elevator selectIdleElevator() {
        for (int i = 0; i < this.elevators.size(); i++) {
            Elevator current = this.elevators.get(i);
            if (current.getStatus() == ElevatorStatus.STOPPED && (!current.isInMove())) {
                System.out.println("Found idle elevator - " + current.getElevatorNumber() + " on floor " + current.getCurrentFloor());
                return current;
            }
        }
        return null;
    }

    public void sendElevator(FloorCall call) {
        // can only send an elevator that is not currently moving
        Elevator idleElevator = selectIdleElevator();

        while (idleElevator == null) {
            idleElevator = selectIdleElevator();
        }
        // found elevator
        //idleElevator.move(call);
    }

    public boolean removeFloorCall(FloorCall call) {
        for (int i = 0; i < this.calledFloors.size(); i++) {
            FloorCall current = this.calledFloors.get(i);
            if ((current.getFloorNumber() == call.getFloorNumber()) && (current.getDirection() == call.getDirection())) {
                this.calledFloors.remove(current);
                System.out.println("Removing floor call from list");
                return true;
            }
        }
        System.out.println("No such floor call to remove");
        return false;
    }

    public boolean addFloorCall(FloorCall call) {
        if (addFloorCall(call.getFloorNumber(), call.getDirection())) {
            sendElevator(call);
            return true;
        }
        return false;
    }

    public boolean addFloorCall(int floorNumber, int direction) {
        Floor calledFrom = this.floors.get(floorNumber-1);
        FloorCall returned;

        synchronized (this.calledFloors) {
            System.out.println("Calling from " + calledFrom.getFloorNumber());
            if (direction == 1) {
                returned = calledFrom.callToGoUp();
            } else if (direction == 0) {
                returned = calledFrom.callToGoDown();
            } else {
                System.out.println("Invalid direction - 1 to go up, 0 to go down");
                return false;
            }

            if (returned != null) {
                for (int i = 0; i < this.calledFloors.size(); i++) {
                    FloorCall current = this.calledFloors.get(i);
                    if ((current.getFloorNumber() == returned.getFloorNumber()) && (current.getDirection() == returned.getDirection())) {
                        System.out.println("A call is already made from this floor");
                        return false;
                    }
                }
                this.calledFloors.add(returned);
                this.calledFloors.notifyAll();
                System.out.println("Added floor call from " + returned.getFloorNumber() + " to list");

                return true;
            }
            return false;
        }
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<FloorCall> getCalledFloors() {
        return calledFloors;
    }
}
