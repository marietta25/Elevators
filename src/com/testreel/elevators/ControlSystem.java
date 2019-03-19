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

    public FloorCall findStop(int floor, int direction) {
        for (int i = 0; i < this.calledFloors.size(); i++) {
            FloorCall current = this.calledFloors.get(i);
            if (current.getFloorNumber() == floor && current.getDirection() == direction) {
                return current;
            }
        }
        return null;
    }

    public boolean removeStop(int floor, String direction) {
        if (direction.toLowerCase() == "up") {
            this.upRequests.remove(floor);
            this.calledFloors.remove(findStop(floor, 1));
            System.out.println("Removing floor call from up requests");
            return true;
        } else if (direction.toLowerCase() == "down") {
            this.downRequests.remove(floor);
            this.calledFloors.remove(findStop(floor, 0));
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
                synchronized (this.calledFloors) {

                    this.calledFloors.add(call);
                    this.calledFloors.notifyAll();
                    System.out.println("Added floor call from " + call.getFloorNumber() + " to merged list");
            }
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
