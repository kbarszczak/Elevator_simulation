// imports
import SimulationIdOption from './simulation-id-option.js'
import ElevatorDiv from './elevator-div.js'

// const variables
const baseUrl = "http://localhost:10000/simulation/elevator"
const pickupUrlPostfix = "/pickup"
const elevateUrlPostfix = "/elevate"

// variables
let simulationIdSelect = document.querySelector('#simulation-id')
let createSimulationButton = document.querySelector('#create-new-simulation')
let deleteSimulationButton = document.querySelector('#delete-simulation')
let elevatorContainerDiv = document.querySelector('#main-elevator-container')
let createSimulationSubmit = document.querySelector('#form-submit')
let createSimulationDiv = document.querySelector('#form-div')
let errorDiv = document.querySelector('#error-div')
let errorButton = document.querySelector('#error-div button')

// event listeners
document.addEventListener("DOMContentLoaded", preparePage);
simulationIdSelect.addEventListener('change', changeCurrentSimulation)
createSimulationButton.addEventListener('click', showCreateSimulationForm)
deleteSimulationButton.addEventListener('click', deleteSimulation)
createSimulationSubmit.addEventListener('click', createSimulation)
errorButton.addEventListener('click', hideError)

// functions
/**
 *
 * @param event
 */
function preparePage(event) {
    updateSimulationId()
    setInterval(updateSimulationId, 5000)
}

/**
 *
 * @param event
 * @returns {Promise<void>}
 */
async function changeCurrentSimulation(event) {
    // todo: change the simulation and load the new one
}

/**
 *
 * @param event
 */
function showCreateSimulationForm(event) {
    show(createSimulationDiv)
}

/**
 *
 * @param event
 * @returns {Promise<void>}
 */
async function createSimulation(event) {
    event.preventDefault()
    hide(createSimulationDiv)
    // todo: send request for creating new simulation and refresh the page
}

/**
 *
 * @param event
 * @returns {Promise<void>}
 */
async function deleteSimulation(event) {
    // todo: send request for deleting current simulation and refresh the page
}

/**
 *
 * @param event
 */
function hideError(event) {
    hide(errorDiv)
}

/**
 *
 * @param message
 */
function showError(message) {
    errorDiv.children.item(0).children.item(0).innerHTML = message
    show(errorDiv)
}

/**
 *
 * @param node
 */
function hide(node) {
    if (!node.classList.contains('hidden')) {
        node.classList.add('hidden')
    }
}

/**
 *
 * @param node
 */
function show(node) {
    if (node.classList.contains('hidden')) {
        node.classList.remove('hidden')
    }
}

/**
 *
 * @returns {Promise<void>}
 */
async function updateSimulationId() {
    let ids = await fetch(baseUrl)
        .then(promise => {
            if (promise.ok) return promise.json()
            throw new Error("Cannot load simulation ids")
        })
        .then(ids => {
            // add missing nodes
            ids.forEach(x => {
                if ([...simulationIdSelect.children].filter(y => y.value == x).length === 0) {
                    let simulationOption = new SimulationIdOption(x, x)
                    simulationIdSelect.appendChild(simulationOption.toNode())
                }
            });

            // remove useless nodes
            [...simulationIdSelect.children].forEach(x => {
                if(x.value != -1){
                    if (ids.filter(y => y == x.value).length === 0) {
                        simulationIdSelect.removeChild(x)
                    }
                }
            })
        })
        .catch(error => {
            showError(error)
        })
}