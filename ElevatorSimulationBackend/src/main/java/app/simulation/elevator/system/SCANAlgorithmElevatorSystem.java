package app.simulation.elevator.system;

import app.simulation.elevator.model.Direction;
import lombok.ToString;
import app.simulation.elevator.model.Elevator;
import app.simulation.elevator.request.ElevateRequest;
import app.simulation.elevator.request.PickupRequest;
import app.simulation.elevator.request.Request;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@ToString
public class SCANAlgorithmElevatorSystem implements ElevatorSystem {

    /**
     * @param elevators
     * @param request
     * @return
     */
    @Override
    public int register(List<Elevator> elevators, Request request) {
        // empty result
        Optional<Integer> elevatorId = Optional.empty();

        // check the request object
        if (request instanceof PickupRequest pickupRequest) {
            // check if any of the elevators has that request registered
            Elevator elevator = elevators
                    .stream()
                    .filter(x ->
                            x.getRequests().stream().anyMatch(y ->
                                    y.equals(pickupRequest)
                                            || (y.getDestination() == pickupRequest.getDestination() && y.getDirection() == pickupRequest.getDirection())
                                            || (x.getLastFloorInDirection(y.getDirection()) == pickupRequest.getDestination())
                            )
                    )
                    .findFirst()
                    .orElseGet(() -> {
                        Elevator picked = SCANAlgorithmElevatorSystem.pickElevator(elevators, pickupRequest);
                        appendRequest(picked, request);
                        return picked;
                    });

            // save elevator id
            elevatorId = Optional.of(elevator.getId());
        } else if (request instanceof ElevateRequest elevateRequest) {
            // find the elevator from the request
            Elevator elevator = elevators
                    .stream()
                    .filter(x -> x.getId() == elevateRequest.getElevatorId())
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(String.format("Elevator with id %d could not be found", elevateRequest.getElevatorId())));

            // check if the elevator has this request registered
            Optional<Request> r = elevator.getRequests()
                    .stream()
                    .filter(x ->
                            (x.getDestination() == elevateRequest.getDestination() && x.getDirection() == elevateRequest.getDirection())
                                    || (elevator.getLastFloorInDirection(x.getDirection()) == elevateRequest.getDestination()))
                    .findFirst();

            // if the similar request was not found then append it
            if (r.isEmpty()) appendRequest(elevator, request);

            // save elevator id
            elevatorId = Optional.of(elevator.getId());
        }

        // return elevator id
        return elevatorId.orElseThrow(() -> new NoSuchElementException("Could not get the elevator id"));
    }

    /**
     * @param elevator
     * @param pickup
     * @return
     */
    public static int calculateDistance(Elevator elevator, PickupRequest pickup) {
        // get the current direction of the elevator
        Direction direction = elevator.getDirection();

        // use the proper formula to calculate the distance
        return switch (direction) {
            case NOT_IN_MOVE -> Math.abs(elevator.getCurrentFloor() - pickup.getDestination());
            case UP -> (direction == pickup.getDirection() && elevator.getCurrentFloor() <= pickup.getDestination())
                    || (direction != pickup.getDirection() && elevator.getLastFloorInCurrentDirection() <= pickup.getDestination()) ?
                    pickup.getDestination() - elevator.getCurrentFloor() :
                    2 * elevator.getLastFloorInCurrentDirection() - elevator.getCurrentFloor() - pickup.getDestination();
            default -> (direction == pickup.getDirection() && elevator.getCurrentFloor() >= pickup.getDestination())
                    || (direction != pickup.getDirection() && elevator.getLastFloorInCurrentDirection() >= pickup.getDestination()) ?
                    elevator.getCurrentFloor() - pickup.getDestination() :
                    elevator.getCurrentFloor() + pickup.getDestination() - 2 * elevator.getLastFloorInCurrentDirection();
        };
    }

    /**
     * @param elevators
     * @param pickup
     * @return
     */
    public static Elevator pickElevator(List<Elevator> elevators, PickupRequest pickup) {
        // variables for best elevator
        Elevator best = null;
        Integer leastDistance = null;

        // loop over the elevator to find the one with the least distance to travel
        for (Elevator elevator : elevators) {
            // calculate the distance from pickup floor to the current elevator position
            int distance = calculateDistance(elevator, pickup);

            // capture the best elevator
            if (leastDistance == null || distance < leastDistance) {
                leastDistance = distance;
                best = elevator;
            }
        }

        // return found elevator
        return best;
    }

    /**
     * @param elevator
     * @param request
     */
    public static void appendRequest(Elevator elevator, Request request) {
        // return requests that have NOT_IN_MOVE direction (fe. from floor 5 to 5 etc.)
        if (request.getDirection() == Direction.NOT_IN_MOVE) return;

        // get the current direction of the elevator
        Direction direction = elevator.getDirection();

        // if the elevator is not in move then append the current request and return
        if (direction == Direction.NOT_IN_MOVE) {
            elevator.getRequests().add(request);
            return;
        }

        // split the requests for up and down
        List<Request> requestsUp = elevator.getRequests().stream().filter(x -> x.getDirection() == Direction.UP).collect(Collectors.toCollection(LinkedList::new));
        List<Request> requestsDown = elevator.getRequests().stream().filter(x -> x.getDirection() == Direction.DOWN).collect(Collectors.toCollection(LinkedList::new));
        List<Request> result = null;

        if (direction == Direction.UP) {
            // split the up request for those above and below current elevator floor
            List<Request> requestsUpAbove = requestsUp.stream().filter(x -> x.getDestination() >= elevator.getCurrentFloor()).collect(Collectors.toCollection(LinkedList::new));
            List<Request> requestsUpBelow = requestsUp.stream().filter(x -> x.getDestination() < elevator.getCurrentFloor()).collect(Collectors.toCollection(LinkedList::new));

            // now inset the current request to the proper list (requestsDown, requestsUpAbove, or requestsUpBelow)
            if (request.getDirection() == Direction.DOWN) {
                requestsDown.add(request);
                requestsDown.sort((x1, x2) -> Integer.compare(x2.getDestination(), x1.getDestination())); // descending;
            } else if (request.getDirection() == Direction.UP) {
                List<Request> buf;
                if (request.getDestination() >= elevator.getCurrentFloor()) buf = requestsUpAbove;
                else buf = requestsUpBelow;
                buf.add(request);
                buf.sort((x1, x2) -> Integer.compare(x1.getDestination(), x2.getDestination())); // ascending
            }

            // merge the lists in the proper order
            result = Stream.of(requestsUpAbove, requestsDown, requestsUpBelow).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedList::new));
        } else if (direction == Direction.DOWN) {
            // split the up request for those above and below current elevator floor
            List<Request> requestsDownAbove = requestsDown.stream().filter(x -> x.getDestination() > elevator.getCurrentFloor()).collect(Collectors.toCollection(LinkedList::new));
            List<Request> requestsDownBelow = requestsDown.stream().filter(x -> x.getDestination() <= elevator.getCurrentFloor()).collect(Collectors.toCollection(LinkedList::new));

            // now inset the current request to the proper list (requestsDown, requestsUpAbove, or requestsUpBelow)
            if (request.getDirection() == Direction.UP) {
                requestsUp.add(request);
                requestsUp.sort((x1, x2) -> Integer.compare(x1.getDestination(), x2.getDestination())); // ascending
            } else if (request.getDirection() == Direction.DOWN) {
                List<Request> buf;
                if (request.getDestination() <= elevator.getCurrentFloor()) buf = requestsDownBelow;
                else buf = requestsDownAbove;
                buf.add(request);
                buf.sort((x1, x2) -> Integer.compare(x2.getDestination(), x1.getDestination())); // descending;
            }

            // merge the lists in the proper order
            result = Stream.of(requestsDownBelow, requestsUp, requestsDownAbove).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedList::new));
        }

        // set up the result
        elevator.setRequests(result);
    }

}
