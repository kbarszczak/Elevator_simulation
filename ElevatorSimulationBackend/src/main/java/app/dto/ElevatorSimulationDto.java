package app.dto;


import java.util.List;

public record
ElevatorSimulationDto(
        Integer id,
        List<List<Integer>> elevators
) {
}
