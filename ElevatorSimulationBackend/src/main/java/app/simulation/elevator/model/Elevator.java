package app.simulation.elevator.model;


import app.simulation.elevator.request.Request;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * The class that models elevator
 */
@Data
public class Elevator {

    public static int ELEVATOR_COUNTER = 0;

    private final int id;
    private List<Request> requests;
    private int currentFloor;

    /**
     * The constructor sets up the object with current floor set to 0, empty request queue and the id
     * set to the unique value
     */
    public Elevator() {
        this.requests = new LinkedList<>();
        this.currentFloor = 0;
        this.id = ELEVATOR_COUNTER++;
    }

    /**
     * The method returns the floor where the elevator is going to stop
     * @return the next elevator stop (for request queue 1 -> 5 -> 9 -> 0 the method will return 1)
     */
    public int getDestinationFloor() {
        for (Request request : requests) {
            int destination = request.getDestination();
            if (destination != currentFloor) return destination;
        }
        return currentFloor;
    }

    /**
     * The method returns the last floor from the request queue in a given direction
     * @param direction of seeking the floor
     * @return the last floor in a given direction (for request queue 1 -> 5 -> 9 -> 0 and direction UP the method will return 9)
     */
    public int getLastFloorInDirection(Direction direction) {
        return switch (direction) {
            case DOWN -> requests.stream().mapToInt(Request::getDestination).min().orElseThrow();
            case UP -> requests.stream().mapToInt(Request::getDestination).max().orElseThrow();
            default -> currentFloor;
        };
    }

    /**
     * @return the last floor in current elevator direction
     */
    public int getLastFloorInCurrentDirection() {
        return getLastFloorInDirection(getDirection());
    }

    /**
     * @return the enum value that describes the elevator movement
     */
    public Direction getDirection() {
        return switch ((int) Math.signum(currentFloor - getDestinationFloor())) {
            case 1 -> Direction.DOWN;
            case -1 -> Direction.UP;
            default -> Direction.NOT_IN_MOVE;
        };
    }
}
