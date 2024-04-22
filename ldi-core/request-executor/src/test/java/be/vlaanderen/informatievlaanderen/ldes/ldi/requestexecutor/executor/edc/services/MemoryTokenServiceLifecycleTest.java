package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.SequencedCollection;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MemoryTokenServiceLifecycleTest {

    private MemoryTokenServiceLifecycle serviceLifecycle;

    @BeforeEach
    void setUp() {
        serviceLifecycle = new MemoryTokenServiceLifecycle();
    }

    @Test
    void shouldPauseAndResume() throws InterruptedException {
        final SequencedCollection<String> tracker = new ArrayList<>();

        Thread testThread = new Thread(() -> {
            serviceLifecycle.pause();
            tracker.addLast("The lifecycle is paused. Let's check the state!");
            serviceLifecycle.checkPipelineState(); // This should block until resumed
            tracker.addLast("Finally, we have resumed!");
        });

        testThread.start();
        await().atLeast(100, TimeUnit.MILLISECONDS); // Ensures the thread has time to pause
        tracker.addLast("The thread with the lifecycle should now be waiting until someone resumes the lifecycle.");
        await().atLeast(100, TimeUnit.MILLISECONDS);
        tracker.addLast("After a minor nap, the lifecycle should still be waiting..");

        serviceLifecycle.resume();
        testThread.join();

        assertThat(tracker.getLast()).isEqualTo("Finally, we have resumed!");
        assertDoesNotThrow(() -> serviceLifecycle.checkPipelineState());
    }

    @Test
    void whenLifecycleIsRunning_shouldNotThrowExceptionsOrHang() {
        assertDoesNotThrow(() -> serviceLifecycle.checkPipelineState());
    }

    @Test
    void whenLifecycleIsShutDown_ThenTerminatedExceptionIsThrown() {
        serviceLifecycle.shutdown();
        assertThatThrownBy(() -> serviceLifecycle.checkPipelineState())
                .isInstanceOf(MemoryTokenServiceLifecycle.TerminatedException.class);
    }

}