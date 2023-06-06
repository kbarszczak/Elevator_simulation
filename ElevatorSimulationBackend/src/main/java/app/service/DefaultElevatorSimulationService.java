package app.service;

import app.dto.ElevatorSimulationDto;
import app.simulation.SimulationRunner;
import app.simulation.elevator.ElevatorSimulation;
import app.simulation.elevator.model.Direction;
import app.simulation.elevator.model.Elevator;
import app.simulation.elevator.request.ElevateRequest;
import app.simulation.elevator.request.PickupRequest;
import app.simulation.elevator.system.ElevatorSystem;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DefaultElevatorSimulationService implements ElevatorSimulationService {

    private final List<SimulationRunner> runners;

    public DefaultElevatorSimulationService() {
        this.runners = new ArrayList<>();
    }

    @Override
    public Integer createElevatorSimulation(Integer elevatorCount, Integer delayMs, String elevatorSystemClassName)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        Class<?> c = Class.forName(elevatorSystemClassName);
        Constructor<?> constructor = c.getConstructor();
        ElevatorSystem system = (ElevatorSystem) constructor.newInstance();
        SimulationRunner runner = new SimulationRunner(
                new ElevatorSimulation(
                        system,
                        IntStream.range(0, elevatorCount).mapToObj(x -> new Elevator()).collect(Collectors.toCollection(LinkedList::new))
                ),
                delayMs
        );
        runners.add(runner);
        runner.start();
        return runner.getSimulationId();
    }

    @Override
    public List<Integer> getSimulationIds() {
        return runners
                .stream()
                .map(SimulationRunner::getSimulationId)
                .toList();
    }

    @Override
    public ElevatorSimulationDto getSimulationById(Integer id) {
        ElevatorSimulation simulation = getSimulationByRunnerId(id);

        List<List<Integer>> elevators = simulation.getElevators()
                .stream()
                .map(x ->
                        List.of(
                                x.getId(),
                                x.getCurrentFloor(),
                                x.getRequests().isEmpty() ? x.getCurrentFloor() : x.getRequests().get(0).getDestination(),
                                x.getRequests().isEmpty() ? 0 : x.getCurrentFloor() == x.getRequests().get(0).getDestination() ? 1 : 0
                        ))
                .toList();

        return new ElevatorSimulationDto(
                id,
                elevators
        );
    }

    @Override
    public Integer deleteSimulationById(Integer id) {
        if(runners.removeIf(x -> x.getSimulationId() == id)) return id;
        else throw new NoSuchElementException(String.format("Cannot find the simulation with id '%d'", id));
    }

    @Override
    public Integer createPickupRequestById(Integer id, Integer source, Direction direction) {
        ElevatorSimulation simulation = getSimulationByRunnerId(id);
        return simulation.register(new PickupRequest(
                source,
                direction
        ));
    }

    @Override
    public Integer createElevateRequestById(Integer id, Integer source, Integer elevatorId, Integer destination) {
        ElevatorSimulation simulation = getSimulationByRunnerId(id);
        return simulation.register(new ElevateRequest(
                destination,
                source,
                elevatorId
        ));
    }

    private ElevatorSimulation getSimulationByRunnerId(Integer id) {
        return (ElevatorSimulation) runners
                .stream()
                .filter(x -> x.getSimulationId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("Cannot find the simulation with id '%d'", id)))
                .getSimulation();
    }

}
