package com.testreel.elevators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Building {

    private List<Elevator> elevators;
    private List<Floor> floors;
    private ControlSystem controlSystem;

    public Building() {
        this.elevators = new ArrayList<>();
        this.floors = new LinkedList<>();
        this.controlSystem = new ControlSystem(this.elevators, this.floors);
    }

    public void generateElevators() {
        // at the start, place 3 elevators on different floors and give them differently colored outputs (in intelliJ)
        this.elevators.add(0, new Elevator(1, 1, controlSystem, ThreadColor.ANSI_BLUE));
        this.elevators.add(1, new Elevator(2, 6, controlSystem, ThreadColor.ANSI_GREEN));
        this.elevators.add(2, new Elevator(3, 12, controlSystem, ThreadColor.ANSI_PURPLE));
    }

    public void generateFloors() {
        // generate 13 floors for the building
        for (int i = 0; i < 13; i++) {
            this.floors.add(i, new Floor(i+1));
        }
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public void setElevators(List<Elevator> elevators) {
        this.elevators = elevators;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    public ControlSystem getControlSystem() {
        return controlSystem;
    }
}
