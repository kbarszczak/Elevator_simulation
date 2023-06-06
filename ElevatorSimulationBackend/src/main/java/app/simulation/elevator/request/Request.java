package app.simulation.elevator.request;

import app.simulation.elevator.model.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 *
 */
@Getter
@EqualsAndHashCode
@ToString
public abstract class Request{

    protected final int destination;

    /**
     *
     * @param destination
     */
    public Request(int destination) {
        if(destination < 0) throw new IllegalArgumentException("Destination floor cannot be negative");
        this.destination = destination;
    }

    /**
     *
     * @return
     */
    public abstract Direction getDirection();

}
