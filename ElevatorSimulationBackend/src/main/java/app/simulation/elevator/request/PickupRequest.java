package app.simulation.elevator.request;

import app.simulation.elevator.model.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PickupRequest extends Request {

    private final Direction direction;

    /**
     * @param destination
     * @param direction
     */
    public PickupRequest(int destination, Direction direction) {
        super(destination);
        this.direction = direction;
    }
}
