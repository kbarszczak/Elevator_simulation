package app.dto;


import java.util.List;

public record
ElevatorSimulationDto(
        Integer id,
        Integer floors,
        List<List<Integer>> elevators
) {
}
