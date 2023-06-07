/**
 * This class represents option for select box for picking current simulation id
 */
export default class SimulationIdOption {

    constructor(option, value) {
        this.option = option
        this.value = value
    }

    toNode(selected = false) {
        let option = document.createElement("option")
        option.appendChild(document.createTextNode(this.option))
        option.value = this.value
        option.selected = selected
        return option;
    }

}