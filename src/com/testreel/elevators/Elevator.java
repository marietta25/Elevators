package com.testreel.elevators;

import java.util.*;

import static java.lang.Thread.interrupted;

public class Elevator implements Runnable {

    private int elevatorNumber;
    private int currentFloor;
    private boolean inMove;
    private String currentDirection;
    private TreeSet<Integer> requestedFloorsUp;
    private TreeSet<Integer> requestedFloorsDown;
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
        this.requestedFloorsUp = new TreeSet<>();
        this.requestedFloorsDown = new TreeSet<>();
        this.controlSystem = controlSystem;
        this.thread = new Thread(this,"Elevator " + this.elevatorNumber);
        this.color = color;
    }

    public boolean pressFloorButton(int floor) {
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
    }

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
        List<FloorCall> calledFloors = controlSystem.getCalledFloors();
        TreeSet<Integer> upRequests = controlSystem.getUpRequests();
        TreeSet<Integer> downRequests = controlSystem.getDownRequests();


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
    }

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
        pressFloorButton(floorButtonUp);
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

    public TreeSet<Integer> getRequestedFloorsUp() {
        return requestedFloorsUp;
    }

    public TreeSet<Integer> getRequestedFloorsDown() {
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
