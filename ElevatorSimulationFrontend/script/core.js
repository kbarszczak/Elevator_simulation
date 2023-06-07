// imports
import ViewModel from './view-model.js'
import SimulationIdOption from './simulation-id-option.js'
import ElevatorDiv from './elevator-div.js'

// const variables
const baseUrl = "http://localhost:10000/simulation/elevator"
const pickupUrlPostfix = "/pickup"
const elevateUrlPostfix = "/elevate"

// variables
let simulationIdSelect = document.querySelector('#simulation-id')
let showNewSimulationFormButton = document.querySelector('#create-new-simulation')
let createSimulationButton = document.querySelector('#form-button')
let deleteSimulationButton = document.querySelector('#delete-simulation')
let errorButton = document.querySelector('#error-div button')
let createSimulationDiv = document.querySelector('#form-div')
let elevatorContainerDiv = document.querySelector('#main-elevator-container')
let errorDiv = document.querySelector('#error-div')
let viewModel = null

// event listeners
document.addEventListener("DOMContentLoaded", preparePage);
simulationIdSelect.addEventListener('change', update)
showNewSimulationFormButton.addEventListener('click', showCreateSimulationForm)
createSimulationButton.addEventListener('click', createSimulation)
deleteSimulationButton.addEventListener('click', deleteSimulation)
errorButton.addEventListener('click', hideError)

// event functions
/**
 * The function sets up the page, loads all the components and sets the interval to update the page content every second
 */
function preparePage() {
    update()
    setInterval(update, 1000)
}

/**
 * The function handles all the page update. It updates all the available simulations, current simulation model and
 * the simulation view
 * @returns {Promise<void>} this promise can be ignored
 */
async function update() {
    await updateSimulationIds(parseInt(simulationIdSelect.value))
    await updateModel()
    await updateView()
}

/**
 * The function pops out the simulation creation form
 */
function showCreateSimulationForm() {
    show(createSimulationDiv)
}

/**
 * The function deletes currently selected simulation from the server. Its sets the 'None' value
 * as a currently displayed simulation. If the request is not successful then proper message pops out on the screen
 */
function deleteSimulation() {
    let id = parseInt(simulationIdSelect.value)
    if (id !== -1) {
        fetch(`${baseUrl}/${id}`,
            {
                method: "DELETE"
            })
            .then(async response => {
                if (!response.ok) throw new Error(await response.text())
            })
            .then(async () => {
                await updateSimulationIds(-1)
                await update()
            })
            .catch(error => {
                showMessage(error)
            })
    } else {
        showMessage("Please select simulation to delete")
    }

}

/**
 * The function creates a new simulation on the server. It will verify each form parameter.
 * In case of any error proper message pops out
 * @param event that caused calling this function
 */
function createSimulation(event) {
    event.preventDefault()

    let elevatorCount = document.querySelector('#input-elevator-count').value
    let delayMs = document.querySelector('#input-delay-ms').value
    let maxFloors = document.querySelector('#input-max-floors').value
    let systemName = document.querySelector('#select-system-name').value
    let errors = []

    if (elevatorCount < 1 || elevatorCount > 16) errors.push(`Elevator has to be in range from 1 to 16. Got: '${elevatorCount}'`)
    if (delayMs < 0) errors.push(`Delay cannot be negative. Got: '${delayMs}ms'`)
    if (maxFloors < 1 || maxFloors > 500) errors.push(`Elevator has to be in range from 1 to 500. Got: '${maxFloors}'`)

    if (errors.length !== 0) {
        showMessage(errors)
        return
    }

    hide(createSimulationDiv)
    fetch(`${baseUrl}?elevator_count=${elevatorCount}&delay=${delayMs}&system_name=${systemName}&floors=${maxFloors}`,
        {
            method: 'POST'
        })
        .then(async response => {
            if (!response.ok) throw new Error(await response.text())
            return response.json()
        })
        .then(async id => {
            await updateSimulationIds(parseInt(id))
            await update()
        })
        .catch(error => {
            showMessage(error)
        })
}

/**
 * The method hides the error box
 */
function hideError() {
    hide(errorDiv)
}

/**
 * The method sends the pickup request to the server
 * @param event the button that caused this event
 */
function servePickupRequest(event) {
    let idSplit = event.target.id.split("_")
    let direction = idSplit[0].toUpperCase()
    let source = parseInt(idSplit[1])

    fetch(`${baseUrl}${pickupUrlPostfix}/${viewModel.simulationId}?source=${source}&direction=${direction}`,
        {
            method: "PUT"
        }).then(async response => {
        if (!response.ok) throw new Error(await response.text())
    }).catch(error => {
        showMessage(error)
    })
}

/**
 * The method sends the elevate request to the server
 * @param event the button that caused this event
 */
function serveElevateRequest(event) {
    let idSplit = event.target.id.split("_")
    let simulationId = parseInt(idSplit[1])
    let elevatorId = parseInt(idSplit[3])
    let destination = parseInt(idSplit[5])

    let source = viewModel.elevators.filter(x => x.elevatorId === elevatorId)[0].currentFloor
    fetch(`${baseUrl}${elevateUrlPostfix}/${simulationId}?source=${source}&destination=${destination}&elevator_id=${elevatorId}`,
        {
            method: "PUT"
        }).then(async response => {
        if (!response.ok) throw new Error(await response.text())
    }).catch(error => {
        showMessage(error)
    })
}

// other functions
/**
 * The function loads simulation ids and presents them in the proper combobox
 * @param selectedId the id of the simulation that will be selected after completion. If the id does not exist after execution
 * of this function then the None simulation is set
 * @returns {Promise<void>} this may be ignored
 */
async function updateSimulationIds(selectedId) {
    await fetch(baseUrl)
        .then(promise => {
            if (promise.ok) return promise.json()
            throw new Error("Cannot load simulation ids")
        })
        .then(ids => {
            // add missing nodes
            ids.forEach(x => {
                if ([...simulationIdSelect.children].filter(y => parseInt(y.value) === x).length === 0) {
                    let simulationOption = new SimulationIdOption(x, x)
                    simulationIdSelect.appendChild(simulationOption.toNode())
                }
            });

            // remove useless nodes
            [...simulationIdSelect.children].forEach(x => {
                if (parseInt(x.value) !== -1) {
                    if (ids.filter(y => y === parseInt(x.value)).length === 0) {
                        simulationIdSelect.removeChild(x)
                    }
                }
            })

            simulationIdSelect.value = isNaN(selectedId) ? "-1" : selectedId.toString()
        })
        .catch(error => {
            showMessage(error)
        })
}

/**
 * The function synchronizes current simulation model with the one existing on the server
 * @returns {Promise<void>} this may be ignored
 */
async function updateModel() {
    let id = parseInt(simulationIdSelect.value)
    if (id === -1) {
        viewModel = null
        return
    }

    await fetch(`${baseUrl}/${id}`)
        .then(async response => {
            if (!response.ok) throw new Error(await response.text())
            return response.json()
        })
        .then(data => {
            if (viewModel === null) {
                viewModel = new ViewModel(data.id, data.floors, data.elevators.map(x => new ElevatorDiv(x[0], x[1], x[2], x[3] === 1)))
            } else {
                viewModel.simulationId = data.id
                viewModel.floors = data.floors
                viewModel.elevators = data.elevators.map(x => new ElevatorDiv(x[0], x[1], x[2], x[3] === 1))
            }
        })
        .catch(error => {
            showMessage(error)
        })
}

/**
 * The function is responsible for updating the model view. If the model view is null then the view is empty.
 * If the current view shows the same simulation its state will be updated otherwise, the simulation from the model will be setup
 */
function updateView() {
    if (viewModel === null) {
        deleteChildren(elevatorContainerDiv)
        return
    }

    // update the simulation state
    if (elevatorContainerDiv.firstElementChild !== null
        && parseInt(elevatorContainerDiv.firstElementChild.id.split("_")[1]) === viewModel.simulationId) {
        viewModel.elevators.forEach(x => {
            let elevator = document.querySelector(`#simulation_${viewModel.simulationId}_elevator_${x.elevatorId}`)
            let elevatorChild = elevator.firstElementChild
            elevatorChild.style = `bottom: calc(120px * ${x.currentFloor} + 10px);`
            let elevatorPs = [...elevatorChild.children]
            elevatorPs[0].innerHTML = `c: ${x.currentFloor}`
            elevatorPs[1].innerHTML = `d: ${x.destinationFloor}`
            if (x.isOpened) {
                if (!elevatorChild.classList.contains("opened")) elevatorChild.classList.add("opened")
            } else {
                if (elevatorChild.classList.contains("opened")) elevatorChild.classList.remove("opened")
            }
        })
    } else { // set up the simulation
        // delete all current children
        deleteChildren(elevatorContainerDiv)

        // add elevators
        viewModel.elevators.forEach(x => {
            let elevatorShaft = document.createElement("div")
            elevatorShaft.classList.add("shaft")
            elevatorShaft.id = `simulation_${viewModel.simulationId}_elevator_${x.elevatorId}`
            elevatorShaft.style = `height: calc(120px * ${viewModel.floors});`;

            let elevator = document.createElement("div")
            elevator.classList.add("floor")
            elevator.classList.add("elevator")
            elevator.style = `bottom: calc(120px * ${x.currentFloor} + 10px);`

            let pc = document.createElement("p")
            pc.innerHTML = `c: ${x.currentFloor}`

            let pd = document.createElement("p")
            pd.innerHTML = `d: ${x.destinationFloor}`

            let panel = document.createElement("div")
            panel.classList.add("elevator-panel")

            let panelButtons = document.createElement("div")
            panelButtons.classList.add("elevator-buttons");

            [...Array(viewModel.floors).keys()].forEach(y => {
                let button = document.createElement('button')
                button.id = `simulation_${viewModel.simulationId}_elevator_${x.elevatorId}_button_${y}`
                button.innerHTML = y.toString()
                button.addEventListener('click', serveElevateRequest)
                panelButtons.appendChild(button)
            })

            panel.appendChild(panelButtons)

            elevator.appendChild(pc)
            elevator.appendChild(pd)
            elevator.appendChild(panel)

            elevatorShaft.appendChild(elevator)

            elevatorContainerDiv.appendChild(elevatorShaft)
        })

        // add floors shaft
        let floors = document.createElement("div")
        floors.classList.add("shaft");
        floors.style = `height: calc(120px * ${viewModel.floors});`;
        [...Array(viewModel.floors).keys()].forEach(x => {
            let floor = document.createElement("div")
            floor.classList.add("floor")
            floor.style = `bottom: calc(120px * ${x} + 10px);`

            let p = document.createElement("p")
            p.innerHTML = `Floor: ${x}`

            let buttonUp = document.createElement("button")
            buttonUp.id = `up_${x}`
            buttonUp.innerHTML = "up"
            buttonUp.addEventListener('click', servePickupRequest)

            let buttonDown = document.createElement("button")
            buttonDown.id = `down_${x}`
            buttonDown.innerHTML = "down"
            buttonDown.addEventListener('click', servePickupRequest)

            floor.appendChild(p)
            floor.appendChild(buttonUp)
            floor.appendChild(buttonDown)

            floors.appendChild(floor)
        })

        elevatorContainerDiv.appendChild(floors)
    }
}

/**
 * The function deletes all the children of a given node
 * @param node to delete the children from
 */
function deleteChildren(node) {
    while (node.firstChild) {
        node.removeChild(node.firstChild)
    }
}

/**
 * The function displays error messages in the full screen window
 * @param message this may be a single string or an array. In case of array each element will be put to a separate paragraph
 */
function showMessage(message) {
    if (!Array.isArray(message)) message = [message]

    let innerErrorDiv = errorDiv.children.item(0);
    [...innerErrorDiv.childNodes].filter(x => x.tagName === 'P').forEach(x => innerErrorDiv.removeChild(x))

    message.forEach(x => {
        let p = document.createElement('p')
        p.innerHTML = x
        innerErrorDiv.prepend(p)
    })

    show(errorDiv)
}

/**
 * The function hides selected node
 * @param node to be hidden
 */
function hide(node) {
    if (!node.classList.contains('hidden')) {
        node.classList.add('hidden')
    }
}

/**
 * The function shows selected node
 * @param node to be shown
 */
function show(node) {
    if (node.classList.contains('hidden')) {
        node.classList.remove('hidden')
    }
}