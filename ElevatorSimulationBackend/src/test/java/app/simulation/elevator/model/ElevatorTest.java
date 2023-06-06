package app.simulation.elevator.model;

import app.simulation.elevator.request.ElevateRequest;
import app.simulation.elevator.request.PickupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorTest {

    private Elevator e1;
    private Elevator e2;
    private Elevator e3;
    private Elevator e4;
    private Elevator e5;

    @BeforeEach
    void setUp(TestInfo info) {
        if (info.getDisplayName().equals("getLastFloorInCurrentDirectionWithReflection()")) return;

        e1 = new Elevator();
        e2 = new Elevator();
        e3 = new Elevator();
        e4 = new Elevator();
        e5 = new Elevator();

        // going only down
        e1.setCurrentFloor(5);
        e1.setRequests(List.of(
                new PickupRequest(3, Direction.DOWN),
                new ElevateRequest(1, 3, e1.getId()),
                new PickupRequest(1, Direction.DOWN),
                new ElevateRequest(0, 1, e1.getId())
        ));

        // going first up then down
        e2.setCurrentFloor(2);
        e2.setRequests(List.of(
                new PickupRequest(3, Direction.UP),
                new ElevateRequest(5, 3, e2.getId()),
                new ElevateRequest(8, 3, e2.getId()),
                new PickupRequest(9, Direction.DOWN),
                new ElevateRequest(2, 9, e2.getId()),
                new ElevateRequest(0, 9, e2.getId())
        ));

        // going only up
        e3.setCurrentFloor(1);
        e3.setRequests(List.of(
                new PickupRequest(7, Direction.UP),
                new ElevateRequest(8, 7, e3.getId()),
                new PickupRequest(9, Direction.UP),
                new ElevateRequest(10, 9, e3.getId())
        ));

        // going first down then up
        e4.setCurrentFloor(7);
        e4.setRequests(List.of(
                new PickupRequest(10, Direction.DOWN),
                new ElevateRequest(2, 10, e4.getId()),
                new ElevateRequest(1, 10, e4.getId()),
                new PickupRequest(0, Direction.UP),
                new ElevateRequest(5, 0, e4.getId()),
                new ElevateRequest(7, 0, e4.getId())
        ));

        // not moving
        e5.setCurrentFloor(3);
    }

    @Test
    void getDestinationFloor() {
        assertEquals(3, e1.getDestinationFloor());
        assertEquals(3, e2.getDestinationFloor());
        assertEquals(7, e3.getDestinationFloor());
        assertEquals(10, e4.getDestinationFloor());
        assertEquals(3, e5.getDestinationFloor());
    }

    @Test
    void getLastFloorInCurrentDirection() {
        assertEquals(0, e1.getLastFloorInCurrentDirection());
        assertEquals(9, e2.getLastFloorInCurrentDirection());
        assertEquals(10, e3.getLastFloorInCurrentDirection());
        assertEquals(10, e4.getLastFloorInCurrentDirection());
        assertEquals(3, e5.getLastFloorInCurrentDirection());
    }

    @Test
    void getLastFloorInCurrentDirectionWithReflection() {
        Elevator e = new Elevator();
        e.setCurrentFloor(5);
        e.setRequests(List.of(
                new PickupRequest(6, Direction.UP),
                new ElevateRequest(9, 6, e.getId()),
                new ElevateRequest(10, 6, e.getId()),
                new PickupRequest(7, Direction.DOWN),
                new ElevateRequest(2, 7, e.getId()),
                new ElevateRequest(0, 7, e.getId()),
                new PickupRequest(2, Direction.UP),
                new ElevateRequest(4, 2, e.getId())
        ));
        assertEquals(10, e.getLastFloorInCurrentDirection());
    }

    @Test
    void getDirection() {
        assertEquals(Direction.DOWN, e1.getDirection());
        assertEquals(Direction.UP, e2.getDirection());
        assertEquals(Direction.UP, e3.getDirection());
        assertEquals(Direction.UP, e4.getDirection());
        assertEquals(Direction.NOT_IN_MOVE, e5.getDirection());
    }
}