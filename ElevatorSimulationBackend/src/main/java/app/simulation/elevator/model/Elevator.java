package app.simulation.elevator.model;


import app.simulation.elevator.request.Request;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@Data
public class Elevator {

    public static int ELEVATOR_COUNTER = 0;

    private final int id;
    private List<Request> requests;
    private int currentFloor;

    /**
     *
     */
    public Elevator() {
        this.requests = new LinkedList<>();
        this.currentFloor = 0;
        this.id = ELEVATOR_COUNTER++;
    }

    /**
     * @return
     */
    public int getDestinationFloor() {
        for (Request request : requests) {
            int destination = request.getDestination();
            if (destination != currentFloor) return destination;
        }
        return currentFloor;
    }

    public int getLastFloorInDirection(Direction direction) {
        return switch (direction) {
            case DOWN -> requests.stream().mapToInt(Request::getDestination).min().orElseThrow();
            case UP -> requests.stream().mapToInt(Request::getDestination).max().orElseThrow();
            default -> currentFloor;
        };
    }

    /**
     * @return
     */
    public int getLastFloorInCurrentDirection() {
        return getLastFloorInDirection(getDirection());
    }

    /**
     * @return
     */
    public Direction getDirection() {
        return switch ((int) Math.signum(currentFloor - getDestinationFloor())) {
            case 1 -> Direction.DOWN;
            case -1 -> Direction.UP;
            default -> Direction.NOT_IN_MOVE;
        };
    }
}
