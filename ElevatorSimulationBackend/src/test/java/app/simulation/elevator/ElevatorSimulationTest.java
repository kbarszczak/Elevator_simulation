package app.simulation.elevator;

import app.simulation.elevator.model.Direction;
import app.simulation.elevator.model.Elevator;
import app.simulation.elevator.request.PickupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorSimulationTest {

    private ElevatorSimulation simulation;

    @BeforeEach
    void setUp() {
        Elevator e1 = new Elevator();
        e1.setCurrentFloor(7);
        e1.setRequests(new LinkedList<>(List.of(
                new PickupRequest(10, Direction.DOWN),
                new PickupRequest(2, Direction.UP)

        )));

        simulation = new ElevatorSimulation(
                (elevators, request) -> {
                    elevators.get(0).getRequests().remove(0);
                    return 0;
                },
                List.of(e1),
                20
        );
    }

    @Test
    void registerAndStepBlock1() {
        Thread t1 = new Thread(
                () -> simulation.register(new PickupRequest(2, Direction.UP))
        );

        Thread t2 = new Thread(
                () -> simulation.step()
        );

        try {
            t1.start();
            Thread.sleep(5);
            t2.start();

            t1.join();
            t2.join();
        } catch (InterruptedException ignore) {
        }

        assertEquals(6, simulation.getElevators().get(0).getCurrentFloor());
    }

    @Test
    void registerAndStepBlock2() {
        Thread t1 = new Thread(
                () -> simulation.register(null)
        );

        Thread t2 = new Thread(
                () -> simulation.step()
        );

        try {
            t2.start();
            Thread.sleep(5);
            t1.start();

            t1.join();
            t2.join();
        } catch (InterruptedException ignore) {
        }

        assertEquals(8, simulation.getElevators().get(0).getCurrentFloor());
    }

}