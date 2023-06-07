/**
 * This class is model of currently displayed simulation.
 * It contains information such as simulation id, max floor and the elevator objects
 */
export default class ViewModel {

    constructor(simulationId, floors, elevators) {
        this.simulationId = simulationId
        this.floors = floors
        this.elevators = elevators
    }

}