package app.simulation.elevator.request;

import app.simulation.elevator.model.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The base abstract class for a Request objects
 */
@Getter
@EqualsAndHashCode
@ToString
public abstract class Request{

    protected final int destination;

    /**
     * @param destination the floor where the elevator should go
     */
    public Request(int destination) {
        if(destination < 0) throw new IllegalArgumentException("Destination floor cannot be negative");
        this.destination = destination;
    }

    /**
     * @return the direction of a request
     */
    public abstract Direction getDirection();

}
