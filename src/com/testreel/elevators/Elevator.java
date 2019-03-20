package com.testreel.elevators;

import java.util.*;

import static java.lang.Thread.interrupted;

public class Elevator implements Runnable {

    private int elevatorNumber;
    private int currentFloor;
    private boolean inMove;
    private String currentDirection;
    private ArrayList<Integer> requestedFloorsUp;
    private ArrayList<Integer> requestedFloorsDown;
    private ControlSystem controlSystem;
    private Thread thread;
    private String color; // to color different thread outputs in IntelliJ

    public Elevator(int elevatorNumber, int currentFloor, ControlSystem controlSystem, String color) {
        this.elevatorNumber = elevatorNumber;

        if (currentFloor < 1 || currentFloor > 13) {
            System.out.println("Invalid floor");
        } else {
            this.currentFloor = currentFloor;
        }

        this.currentDirection = "UP";
        this.requestedFloorsUp = new ArrayList<>();
        this.requestedFloorsDown = new ArrayList<>();
        this.controlSystem = controlSystem;
        this.thread = new Thread(this, "Elevator " + this.elevatorNumber);
        this.color = color;
    }

    private synchronized void determineDirection() {
        // change direction if elevator reaches first or last floor
        if (this.currentFloor == 13 && this.currentDirection.equals("UP")) {
            this.currentDirection = "DOWN";
        } else if (this.currentFloor == 1 && this.currentDirection.equals("DOWN")) {
            this.currentDirection = "UP";
        }
    }

    // FIXME! ugly spaghetti code
    // return next stop for an elevator
    private int getNextStop() throws InterruptedException {
        List<FloorCall> calledFloors = controlSystem.getCalledFloors();
        ArrayList<FloorCall> upRequests = controlSystem.getUpRequests();
        ArrayList<FloorCall> downRequests = controlSystem.getDownRequests();


        synchronized (calledFloors) {
            while (upRequests.isEmpty() && downRequests.isEmpty() && this.requestedFloorsUp.isEmpty() && this.requestedFloorsDown.isEmpty()) {
                // no floor calls nor passenger requests are left to serve
                System.out.println(color + this.elevatorNumber + " is Waiting for a floor call..");
                calledFloors.wait();
            }
            // sort passenger requests
            Collections.sort(this.requestedFloorsUp);
            Collections.sort(this.requestedFloorsDown);

            System.out.println(color + this.elevatorNumber + " pressed up buttons " + this.requestedFloorsUp);
            System.out.println(color + this.elevatorNumber + " pressed down buttons " + this.requestedFloorsDown);

            if (this.requestedFloorsUp.isEmpty() && this.requestedFloorsDown.isEmpty()) {
                // only floor calls, eg no passenger requests yet
                FloorCall nextStop = null;

                if (!upRequests.isEmpty()) {
                    // there are requested floor calls to go up
                    for (FloorCall call : upRequests) {
                        System.out.println(color + "iterating elev " + this.elevatorNumber + " calls " + call.getStartFloor() + " " + call.getDestinationFloor());
                    }

                    // select floor call that is closest to the elevator
                    int distance;
                    int minDistance = 12;

                    for (FloorCall calledFrom : upRequests) {
                        distance = Math.abs(this.currentFloor - calledFrom.getStartFloor());
                        System.out.println("Distance from " + calledFrom.getStartFloor() + " " + distance);
                        if (distance <= minDistance) {
                            minDistance = distance;
                            nextStop = calledFrom;
                        }
                    }

                    upRequests.remove(nextStop);

                    // find if there is more requests to go up from that floor
                    List<FloorCall> stopsToRemove = new ArrayList<>();
                    for (FloorCall current : upRequests) {
                        if (current.getStartFloor() == nextStop.getStartFloor()) {
                            System.out.println("Found more up requests from the same floor " + current.getDestinationFloor());
                            stopsToRemove.add(current);
                            this.requestedFloorsUp.add(current.getDestinationFloor());
                            controlSystem.removeStop(current.getStartFloor(), current.getDestinationFloor(), "up");
                        }
                    }
                    // remove found double requests
                    upRequests.removeAll(stopsToRemove);

                    controlSystem.removeStop(nextStop.getStartFloor(), nextStop.getDestinationFloor(), "up");
                } else {
                    // there are requested floor calls to go down
                    // select floor call that is closest to the elevator
                    int distance;
                    int minDistance = 12;

                    for (FloorCall calledFrom : downRequests) {
                        distance = Math.abs(this.currentFloor - calledFrom.getStartFloor());
                        System.out.println("Distance from " + calledFrom.getStartFloor() + " " + distance);
                        if (distance <= minDistance) {
                            minDistance = distance;
                            nextStop = calledFrom;
                        }
                    }

                    downRequests.remove(nextStop);

                    // find if there is more requests to go down on that floor
                    List<FloorCall> stopsToRemove = new ArrayList<>();
                    for (FloorCall current : downRequests) {
                        if (current.getStartFloor() == nextStop.getStartFloor()) {
                            System.out.println("Found more down requests from the same floor " + current.getDestinationFloor());
                            stopsToRemove.add(current);
                            this.requestedFloorsDown.add(current.getDestinationFloor());
                            controlSystem.removeStop(current.getStartFloor(), current.getDestinationFloor(), "down");
                        }
                    }
                    // remove found double requests
                    downRequests.removeAll(stopsToRemove);

                    controlSystem.removeStop(nextStop.getStartFloor(), nextStop.getDestinationFloor(), "down");
                }

                System.out.println(color + "1---getnextstop " + nextStop.getStartFloor());
                if (nextStop.getDirection() == 1) {
                    this.requestedFloorsUp.add(nextStop.getDestinationFloor());
                } else if (nextStop.getDirection() == 0) {
                    this.requestedFloorsDown.add(nextStop.getDestinationFloor());
                }

                calledFloors.notifyAll();
                return nextStop.getStartFloor();

            } else if (!this.requestedFloorsUp.isEmpty()) {
                // there are passenger requests to go up (made from inside the elevator)
                FloorCall nextStop;
                Integer requestedStop;

                Integer nextUpRequest = this.requestedFloorsUp.get(0);
                System.out.println(color + "2----- nextuprequest " + nextUpRequest);


                if (!upRequests.isEmpty()) {
                    // there are pending floor calls
                    for (FloorCall call : upRequests) {
                        System.out.println(color + "iterating elev " + this.elevatorNumber + " calls " + call.getStartFloor() + " " + call.getDestinationFloor());
                    }
                    nextStop = upRequests.get(0);

                    if (nextStop.getStartFloor() == nextUpRequest) {
                        // remove double floor calls
                        System.out.println("Remove double up call");
                        this.requestedFloorsUp.remove(nextUpRequest);
                    }
                    this.requestedFloorsUp.add(nextStop.getDestinationFloor());
                } else {
                    // there are no pending calls, only passenger requests from floor ie only integers
                    requestedStop = nextUpRequest;
                    System.out.println(color + " only passenger request");
                    this.requestedFloorsUp.remove(requestedStop);
                    return requestedStop;
                }

                System.out.println(color + "2----getnextstop " + nextStop.getStartFloor());
                upRequests.remove(nextStop);
                controlSystem.removeStop(nextStop.getStartFloor(), nextStop.getDestinationFloor(), "up");
                calledFloors.notifyAll();
                return nextStop.getStartFloor();

            } else if (!this.requestedFloorsDown.isEmpty()) {
                // there are passenger requests to go down (made from inside the elevator)
                FloorCall nextStop;
                Integer requestedStop;

                Integer nextDownRequest = this.requestedFloorsDown.get(this.requestedFloorsDown.size()-1);

                if (!downRequests.isEmpty()) {
                    // there are pending floor calls
                    nextStop = downRequests.get(downRequests.size()-1);

                    if (nextStop.getStartFloor() == nextDownRequest) {
                        // remove double floor calls
                        System.out.println("Remove double down call");
                        this.requestedFloorsDown.remove(nextDownRequest);
                    }

                    this.requestedFloorsDown.add(nextStop.getDestinationFloor());
                } else {
                    // there are no pending calls, only passenger requests from floor ie only integers
                    requestedStop = nextDownRequest;
                    System.out.println(color + " only passenger request");
                    this.requestedFloorsDown.remove(requestedStop);
                    return requestedStop;
                }

                System.out.println(color + "3----getnextstop " + nextStop.getStartFloor());
                downRequests.remove(nextStop);
                controlSystem.removeStop(nextStop.getStartFloor(), nextStop.getDestinationFloor(), "down");
                calledFloors.notifyAll();
                return nextStop.getStartFloor();

            } else {
                System.out.println(color + " Something went wrong - got no next stop");
                return -1;
            }
        }
    }

    private void stop() {
        // reached floor that the request came from or reached requested destination floor
        System.out.println(color + "Elevator " + this.elevatorNumber + " stopping on floor " + this.currentFloor);
        this.inMove = false;

        // pause elevator to let passengers out / in
        pauseThread(5000);
    }

    // pause elevator thread for a given time (e.g move between floors, let passengers out, etc
    private void pauseThread(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // move elevator up and down depending on destination floor
    private void move(int destinationFloor) {
        if (destinationFloor < 1 || destinationFloor > 13) {
            System.out.println("Invalid destination floor");
            return;
        }

        this.inMove = true;
        determineDirection();

        if (this.currentFloor != destinationFloor) {
            System.out.println(color + "Elevator " + this.elevatorNumber + " is starting to move from floor " + this.currentFloor);
        }

        while (true) {
            if (destinationFloor < this.currentFloor) {
                this.currentDirection = "DOWN";
                pauseThread(2000);
                this.currentFloor--;
                System.out.println(color + "Elevator " + this.elevatorNumber + " is going " + this.currentDirection + ", reached floor " + this.currentFloor);
            } else if (destinationFloor > this.currentFloor) {

                this.currentDirection = "UP";
                pauseThread(2000);
                this.currentFloor++;
                System.out.println(color + "Elevator " + this.elevatorNumber + " is going " + this.currentDirection + ", reached floor " + this.currentFloor);
            } else {
                // reached destination floor
                this.currentFloor = destinationFloor;
                stop();
                return;
            }
        }
    }

    public int getElevatorNumber() {
        return elevatorNumber;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public boolean isInMove() {
        return inMove;
    }

    public String getCurrentDirection() {
        return currentDirection;
    }

    public ArrayList<Integer> getRequestedFloorsUp() {
        return requestedFloorsUp;
    }

    public ArrayList<Integer> getRequestedFloorsDown() {
        return requestedFloorsDown;
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        System.out.println(color + "Elevator " + this.elevatorNumber + " thread is running");
        while (true) {
            if (interrupted()) {
                return;
            }

            try {
                int nextStop = getNextStop();
                System.out.println(color + "Elevator " + elevatorNumber + " is servicing next request to floor " + nextStop);
                move(nextStop);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
