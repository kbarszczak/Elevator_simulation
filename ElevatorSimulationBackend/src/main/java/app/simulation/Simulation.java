package app.simulation;

/**
 * The Simulation interface. It may be used to run any type of Simulations inside SimulationRunner class
 */
public interface Simulation {

    /**
     * The method is responsible for executing one step of the simulation
     */
    void step();

}
