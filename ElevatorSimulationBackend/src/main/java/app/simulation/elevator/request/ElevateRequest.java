package app.simulation.elevator.request;

import app.simulation.elevator.model.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The request class that may be used to serve requests that comes from inside the elevator
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ElevateRequest extends Request {

    private final int source;
    private final int elevatorId;

    /**
     * @param destination the floor where the elevator will go
     * @param source the floor where the request has been registered
     * @param elevatorId the id of the elevator that should handle this request
     */
    public ElevateRequest(int destination, int source, int elevatorId) {
        super(destination);
        this.source = source;
        this.elevatorId = elevatorId;
    }

    /**
     * @return the desired direction of the request
     */
    @Override
    public Direction getDirection() {
        return switch ((int) Math.signum(source - destination)) {
            case 1 -> Direction.DOWN;
            case -1 -> Direction.UP;
            default -> Direction.NOT_IN_MOVE;
        };
    }
}
