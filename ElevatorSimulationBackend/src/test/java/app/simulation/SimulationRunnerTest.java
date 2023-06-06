package app.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationRunnerTest {

    private int counter;
    private SimulationRunner runner;

    @BeforeEach
    void setUp() {
        counter = 0;
        runner = new SimulationRunner(
                () -> counter++,
                1
        );
    }

    @Test
    void disableAndRun() {
        try {
            runner.start();
            Thread.sleep(10);

            runner.disable();
            runner.join();

            assertTrue(counter > 0);
            assertFalse(runner.isRun());
        } catch (InterruptedException ignore) {
        }
    }
}