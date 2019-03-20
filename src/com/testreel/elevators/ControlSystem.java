package com.testreel.elevators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class ControlSystem {

    private List<Elevator> elevators;
    private List<Floor> floors;
    private List<FloorCall> calledFloors;
    private ArrayList<FloorCall> upRequests;
    private ArrayList<FloorCall> downRequests;

    public ControlSystem(List<Elevator> elevators, List<Floor> floors) {
        this.elevators = elevators;
        this.floors = floors;
        this.calledFloors = new ArrayList<>();
        this.upRequests = new ArrayList<>();
        this.downRequests = new ArrayList<>();
    }

    public synchronized FloorCall findStop(int startFloor, int destinationFloor) {
        for (int i = 0; i < this.calledFloors.size(); i++) {
            FloorCall current = this.calledFloors.get(i);
            if (current.getStartFloor() == startFloor && current.getDestinationFloor() == destinationFloor) {
                return current;
            }
        }
        return null;
    }

    public synchronized boolean removeStop(int floor, int destinationFloor, String direction) {
        if (direction.toLowerCase() == "up") {
            this.upRequests.remove(findStop(floor, destinationFloor));
            this.calledFloors.remove(findStop(floor, destinationFloor));
            System.out.println("Removing floor call from up requests");
            return true;
        } else if (direction.toLowerCase() == "down") {
            this.downRequests.remove(findStop(floor, destinationFloor));
            this.calledFloors.remove(findStop(floor, destinationFloor));
            System.out.println("Removing floor call from down requests");
            return true;
        } else {
            System.out.println("Check direction parameter");
            return false;
        }
    }

    public FloorCall makeFloorCall(int fromFloor, int toFloor, int direction) {
        Floor calledFrom = this.floors.get(fromFloor-1);
        FloorCall newCall;

        if (direction == 1) {
            // request to go up
            newCall = calledFrom.callToGoUp(toFloor);

        } else if (direction == 0) {
            // request to go down
            newCall = calledFrom.callToGoDown(toFloor);
        } else {
            return null;
        }
        return newCall;
    }

    public boolean addFloorCall(FloorCall call) {
        if (call != null) {
            if (addFloorCall(call.getStartFloor(), call.getDestinationFloor(), call.getDirection())) {
                synchronized (this.calledFloors) {

                    this.calledFloors.add(call);
                    this.calledFloors.notifyAll();
                    System.out.println("Added floor call from " + call.getStartFloor() + " to " + call.getDestinationFloor() + " to merged list");
            }
                return true;
            }
            return false;
        }

        return false;
    }

    public boolean addFloorCall(int startFloor, int destinationFloor, int direction) {
        if (direction == 1) {
            // request to go up
            synchronized (this.upRequests) {

                this.upRequests.add(new FloorCall(startFloor, destinationFloor, direction));
                Collections.sort(this.upRequests, FloorCall.FloorCallSort);
                this.upRequests.notifyAll();
                System.out.println("Added floor call from " + startFloor + " to uprequest list");
                return true;
            }
        } else if (direction == 0) {
            // request to go down
            synchronized (this.downRequests) {

                this.downRequests.add(new FloorCall(startFloor, destinationFloor, direction));
                Collections.sort(this.downRequests, FloorCall.FloorCallSort);
                this.downRequests.notifyAll();
                System.out.println("Added floor call from " + startFloor + " to downrequests list");
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

    public ArrayList<FloorCall> getUpRequests() {
        return upRequests;
    }

    public ArrayList<FloorCall> getDownRequests() {
        return downRequests;
    }
}
