import ElevatorDiv from './elevator-div.js'

export default class ViewModel{

    constructor(simulationId, floors, elevators) {
        this.simulationId = simulationId
        this.floors = floors
        this.elevators = elevators
    }

}