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

//todo: implement:
// 1. view model
// 2. refresh current model if set
// 3. update view with model

// event functions
function preparePage() {
    update()
    setInterval(update, 1000)
}

async function update() {
    await updateSimulationIds(parseInt(simulationIdSelect.value))
    await updateModel()
    await updateView()
}

function showCreateSimulationForm() {
    show(createSimulationDiv)
}

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

function hideError() {
    hide(errorDiv)
}

// other functions
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

async function updateModel() {
    let id = parseInt(simulationIdSelect.value)
    if (id !== -1) {
        await fetch(`${baseUrl}/${id}`)
            .then(async response => {
                if (!response.ok) throw new Error(await response.text())
                return response.json()
            })
            .then(data => {
                console.log(data)
                // todo: set up viewModel here
            })
            .catch(error => {
                showMessage(error)
            })
    } else {
        viewModel = null
    }
}

function updateView() {
    if (viewModel !== null) {
        // todo: update view
    } else {
        // todo: clear elevator div
    }
}

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

function hide(node) {
    if (!node.classList.contains('hidden')) {
        node.classList.add('hidden')
    }
}

function show(node) {
    if (node.classList.contains('hidden')) {
        node.classList.remove('hidden')
    }
}