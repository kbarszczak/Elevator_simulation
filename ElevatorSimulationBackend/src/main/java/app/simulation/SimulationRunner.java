package app.simulation;

import lombok.Getter;

/**
 *
 */
@Getter
public class SimulationRunner extends Thread {

    private static int SIMULATION_RUNNER_COUNTER = 0;

    private final Simulation simulation;
    private final int delayMs;
    private final int simulationId;
    private boolean run;

    /**
     * @param simulation
     * @param delayMs
     */
    public SimulationRunner(Simulation simulation, int delayMs) {
        this.simulation = simulation;
        this.delayMs = delayMs;
        this.simulationId = SIMULATION_RUNNER_COUNTER++;
        this.run = true;
    }

    /**
     *
     */
    void disable() {
        run = false;
    }

    /**
     *
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
