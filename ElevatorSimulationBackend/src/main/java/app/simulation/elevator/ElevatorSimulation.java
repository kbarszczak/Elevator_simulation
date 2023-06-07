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
 * The class implements a Simulation interface and simulates the movement of the elevator
 */
@Setter
@Getter
@ToString
public class ElevatorSimulation implements Simulation {

    @ToString.Exclude
    private final Semaphore semaphore;
    private final List<Elevator> elevators;
    private final int floors;
    private ElevatorSystem system;

    /**
     * The constructor sets up the ElevatorSimulation
     * @param system is the algorithm that is responsible for arranging the movement of the elevators
     * @param elevators that are used by the simulation
     */
    public ElevatorSimulation(ElevatorSystem system, List<Elevator> elevators, int floors) {
        this.semaphore = new Semaphore(1);
        this.system = system;
        this.elevators = elevators;
        this.floors = floors;
    }

    /**
     * The method registers elevator requests. This method is a blocking method. Its blocks the thread
     * until the step method is released
     * @param request that will be registered. Either PickupRequest or ElevateRequest
     */
    public int register(Request request) {
        int result = 0;
        if(request.getDestination() > floors) throw new IllegalArgumentException(String.format("Cannot go to floor '%d' because the simulation has only '%d' floors", request.getDestination(), floors));

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
     * The method executes one step of the simulation. Same as the register method this method is also blocking.
     * It blocks the thread until the register method is released
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
