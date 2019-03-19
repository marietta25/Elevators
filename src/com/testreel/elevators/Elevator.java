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
            System.out.println("Reached Invalid floor");
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

    /*public boolean pressFloorButton(int floor) {
        if (floor < 1 || floor > 13) {
            System.out.println(color + "Invalid floor");
            return false;
        }
        if (floor == this.currentFloor) {
            System.out.println(color + "Already on " + floor + " floor");
            return false;
        }

        if (this.currentDirection.equals("UP")) {
            System.out.println(color + "Pressed button to stop at floor " + floor);
            this.requestedFloorsUp.add(floor);
            return true;
        } else if (this.currentDirection.equals("DOWN")) {
            System.out.println(color + "Pressed button to stop at floor " + floor);
            this.requestedFloorsDown.add(floor);
            return true;
        }
        return false;
    }*/

    public synchronized void determineDirection() {
        //TreeSet<Integer> upRequests = controlSystem.getUpRequests();
        //TreeSet<Integer> downRequests = controlSystem.getDownRequests();

        if (this.currentFloor == 13 && this.currentDirection.equals("UP")) {
            this.currentDirection = "DOWN";
        } else if (this.currentFloor == 1 && this.currentDirection.equals("DOWN")) {
            this.currentDirection = "UP";
        }
    }

    public int getNextStop() throws InterruptedException {
        //todo - debug adding passenger requests to floor requests
        List<FloorCall> calledFloors = controlSystem.getCalledFloors();
        ArrayList<FloorCall> upRequests = controlSystem.getUpRequests();
        ArrayList<FloorCall> downRequests = controlSystem.getDownRequests();


        synchronized (calledFloors) {
            while (upRequests.isEmpty() && downRequests.isEmpty() && this.requestedFloorsUp.isEmpty() && this.requestedFloorsDown.isEmpty()) {
                System.out.println(color + this.elevatorNumber + " is Waiting for a floor call..");
                calledFloors.wait();
            }
            System.out.println(color + this.elevatorNumber + " pressed up buttons " + this.requestedFloorsUp);
            System.out.println(color + this.elevatorNumber + " pressed down buttons " + this.requestedFloorsDown);

            if (this.requestedFloorsUp.isEmpty() && this.requestedFloorsDown.isEmpty()) {
                FloorCall nextStop;

                if (!upRequests.isEmpty()) {
                    for (FloorCall call : upRequests) {
                        System.out.println(color + "iterating elev " + this.elevatorNumber + " calls " + call.getStartFloor() + " " + call.getDestinationFloor());
                    }

                    nextStop = upRequests.get(0);
                    upRequests.remove(nextStop);
                    controlSystem.removeStop(nextStop.getStartFloor(), nextStop.getDestinationFloor(), "up");
                } else {
                    nextStop = downRequests.get(downRequests.size()-1);
                    downRequests.remove(nextStop);
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
                FloorCall nextStop = null;

                //todo - method to determine next floor up from current floor
                //Integer nextUpRequest = this.requestedFloorsUp.higher(this.currentFloor);
                Integer nextUpRequest = this.requestedFloorsUp.get(0);
                System.out.println(color + "2----- nextuprequest " + nextUpRequest);

                Integer requestedStop = null;

                if (!upRequests.isEmpty()) {
                    // there are pending floor calls
                    for (FloorCall call : upRequests) {
                        System.out.println(color + "iterating elev " + this.elevatorNumber + " calls " + call.getStartFloor() + " " + call.getDestinationFloor());
                    }
                    nextStop = upRequests.get(0);
                    this.requestedFloorsUp.add(nextStop.getDestinationFloor());
                } else {
                    // there are no pending calls, only passenger requests from floor ie only integers
                    requestedStop = nextUpRequest;
                    System.out.println(color + " only passenger request");
                    this.requestedFloorsUp.remove(requestedStop);
                    return requestedStop;
                }

                if (nextStop == null) {
                    System.out.println(color + " no floors to go to - null");
                    return -1;
                    //nextStop.setStartFloor(this.requestedFloorsUp.get(0));
                } else if (nextStop == null && nextUpRequest != null) {
                    nextStop.setStartFloor(nextUpRequest);
                } else if (nextStop != null && nextUpRequest != null && nextUpRequest < nextStop.getStartFloor()) {
                    nextStop.setStartFloor(nextUpRequest);
                }

                System.out.println(color + "2----getnextstop " + nextStop.getStartFloor());
                upRequests.remove(nextStop);
                controlSystem.removeStop(nextStop.getStartFloor(), nextStop.getDestinationFloor(), "up");
                calledFloors.notifyAll();
                return nextStop.getStartFloor();

            } else if (!this.requestedFloorsDown.isEmpty()) {
                FloorCall nextStop = null;
                //todo - method to determine next floor down from current floor
                //Integer nextDownRequest = this.requestedFloorsDown.lower(this.currentFloor);
                Integer nextDownRequest = this.requestedFloorsDown.get(this.requestedFloorsDown.size()-1);

                Integer requestedStop = null;

                if (!downRequests.isEmpty()) {
                    // there are pending floor calls
                    nextStop = downRequests.get(downRequests.size()-1);
                    this.requestedFloorsDown.add(nextStop.getDestinationFloor());
                } else {
                    // there are no pending calls, only passenger requests from floor ie only integers
                    requestedStop = nextDownRequest;
                    System.out.println(color + " only passenger request");
                    this.requestedFloorsDown.remove(requestedStop);
                    return requestedStop;
                }

                if (nextStop == null) {
                    System.out.println(color + " no floors to go to - null");
                    return -1;
                } else if (nextStop == null && nextDownRequest != null) {
                    nextStop.setStartFloor(nextDownRequest);
                } else if (nextStop != null && nextDownRequest != null && nextDownRequest > nextStop.getStartFloor()) {
                    nextStop.setStartFloor(nextDownRequest);
                }

                System.out.println(color + "3----getnextstop " + nextStop.getStartFloor());
                downRequests.remove(nextStop);
                controlSystem.removeStop(nextStop.getStartFloor(), nextStop.getDestinationFloor(), "down");
                calledFloors.notifyAll();
                return nextStop.getStartFloor();

            } else {
                System.out.println(color + " got no next stop");
                return -1;
            }
        }
    }

    /*public int getNextStop() throws InterruptedException {
        List<FloorCall> calledFloors = controlSystem.getCalledFloors();
        TreeSet<FloorCall> upRequests = controlSystem.getUpRequests();
        TreeSet<FloorCall> downRequests = controlSystem.getDownRequests();


        synchronized (calledFloors) {
            while (upRequests.isEmpty() && downRequests.isEmpty() && this.requestedFloorsUp.isEmpty() && this.requestedFloorsDown.isEmpty()) {
                System.out.println(color + this.elevatorNumber + " is Waiting for a floor call..");
                calledFloors.wait();
            }
            System.out.println(color + this.elevatorNumber + " pressed up buttons " + this.requestedFloorsUp);
            System.out.println(color + this.elevatorNumber + " pressed down buttons " + this.requestedFloorsDown);

            if (this.requestedFloorsUp.isEmpty() && this.requestedFloorsDown.isEmpty()) {
                Integer nextStop = null;

                if (!upRequests.isEmpty()) {
                    nextStop = upRequests.first();
                    controlSystem.removeStop(nextStop, "up");
                } else {
                    nextStop = downRequests.last();
                    controlSystem.removeStop(nextStop, "down");
                }

                System.out.println(color + "getnextstop " + nextStop);
                calledFloors.notifyAll();
                return nextStop;
            } else if (!this.requestedFloorsUp.isEmpty()) {
                Integer nextStop = null;
                Integer nextUpRequest = this.requestedFloorsUp.higher(this.currentFloor);

                if (!upRequests.isEmpty()) {
                    nextStop = upRequests.first();
                }

                if (nextStop == null && nextUpRequest == null) {
                    nextStop = this.requestedFloorsUp.first();
                } else if (nextStop == null && nextUpRequest != null) {
                    nextStop = nextUpRequest;
                } else if (nextStop != null && nextUpRequest != null && nextUpRequest < nextStop) {
                    nextStop = nextUpRequest;
                }

                System.out.println(color + "getnextstop " + nextStop);
                controlSystem.removeStop(nextStop, "up");
                calledFloors.notifyAll();
                return nextStop;

            } else if (!this.requestedFloorsDown.isEmpty()) {
                Integer nextStop = null;
                Integer nextDownRequest = this.requestedFloorsDown.lower(this.currentFloor);

                if (!downRequests.isEmpty()) {
                    nextStop = downRequests.last();
                }

                if (nextStop == null && nextDownRequest == null) {
                    nextStop = this.requestedFloorsDown.last();
                } else if (nextStop == null && nextDownRequest != null) {
                    nextStop = nextDownRequest;
                } else if (nextStop != null && nextDownRequest != null && nextDownRequest > nextStop) {
                    nextStop = nextDownRequest;
                }

                System.out.println(color + "getnextstop " + nextStop);
                controlSystem.removeStop(nextStop, "down");
                calledFloors.notifyAll();
                return nextStop;

            } else {
                System.out.println(color + " got no next stop");
                return -1;
            }
        }
    }*/

    public void stop() {
        // reached floor that the request came from
        System.out.println(color + "Elevator " + this.elevatorNumber + " stopping on floor " + this.currentFloor);
        this.inMove = false;

        // pause elevator to let passengers out / in
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int floorButtonUp = 11;
        //int floorButtonDown = 1;

        // todo - unpress call request button on destination floor
        // passengers choose floor to go up or down
        //pressFloorButton(floorButtonUp);
        //pressFloorButton(floorButtonDown);

    }

    public boolean move(int destinationFloor) {
        // todo - should include list of floors (pressedbuttons)
        this.inMove = true;
        determineDirection();

        if (this.currentFloor != destinationFloor) {
            System.out.println(color + "Elevator " + this.elevatorNumber + " is starting to move from floor " + this.currentFloor);
        }

        while (true) {
            if (destinationFloor < this.currentFloor) {
                // 2 sec to reach next floor
                this.currentDirection = "DOWN";
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.currentFloor--;
                System.out.println(color + "Elevator " + this.elevatorNumber + " is going " + this.currentDirection + ", reached floor " + this.currentFloor);
            } else if (destinationFloor > this.currentFloor) {
                // 2 sec to reach next floor
                this.currentDirection = "UP";
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.currentFloor++;
                System.out.println(color + "Elevator " + this.elevatorNumber + " is going " + this.currentDirection + ", reached floor " + this.currentFloor);
            } else {
                // reached destination floor
                this.currentFloor = destinationFloor;
                stop();

                return true;
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
