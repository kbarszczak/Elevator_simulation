package app.simulation.elevator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import app.simulation.Simulation;
import app.simulation.elevator.system.ElevatorSystem;
import app.simulation.elevator.model.Elevator;
import app.simulation.elevator.request.Request;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *
 */
@Setter
@Getter
@ToString
public class ElevatorSimulation implements Simulation {

    @ToString.Exclude
    private final Semaphore semaphore;
    private final List<Elevator> elevators;
    private ElevatorSystem system;

    /**
     * @param system
     * @param elevators
     */
    public ElevatorSimulation(ElevatorSystem system, List<Elevator> elevators) {
        this.semaphore = new Semaphore(1);
        this.system = system;
        this.elevators = elevators;
    }

    /**
     * @param request
     */
    public int register(Request request) {
        int result = 0;
        try {
            // block the threads that would like to modify elevators' request list
            semaphore.acquire();

            // otherwise, add the request to the proper elevator
            result = system.register(elevators, request);

            // release the resource
            semaphore.release();
        } catch (InterruptedException ignore) {
        }
        return result;
    }

    /**
     *
     */
    @Override
    public void step() {
        try {
            // block the elevators' request lists (to not get modified by register function
            semaphore.acquire();

            // loop over elevators
            for (Elevator elevator : elevators) {
                // get request list for each elevator
                List<Request> requests = elevator.getRequests();

                // if the request list is not empty
                if (!requests.isEmpty()) {
                    // get the first element from the queue (this is the current destination floor for the elevator)
                    Request request = requests.get(0);

                    // if the current desired floor was reached then delete its request from the queue
                    if (elevator.getCurrentFloor() == request.getDestination()) requests.remove(0);
                    else{
                        // decide if move the elevator up or down
                        int offset = request.getDestination() < elevator.getCurrentFloor() ? -1 : 1;

                        // set the new floor for the elevator
                        elevator.setCurrentFloor(elevator.getCurrentFloor() + offset);
                    }
                }
            }

            // release the resource
            semaphore.release();
        } catch (InterruptedException ignore) {
        }
    }

}