package app.simulation;

import lombok.Getter;

/**
 * The thread class that can run any type of simulation
 */
@Getter
public class SimulationRunner extends Thread {

    private static int SIMULATION_RUNNER_COUNTER = 0;

    private final Simulation simulation;
    private final int delayMs;
    private final int simulationId;
    private boolean run;

    /**
     * The constructor sets up the object with run set to true and unique id value
     * @param simulation that will be run
     * @param delayMs the delay between calling the step function from Simulation interface
     */
    public SimulationRunner(Simulation simulation, int delayMs) {
        this.simulation = simulation;
        this.delayMs = delayMs;
        this.simulationId = SIMULATION_RUNNER_COUNTER++;
        this.run = true;
    }

    /**
     * The method will disable the thread. It will rather wait until currently executed operations are finished than
     * interrupt the thread
     */
    void disable() {
        run = false;
    }

    /**
     * The run method of the thread. It executes the step method until interrupted or disabled
     * Interrupted exception is ignored
     */
    @Override
    public void run() {
        try {
            while (run) {
                simulation.step();
                Thread.sleep(delayMs);
            }
        } catch (InterruptedException ignore) {
        }
    }
}
