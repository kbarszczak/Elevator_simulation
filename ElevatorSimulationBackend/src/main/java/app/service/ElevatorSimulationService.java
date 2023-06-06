package app.service;

import app.dto.ElevatorSimulationDto;
import app.simulation.elevator.model.Direction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ElevatorSimulationService {

    Integer createElevatorSimulation(Integer elevatorCount, Integer maxFloors, Integer delayMs, String elevatorSystemClassName)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException;

    List<Integer> getSimulationIds();

    ElevatorSimulationDto getSimulationById(Integer id);

    Integer deleteSimulationById(Integer id);

    Integer createPickupRequestById(Integer id, Integer source, Direction direction);

    Integer createElevateRequestById(Integer id, Integer source, Integer elevatorId, Integer destination);

}
