package com.testreel.elevators;

import java.util.ArrayList;
import java.util.List;

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

        this.status = ElevatorStatus.STOPPED;
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

    public FloorCall getNextStop() throws InterruptedException {
        List<FloorCall> calledFloors = controlSystem.getCalledFloors();

        synchronized (calledFloors) {
            while (calledFloors.isEmpty()) {
                System.out.println(color + this.elevatorNumber + " is Waiting for a floor call..");
                calledFloors.wait();
                //return null;
            }
            FloorCall call = calledFloors.get(0);
            System.out.println(color + "getnextstop " + call.getFloorNumber());
            controlSystem.removeFloorCall(call);
            calledFloors.notifyAll();
            return call;
        }
    }

    public void move(FloorCall call) {
        // todo - should include list of floors (pressedbuttons)
        this.inMove = true;
        int destinationFloor = call.getFloorNumber();
        if (move(destinationFloor)) {
            // unpress call request button on destination floor
            Floor current = controlSystem.getFloors().get(destinationFloor-1);
            if (call.getDirection() == 1) {
                current.setUpButtonPressed(false);
            } else if (call.getDirection() == 0) {
                current.setDownButtonPressed(false);
            } else {
                System.out.println(color + "Unrecognized floor call direction");
            }
        }
    }

    public boolean move(int destinationFloor) {
        // todo - should include list of floors (pressedbuttons)

        while (true) {
            if (destinationFloor < this.currentFloor) {
                System.out.println(color + "Elevator " + this.elevatorNumber + " is moving down, reached floor " + this.currentFloor);
                // 2 sec to reach next floor
                this.status = ElevatorStatus.GOING_DOWN;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.currentFloor--;
            } else if (destinationFloor > this.currentFloor) {
                System.out.println(color + "Elevator " + this.elevatorNumber + " is moving up, reached floor " + this.currentFloor);
                // 2 sec to reach next floor
                this.status = ElevatorStatus.GOING_UP;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.currentFloor++;
            } else {
                // reached destination floor
                this.currentFloor = destinationFloor;
                System.out.println(color + "Elevator " + this.elevatorNumber + " stopping on floor " + this.currentFloor);
                this.inMove = false;
                this.status = ElevatorStatus.STOPPED;
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
                FloorCall nextStop = getNextStop();
                System.out.println(color + "Elevator " + elevatorNumber + " is servicing next request to floor " + nextStop.getFloorNumber());
                move(nextStop);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
