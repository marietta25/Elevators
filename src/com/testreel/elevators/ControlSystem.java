package com.testreel.elevators;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ControlSystem {

    private List<Elevator> elevators;
    private List<Floor> floors;
    private List<FloorCall> calledFloors;
    private TreeSet<Integer> upRequests;
    private TreeSet<Integer> downRequests;

    public ControlSystem(List<Elevator> elevators, List<Floor> floors) {
        this.elevators = elevators;
        this.floors = floors;
        this.calledFloors = new ArrayList<>();
        this.upRequests = new TreeSet<>();
        this.downRequests = new TreeSet<>();
    }


    public boolean removeStop(int floor, String direction) {
        if (direction.toLowerCase() == "up") {
            this.upRequests.remove(floor);
            System.out.println("Removing floor call from up requests");
            return true;
        } else if (direction.toLowerCase() == "down") {
            this.downRequests.remove(floor);
            System.out.println("Removing floor call from down requests");
            return true;
        } else {
            System.out.println("Check direction parameter");
            return false;
        }
    }

    public FloorCall makeFloorCall(int floorNumber, int direction) {
        Floor calledFrom = this.floors.get(floorNumber-1);
        FloorCall newCall;

        if (direction == 1) {
            // request to go up
            newCall = calledFrom.callToGoUp();

        } else if (direction == 0) {
            // request to go down
            newCall = calledFrom.callToGoDown();
        } else {
            return null;
        }
        return newCall;
    }

    public boolean addFloorCall(FloorCall call) {
        if (call != null) {
            if (addFloorCall(call.getFloorNumber(), call.getDirection())) {
                return true;
            }
            return false;
        }

        return false;
    }

    public boolean addFloorCall(int floorNumber, int direction) {

        if (direction == 1) {
            // request to go up
            synchronized (this.upRequests) {
                System.out.println("Calling to go up from " + floorNumber);

                this.upRequests.add(floorNumber);
                this.upRequests.notifyAll();
                System.out.println("Added floor call from " + floorNumber + " to list");
                return true;
            }
        } else if (direction == 0) {
            // request to go down
            synchronized (this.downRequests) {
                System.out.println("Calling to go down from " + floorNumber);

                this.downRequests.add(floorNumber);
                this.downRequests.notifyAll();
                System.out.println("Added floor call from " + floorNumber + " to list");
                return true;
            }
        } else {
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

    public TreeSet<Integer> getUpRequests() {
        return upRequests;
    }

    public TreeSet<Integer> getDownRequests() {
        return downRequests;
    }
}
