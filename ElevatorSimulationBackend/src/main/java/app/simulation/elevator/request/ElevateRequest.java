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
public class ElevateRequest extends Request {

    private final int source;
    private final int elevatorId;

    /**
     * @param destination
     * @param elevatorId
     */
    public ElevateRequest(int destination, int source, int elevatorId) {
        super(destination);
        this.source = source;
        this.elevatorId = elevatorId;
    }

    @Override
    public Direction getDirection() {
        return switch ((int) Math.signum(source - destination)) {
            case 1 -> Direction.DOWN;
            case -1 -> Direction.UP;
            default -> Direction.NOT_IN_MOVE;
        };
    }
}
