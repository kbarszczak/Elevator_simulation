/**
 * The class represents elevator. It contains its id, current floor, destination floor and flag if its currently opened
 */
export default class ElevatorDiv {

    constructor(elevatorId, currentFloor, destinationFloor, isOpened) {
        this.elevatorId = elevatorId
        this.currentFloor = currentFloor
        this.destinationFloor = destinationFloor
        this.isOpened = isOpened
    }

}