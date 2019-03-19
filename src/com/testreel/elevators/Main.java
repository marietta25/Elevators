package com.testreel.elevators;

public class Main {

    private static Building building = new Building();
    private static ControlSystem controlSystem = building.getControlSystem();

    public static void main(String[] args) throws InterruptedException {

        Thread t = Thread.currentThread();
        System.out.println("Current thread: " + t);
        t.setName("Main thread");

        building.generateFloors();
        building.generateElevators();


        // start elevators
        for (int i = 0; i < building.getElevators().size(); i++) {
            Elevator current = building.getElevators().get(i);
            current.getThread().start();
        }

        t.sleep(5000); // wait for the elevators to start before making floor calls

        makeFloorCall(2, 6);
        makeFloorCall(13, 8);
        //makeFloorCall(1, 0);
        //makeFloorCall(13, 1);
        makeFloorCall(5, 3);

        //makeFloorCall(3, 0);

        //t.sleep(10000);
        //makeFloorCall(7, 1);

        t.sleep(5000);
        makeFloorCall(4, 1);


        System.out.println("down req " + controlSystem.getDownRequests());
        System.out.println("up req " +controlSystem.getUpRequests());



        System.out.println("-------------");
//        System.out.println(building.getFloors().get(1).isUpButtonPressed());
//        System.out.println(building.getFloors().get(1).isDownButtonPressed());
//        System.out.println(building.getFloors().get(2).isUpButtonPressed());

    }

    public static void makeFloorCall(int fromFloor, int toFloor) {
        int direction;
        if (fromFloor > toFloor) {
            direction = 0;
        } else if (fromFloor < toFloor) {
            direction = 1;
        } else {
            System.out.println("Already on floor " + toFloor);
            return;
        }
        FloorCall newCall = controlSystem.makeFloorCall(fromFloor, toFloor, direction);
        controlSystem.addFloorCall(newCall);
    }

}
