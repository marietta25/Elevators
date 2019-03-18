package com.testreel.elevators;

import java.util.*;

import static java.lang.Thread.interrupted;

public class Elevator implements Runnable {

    private int elevatorNumber;
    private int currentFloor;
    private boolean inMove;
    private ElevatorStatus status;
    private List<Integer> selectedFloors;
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

        this.status = ElevatorStatus.GOING_UP;
        this.selectedFloors = new ArrayList<>();
        this.controlSystem = controlSystem;
        this.thread = new Thread(this,"Elevator " + this.elevatorNumber);
        this.color = color;
    }

    public boolean pressButton(int floor) {
        if (floor < 1 || floor > 13) {
            System.out.println(color + "Invalid floor");
            return false;
        }
        if (floor == this.currentFloor) {
            System.out.println(color + "Already on " + floor + " floor");
            return false;
        }

        // todo - add and sort pressed buttons in order
        this.selectedFloors.add(floor);
        System.out.println(color + "Pressed button to stop at floor " + floor);
        return true;
    }

    public int getNextStop() throws InterruptedException {
        List<FloorCall> calledFloors = controlSystem.getCalledFloors();
        TreeSet<Integer> upRequests = controlSystem.getUpRequests();
        TreeSet<Integer> downRequests = controlSystem.getDownRequests();
        Iterator<Integer> i = upRequests.iterator();

        if (this.status == ElevatorStatus.GOING_UP) {
            // see if there are more requests to go up
            synchronized (upRequests) {
                while (upRequests.isEmpty()) {
                    System.out.println(color + this.elevatorNumber + " is Waiting for a floor call..");
                    upRequests.wait();
                    //return null;
                }

                int nextStop = upRequests.first();
                System.out.println(color + "getnextstop " + nextStop);
                controlSystem.removeStop(nextStop, "up");
                upRequests.notifyAll();
                return nextStop;
            }
        }
        if (this.status == ElevatorStatus.GOING_DOWN || this.status == ElevatorStatus.STOPPED) {
            // see if there are more requests to go down
            synchronized (downRequests) {
                while (downRequests.isEmpty()) {
                    System.out.println(color + this.elevatorNumber + " is Waiting for a floor call..");
                    downRequests.wait();
                    //return null;
                }

                int nextStop = downRequests.last();
                System.out.println(color + "getnextstop " + nextStop);
                controlSystem.removeStop(nextStop, "down");
                downRequests.notifyAll();
                return nextStop;
            }
        } else {
            // elevator is currently not moving
            System.out.println(color + "Elevator " + this.elevatorNumber + " Not sure what to do");
            return -1;
        }
    }

    public void stop() {
        System.out.println(color + "Elevator " + this.elevatorNumber + " stopping on floor " + this.currentFloor);
        this.inMove = false;
        this.status = ElevatorStatus.STOPPED;

        // reached floor that the request came from
        // todo - unpress call request button on destination floor
    }

    public boolean move(int destinationFloor) {
        // todo - should include list of floors (pressedbuttons)
        this.inMove = true;

        if (this.currentFloor != destinationFloor) {
            System.out.println(color + "Elevator " + this.elevatorNumber + " is starting to move from floor " + this.currentFloor);
        }

        while (true) {
            if (destinationFloor < this.currentFloor) {
                // 2 sec to reach next floor
                this.status = ElevatorStatus.GOING_DOWN;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.currentFloor--;
                System.out.println(color + "Elevator " + this.elevatorNumber + " is moving down, reached floor " + this.currentFloor);
            } else if (destinationFloor > this.currentFloor) {
                // 2 sec to reach next floor
                this.status = ElevatorStatus.GOING_UP;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.currentFloor++;
                System.out.println(color + "Elevator " + this.elevatorNumber + " is moving up, reached floor " + this.currentFloor);
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

    public ElevatorStatus getStatus() {
        return status;
    }

    public boolean isInMove() {
        return inMove;
    }

    public void setStatus(ElevatorStatus status) {
        this.status = status;
    }

    public List<Integer> getSelectedFloors() {
        return selectedFloors;
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
