package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusConsumer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioObserver;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.events.PipelineShutdownEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.StatusChangeSource;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.events.PipelineStatusEvent;
import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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
	void when_EndOfLdesException_And_AllDataProcessed_ShutdownPipeline() {
		when(observer.hasProcessedAllData()).thenReturn(true);
		when(supplier.get()).thenThrow(EndOfLdesException.class);

		client.start();

		InOrder inOrder = inOrder(eventPublisher);
		inOrder.verify(eventPublisher).publishEvent(new PipelineStatusEvent(pipelineName, PipelineStatus.RUNNING, StatusChangeSource.MANUAL));
		inOrder.verify(eventPublisher).publishEvent(new PipelineStatusEvent(pipelineName, PipelineStatus.HALTED, StatusChangeSource.MANUAL));
		inOrder.verify(eventPublisher).publishEvent(new PipelineShutdownEvent(pipelineName));
	}

	@Test
	void when_EndOfLdesException_ShutdownPipeline() {
		when(observer.hasProcessedAllData()).thenReturn(false).thenReturn(true);
		when(supplier.get()).thenThrow(EndOfLdesException.class);

		client.start();
		verify(eventPublisher).publishEvent(new PipelineStatusEvent(pipelineName, PipelineStatus.RUNNING, StatusChangeSource.MANUAL));

		await().atMost(Duration.ofSeconds(40)).untilAsserted(() -> {
			verify(eventPublisher).publishEvent(new PipelineStatusEvent(pipelineName, PipelineStatus.HALTED, StatusChangeSource.MANUAL));
			verify(eventPublisher).publishEvent(new PipelineShutdownEvent(pipelineName));
		});
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