package app.simulation.elevator.system;

import app.simulation.elevator.model.Elevator;
import app.simulation.elevator.request.Request;

import java.util.List;

/**
 * The interface that should serve as an algorithm that arranges the elevator traffic
 */
public interface ElevatorSystem {

    /**
     * The method should register a given request for one of the specified elevators modifying its inside requests queue
     * @param elevators where the request will be registered
     * @param request that will be registered
     * @return the id of an elevator that was used to register the request
     */
    int register(List<Elevator> elevators, Request request);
}
