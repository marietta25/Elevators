package com.testreel.elevators;

public class Main {

    private static Building building = new Building();
    private static ControlSystem controlSystem = building.getControlSystem();

    public static void main(String[] args) throws InterruptedException {

        // generate floors and elevators
        building.generateFloors();
        building.generateElevators();

        // make some floor calls before starting the elevators
        makeFloorCall(2, 6);
        makeFloorCall(2, 7);
        makeFloorCall(5, 3);
        makeFloorCall(10, 12);

        // start elevators
        for (int i = 0; i < building.getElevators().size(); i++) {
            Elevator current = building.getElevators().get(i);
            current.getThread().start();
        }

        // wait some time and make new floor calls
        Thread.sleep(3000);
        makeFloorCall(3, 1);

        Thread.sleep(1000);
        makeFloorCall(13, 8);

        Thread.sleep(500);
        makeFloorCall(8, 1);

        Thread.sleep(5000);
        makeFloorCall(4, 9);

    }

    private static void makeFloorCall(int fromFloor, int toFloor) {
        int direction;
        if (fromFloor > 13 || fromFloor < 1 || toFloor > 13 || toFloor < 1) {
            System.out.println("Invalid floor");
            return;
        }
        if (fromFloor > toFloor) {
            direction = 0;
        } else if (fromFloor < toFloor) {
            direction = 1;
        } else {
            System.out.println("Already on floor " + toFloor);
            return;
        }
        FloorCall newCall = controlSystem.makeFloorCall(fromFloor, toFloor, direction);
        if (!controlSystem.addFloorCall(newCall)) {
            System.out.println("Could not add floor request to call stack");
        }
    }
}
