// imports
import SimulationIdOption from './models.js'

// const variables
const baseUrl = "http://localhost:10000/simulation/elevator"

// variables
let simulationIdSelect = document.querySelector('#simulation-id')

// event listeners
document.addEventListener("DOMContentLoaded", preparePage);

// functions
function preparePage(event) {
    updateSimulationId()

    setInterval(updateSimulationId, 5000)
}

async function updateSimulationId() {
    let ids = await fetch(baseUrl)
        .then(async promise => {
            if (promise.ok) return promise.json()
            throw new Error("Cannot load simulation ids")
        })
        .catch(error => {
            console.log(error) //todo: handle the error
        })

    // add missing nodes
    ids.forEach(x => {
        if([...simulationIdSelect.children].filter(y => y.value == x).length === 0){
            let simulationOption = new SimulationIdOption(x, x)
            simulationIdSelect.appendChild(simulationOption.toNode())
        }
    });
    
    // remove useless nodes
    [...simulationIdSelect.children].forEach(x => {
        if(ids.filter(y => y == x.value || x.value == -1).length === 0){
            simulationIdSelect.removeChild(x)
        }
    })
}