package app.controller;

import app.dto.ElevatorSimulationDto;
import app.service.ElevatorSimulationService;
import app.simulation.elevator.model.Direction;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/simulation/elevator")
public class ElevatorSimulationController {

    private final ElevatorSimulationService simulationService;

    @GetMapping
    public ResponseEntity<List<Integer>> getSimulationIds() {
        return ResponseEntity.ok(simulationService.getSimulationIds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElevatorSimulationDto> getSimulationById(@PathVariable("id") @Min(0) @NotNull Integer id) {
        return ResponseEntity.ok(simulationService.getSimulationById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteSimulationById(@PathVariable("id") @Min(0) @NotNull Integer id) {
        return ResponseEntity.ok(simulationService.deleteSimulationById(id));
    }

    @PutMapping("/pickup/{id}")
    public ResponseEntity<Integer> createPickupRequestById(
            @PathVariable("id") @Min(0) @NotNull Integer id,
            @RequestParam("source") @Min(0) @NotNull Integer source,
            @RequestParam("direction") @NotNull Direction direction
    ) {
        return ResponseEntity.ok(simulationService.createPickupRequestById(id, source, direction));
    }

    @PutMapping("/elevate/{id}")
    public ResponseEntity<Integer> createElevateRequestById(
            @PathVariable("id") @Min(0) @NotNull Integer id,
            @RequestParam("source") @Min(0) @NotNull Integer source,
            @RequestParam("elevator_id") @Min(0) @NotNull Integer elevatorId,
            @RequestParam("destination") @Max(500) @NotNull Integer destination
    ) {
        return ResponseEntity.ok(simulationService.createElevateRequestById(id, source, elevatorId, destination));
    }

    @PostMapping
    public ResponseEntity<Integer> createSimulation(
            @RequestParam("elevator_count") @Min(1) @Max(16) @NotNull Integer elevatorCount,
            @RequestParam("delay") @Min(0) @NotNull Integer delayMs,
            @RequestParam("system_name") @NotEmpty String systemClassName
    ) throws ReflectiveOperationException {
        return ResponseEntity.ok(simulationService.createElevatorSimulation(elevatorCount, delayMs, systemClassName));
    }

    @ExceptionHandler(ReflectiveOperationException.class)
    public ResponseEntity<String> handleReflectiveOperationException(ReflectiveOperationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("%s. Please, verify provided parameters", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}