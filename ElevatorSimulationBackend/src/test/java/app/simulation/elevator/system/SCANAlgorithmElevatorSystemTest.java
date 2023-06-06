package app.simulation.elevator.system;

import app.simulation.elevator.model.Direction;
import app.simulation.elevator.model.Elevator;
import app.simulation.elevator.request.ElevateRequest;
import app.simulation.elevator.request.PickupRequest;
import app.simulation.elevator.request.Request;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SCANAlgorithmElevatorSystemTest {

    @Test
    void distanceTestNotInMove() {
        Elevator e1 = new Elevator();
        e1.setCurrentFloor(1);

        Elevator e2 = new Elevator();
        e2.setCurrentFloor(10);

        assertEquals(5, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(6, Direction.UP)));
        assertEquals(9, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(10, Direction.UP)));

        assertEquals(9, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(1, Direction.DOWN)));
        assertEquals(0, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(10, Direction.DOWN)));
    }

    @Test
    void distanceTestSameDirectionOnWay() {
        Elevator e1 = new Elevator();
        e1.setCurrentFloor(1);
        e1.setRequests(List.of(new ElevateRequest(10, 1, e1.getId())));

        Elevator e2 = new Elevator();
        e2.setCurrentFloor(10);
        e2.setRequests(List.of(new ElevateRequest(1, 10, e2.getId())));

        assertEquals(4, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(5, Direction.UP)));
        assertEquals(7, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(8, Direction.UP)));
        assertEquals(8, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(9, Direction.UP)));

        assertEquals(2, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(8, Direction.DOWN)));
        assertEquals(8, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(2, Direction.DOWN)));
        assertEquals(1, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(9, Direction.DOWN)));
    }

    @Test
    void distanceTestSameDirectionNotOnWay() {
        Elevator e1 = new Elevator();
        // 5 -> 10
        e1.setCurrentFloor(5);
        e1.setRequests(List.of(new ElevateRequest(10, 5, e1.getId())));
        e1.setRequests(List.of(new ElevateRequest(13, 5, e1.getId())));

        Elevator e2 = new Elevator();
        // 5 -> 1
        e2.setCurrentFloor(5);
        e2.setRequests(List.of(new ElevateRequest(2, 5, e2.getId())));
        e2.setRequests(List.of(new ElevateRequest(1, 5, e2.getId())));

        assertEquals(19, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(2, Direction.UP)));
        assertEquals(17, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(4, Direction.UP)));
        assertEquals(21, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(0, Direction.UP)));

        assertEquals(11, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(8, Direction.DOWN)));
        assertEquals(9, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(6, Direction.DOWN)));
        assertEquals(12, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(9, Direction.DOWN)));
    }

    @Test
    void distanceTestDifferentDirectionOnWay() {
        Elevator e1 = new Elevator();
        // 7 -> 12 up
        e1.setCurrentFloor(7);
        e1.setRequests(List.of(new ElevateRequest(10, 7, e1.getId())));
        e1.setRequests(List.of(new ElevateRequest(12, 7, e1.getId())));

        Elevator e2 = new Elevator();
        // 7 -> 2 down
        e2.setCurrentFloor(7);
        e2.setRequests(List.of(new ElevateRequest(5, 7, e2.getId())));
        e2.setRequests(List.of(new ElevateRequest(2, 7, e2.getId())));

        assertEquals(9, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(8, Direction.DOWN)));
        assertEquals(7, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(10, Direction.DOWN)));
        assertEquals(6, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(11, Direction.DOWN)));

        assertEquals(9, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(6, Direction.UP)));
        assertEquals(8, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(5, Direction.UP)));
        assertEquals(6, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(3, Direction.UP)));
    }

    @Test
    void distanceTestDifferentDirectionNotOnWay() {
        Elevator e1 = new Elevator();
        // 7 -> 12 up
        e1.setCurrentFloor(7);
        e1.setRequests(List.of(new ElevateRequest(10, 7, e1.getId())));
        e1.setRequests(List.of(new ElevateRequest(12, 7, e1.getId())));

        Elevator e2 = new Elevator();
        // 7 -> 2 down
        e2.setCurrentFloor(7);
        e2.setRequests(List.of(new ElevateRequest(5, 7, e2.getId())));
        e2.setRequests(List.of(new ElevateRequest(2, 7, e2.getId())));

        assertEquals(13, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(4, Direction.DOWN)));
        assertEquals(16, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(1, Direction.DOWN)));
        assertEquals(14, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(3, Direction.DOWN)));

        assertEquals(11, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(8, Direction.UP)));
        assertEquals(12, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(9, Direction.UP)));
        assertEquals(15, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(12, Direction.UP)));
    }

    @Test
    void distanceTestDifferentDirectionOutOfRange() {
        Elevator e1 = new Elevator();
        // 7 -> 12 up
        e1.setCurrentFloor(1);
        e1.setRequests(List.of(new ElevateRequest(10, 1, e1.getId())));
        e1.setRequests(List.of(new ElevateRequest(12, 1, e1.getId())));

        Elevator e2 = new Elevator();
        // 7 -> 2 down
        e2.setCurrentFloor(10);
        e2.setRequests(List.of(new ElevateRequest(8, 10, e2.getId())));

        assertEquals(15, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(8, Direction.DOWN)));
        assertEquals(13, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(10, Direction.DOWN)));
        assertEquals(14, SCANAlgorithmElevatorSystem.calculateDistance(e1, new PickupRequest(15, Direction.DOWN)));

        assertEquals(5, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(5, Direction.DOWN)));
        assertEquals(3, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(9, Direction.UP)));
        assertEquals(9, SCANAlgorithmElevatorSystem.calculateDistance(e2, new PickupRequest(1, Direction.UP)));
    }

    @Test
    void pickElevatorTest() {
        Elevator e1 = new Elevator(); // up
        e1.setCurrentFloor(2);
        e1.setRequests(List.of(new ElevateRequest(10, 2, e1.getId())));

        Elevator e2 = new Elevator(); // not in move
        e2.setCurrentFloor(10);

        Elevator e3 = new Elevator(); // down
        e3.setCurrentFloor(12);
        e3.setRequests(List.of(new ElevateRequest(1, 12, e3.getId())));

        Elevator e4 = new Elevator(); // down
        e4.setCurrentFloor(5);
        e4.setRequests(List.of(new ElevateRequest(0, 5, e4.getId())));

        Elevator e5 = new Elevator(); // up
        e5.setCurrentFloor(7);
        e5.setRequests(List.of(new ElevateRequest(10, 7, e5.getId())));

        List<Elevator> elevators = List.of(e1, e2, e3, e4, e5);

        Elevator e1p = SCANAlgorithmElevatorSystem.pickElevator(elevators, new PickupRequest(1, Direction.UP)); // dif on way
        Elevator e2p = SCANAlgorithmElevatorSystem.pickElevator(elevators, new PickupRequest(7, Direction.UP)); // same on way
        Elevator e3p = SCANAlgorithmElevatorSystem.pickElevator(elevators, new PickupRequest(6, Direction.DOWN)); // not in move
        Elevator e4p = SCANAlgorithmElevatorSystem.pickElevator(elevators, new PickupRequest(1, Direction.DOWN)); // same on way
        Elevator e5p = SCANAlgorithmElevatorSystem.pickElevator(elevators, new PickupRequest(3, Direction.UP)); // same on way

        assertSame(e4, e1p);
        assertSame(e5, e2p);
        assertSame(e2, e3p);
        assertSame(e4, e4p);
        assertSame(e1, e5p);
    }

    @Test
    void registerWith1EmptyElevator() {
        Elevator e = new Elevator();
        e.setCurrentFloor(1);

        SCANAlgorithmElevatorSystem system = new SCANAlgorithmElevatorSystem();
        List<Elevator> elevators = new LinkedList<>(List.of(e));

        int elevatorId;
        elevatorId = system.register(elevators, new PickupRequest(2, Direction.UP));
        elevatorId = system.register(elevators, new PickupRequest(3, Direction.DOWN));
        elevatorId = system.register(elevators, new ElevateRequest(5, 2, elevatorId));
        elevatorId = system.register(elevators, new PickupRequest(7, Direction.UP));
        elevatorId = system.register(elevators, new ElevateRequest(8, 7, elevatorId));
        elevatorId = system.register(elevators, new ElevateRequest(0, 3, elevatorId));
        elevatorId = system.register(elevators, new PickupRequest(1, Direction.DOWN));
        elevatorId = system.register(elevators, new ElevateRequest(0, 1, elevatorId));
        elevatorId = system.register(elevators, new PickupRequest(10, Direction.DOWN));
        elevatorId = system.register(elevators, new ElevateRequest(8, 10, elevatorId));
        elevatorId = system.register(elevators, new ElevateRequest(3, 10, elevatorId));
        elevatorId = system.register(elevators, new PickupRequest(5, Direction.UP));
        elevatorId = system.register(elevators, new ElevateRequest(10, 5, elevatorId));

        assertEquals(String.format("id=%d dir=UP cf=1 r=[2, 5, 7, 8, 10, 8, 3, 1, 0]", e.getId()), elevatorRequestsToString(e));
    }

    @Test
    void registerWith1EmptyElevatorWithReflectionUp() {
        Elevator e = new Elevator();
        e.setCurrentFloor(5);

        SCANAlgorithmElevatorSystem system = new SCANAlgorithmElevatorSystem();
        List<Elevator> elevators = new LinkedList<>(List.of(e));

        int elevatorId;
        elevatorId = system.register(elevators, new PickupRequest(5, Direction.UP));
        elevatorId = system.register(elevators, new ElevateRequest(8, 5, elevatorId));
        elevatorId = system.register(elevators, new ElevateRequest(10, 5, elevatorId));
        elevatorId = system.register(elevators, new PickupRequest(11, Direction.DOWN));
        elevatorId = system.register(elevators, new ElevateRequest(0, 11, elevatorId));
        elevatorId = system.register(elevators, new PickupRequest(2, Direction.UP));
        elevatorId = system.register(elevators, new ElevateRequest(4, 2, elevatorId));

        assertEquals(String.format("id=%d dir=UP cf=5 r=[5, 8, 10, 11, 0, 2, 4]", e.getId()), elevatorRequestsToString(e));
    }

    @Test
    void registerWith1EmptyElevatorWithReflectionDown() {
        Elevator e = new Elevator();
        e.setCurrentFloor(5);

        SCANAlgorithmElevatorSystem system = new SCANAlgorithmElevatorSystem();
        List<Elevator> elevators = new LinkedList<>(List.of(e));

        int elevatorId;
        elevatorId = system.register(elevators, new PickupRequest(3, Direction.DOWN));
        elevatorId = system.register(elevators, new ElevateRequest(2, 3, elevatorId));
        elevatorId = system.register(elevators, new ElevateRequest(0, 3, elevatorId));
        elevatorId = system.register(elevators, new PickupRequest(1, Direction.UP));
        elevatorId = system.register(elevators, new ElevateRequest(10, 1, elevatorId));
        elevatorId = system.register(elevators, new PickupRequest(10, Direction.DOWN));
        elevatorId = system.register(elevators, new ElevateRequest(7, 10, elevatorId));
        elevatorId = system.register(elevators, new ElevateRequest(6, 10, elevatorId));

        assertEquals(String.format("id=%d dir=DOWN cf=5 r=[3, 2, 0, 1, 10, 7, 6]", e.getId()), elevatorRequestsToString(e));
    }

    @Test
    void registerWith2EmptyElevators() {
        Elevator e1 = new Elevator();
        Elevator e2 = new Elevator();

        e1.setCurrentFloor(6);
        e2.setCurrentFloor(1);

        SCANAlgorithmElevatorSystem system = new SCANAlgorithmElevatorSystem();
        List<Elevator> elevators = new LinkedList<>(List.of(e1, e2));

        int eid1 = system.register(elevators, new PickupRequest(0, Direction.UP));
        int eid2 = system.register(elevators, new PickupRequest(4, Direction.UP));
        int eid3 = system.register(elevators, new PickupRequest(2, Direction.DOWN));
        int eid1e = system.register(elevators, new ElevateRequest(6, 0, eid1));
        int eid3e = system.register(elevators, new ElevateRequest(1, 2, eid3));
        int eid2e1 = system.register(elevators, new ElevateRequest(6, 4, eid2));
        int eid2e2 = system.register(elevators, new ElevateRequest(1, 4, eid2));

        assertEquals(eid1, eid1e);
        assertEquals(eid2, eid2e1);
        assertEquals(eid2, eid2e2);
        assertEquals(eid3, eid3e);
        assertEquals(String.format("id=%d dir=DOWN cf=6 r=[1, 4, 6]", e1.getId()), elevatorRequestsToString(e1));
        assertEquals(String.format("id=%d dir=DOWN cf=1 r=[1, 0, 6, 2]", e2.getId()), elevatorRequestsToString(e2));
    }

    static String elevatorRequestsToString(Elevator elevator) {
        return String.format("id=%d dir=%s cf=%d r=%s",
                elevator.getId(),
                elevator.getDirection(),
                elevator.getCurrentFloor(),
                elevator.getRequests().stream().map(Request::getDestination).toList()
        );
    }
}