package com.testreel.elevators;

public class Main {

    private static Building building = new Building();
    private static ControlSystem controlSystem = building.getControlSystem();

    public static void main(String[] args) throws InterruptedException {

        Thread t = Thread.currentThread();
        System.out.println("Current thread: " + t);
        t.setName("Main thread");

        building.generateFloors();
        //building.generateElevators();

        building.getElevators().add(0, new Elevator(1, 1, controlSystem, TheadColor.ANSI_BLUE));
        building.getElevators().add(1, new Elevator(2, 1, controlSystem, TheadColor.ANSI_GREEN));
        building.getElevators().add(2, new Elevator(3, 1, controlSystem, TheadColor.ANSI_PURPLE));


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

        for (int i = 0; i < controlSystem.getCalledFloors().size(); i++) {
            System.out.println(controlSystem.getCalledFloors().get(i).getFloorNumber());
            System.out.println(controlSystem.getCalledFloors().get(i).getDirection());
        }



        System.out.println("-------------");
        System.out.println(building.getFloors().get(1).isUpButtonPressed());
        System.out.println(building.getFloors().get(1).isDownButtonPressed());
        System.out.println(building.getFloors().get(2).isUpButtonPressed());


        System.out.println(building.getElevators().get(0).getThread());
        System.out.println(building.getElevators().get(1).getThread());
        System.out.println(building.getElevators().get(2).getThread());
    }

    public static void makeFloorCall(int floor, int direction) {
        controlSystem.addFloorCall(floor, direction);
    }

    public static void makeFloorCall(FloorCall call) {
        controlSystem.addFloorCall(call);
    }

}
