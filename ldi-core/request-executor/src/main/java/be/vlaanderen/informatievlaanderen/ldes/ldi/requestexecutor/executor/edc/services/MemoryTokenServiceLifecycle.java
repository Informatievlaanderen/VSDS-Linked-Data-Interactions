package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryTokenServiceLifecycle {

    private final Logger log = LoggerFactory.getLogger(MemoryTokenServiceLifecycle.class);

    private boolean terminated = false;
    private boolean paused = false;

    void checkPipelineState() {
        checkPause();
        checkTerminated();
    }

    private synchronized void checkPause() {
        while (paused) {
            try {
                wait();
            }  catch (InterruptedException e) {
                log.error("Thread interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private void checkTerminated() {
        if (terminated) {
            throw new TerminatedException();
        }
    }

    void pause() {
        this.paused = true;
    }

    synchronized void resume() {
        this.paused = false;
        this.notifyAll();
    }

    void shutdown() {
        this.terminated = true;
    }

    public static class TerminatedException extends RuntimeException {
    }

}
