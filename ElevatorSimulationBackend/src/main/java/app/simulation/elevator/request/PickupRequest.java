package app.simulation.elevator.request;

import app.simulation.elevator.model.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The request class that can be used when registering pickup requests from a specified floor
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PickupRequest extends Request {

    private final Direction direction;

    /**
     * @param destination the floor from which the elevator should pick up one
     * @param direction the desired direction of one's movement
     */
    public PickupRequest(int destination, Direction direction) {
        super(destination);
        this.direction = direction;
    }
}
