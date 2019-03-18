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

        //building.getElevators().get(0).setStatus(ElevatorStatus.GOING_DOWN);
        //building.getElevators().get(1).setStatus(ElevatorStatus.GOING_DOWN);

        makeFloorCall(2, 1);
        makeFloorCall(13, 0);
        makeFloorCall(1, 0);
        makeFloorCall(13, 1);
        makeFloorCall(5, 1);

        makeFloorCall(3, 0);

        t.sleep(10000);
        makeFloorCall(7, 1);

        t.sleep(5000);
        makeFloorCall(4, 0);


        System.out.println("down req " + controlSystem.getDownRequests());
        System.out.println("up req " +controlSystem.getUpRequests());



        System.out.println("-------------");
        System.out.println(building.getFloors().get(1).isUpButtonPressed());
        System.out.println(building.getFloors().get(1).isDownButtonPressed());
        System.out.println(building.getFloors().get(2).isUpButtonPressed());

    }

    public static void makeFloorCall(int floor, int direction) {
        FloorCall newCall = controlSystem.makeFloorCall(floor, direction);
        controlSystem.addFloorCall(newCall);
    }

//    public static void makeFloorCall(FloorCall call) {
//        controlSystem.addFloorCall(call);
//    }

}
