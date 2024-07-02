package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusConsumer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioObserver;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.PipelineShutdownEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.status.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.status.StatusChangeSource;
import be.vlaanderen.informatievlaanderen.ldes.ldio.status.events.PipelineStatusEvent;
import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdioLdesClientTest {

    @Mock
    private ComponentExecutor componentExecutor;

    @Mock
    private LdioObserver observer;

    @Mock
    private MemberSupplier supplier;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ClientStatusConsumer clientStatusConsumer;

    private LdioLdesClient client;
    private final String pipelineName = "pipeline";

    @BeforeEach
    void setUp() {
        when(observer.getPipelineName()).thenReturn(pipelineName);
        client = new LdioLdesClient(
                componentExecutor,
                observer,
                supplier,
                eventPublisher,
                false,
                clientStatusConsumer);
    }

    @AfterEach
    void tearDown() {
        client.shutdown();
    }

    @Test
    void when_EndOfLdesException_ShutdownPipeline() {
        when(supplier.get()).thenThrow(EndOfLdesException.class);

        client.start();

        verify(eventPublisher).publishEvent(new PipelineShutdownEvent(pipelineName));
    }

    @Test
    void when_RuntimeException_StopPipeline() {
        doThrow(RuntimeException.class).when(supplier).init();

        client.start();

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            verify(eventPublisher).publishEvent(new PipelineStatusEvent(pipelineName, PipelineStatus.RUNNING, StatusChangeSource.MANUAL));
            verify(eventPublisher).publishEvent(new PipelineStatusEvent(pipelineName, PipelineStatus.HALTED, StatusChangeSource.MANUAL));
        });

    }
}