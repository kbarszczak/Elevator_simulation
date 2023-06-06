package app.simulation.elevator.system;

import app.simulation.elevator.model.Elevator;
import app.simulation.elevator.request.Request;

import java.util.List;

/**
 *
 */
public interface ElevatorSystem {

    /**
     * @param elevators
     * @param request
     * @return
     */
    int register(List<Elevator> elevators, Request request);
}
