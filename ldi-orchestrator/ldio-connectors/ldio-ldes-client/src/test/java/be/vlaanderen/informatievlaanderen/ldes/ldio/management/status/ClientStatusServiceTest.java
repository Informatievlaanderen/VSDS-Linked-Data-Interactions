package be.vlaanderen.informatievlaanderen.ldes.ldio.management.status;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDeletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import static ldes.client.treenodesupplier.domain.valueobject.ClientStatus.REPLICATING;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ClientStatusService.class)
class ClientStatusServiceTest {
	@Autowired
	private ClientStatusService clientStatusService;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Test
	void test_baseFlow() {
		String pipelineId = "1";

		var status = clientStatusService.getClientStatus(pipelineId);

		assertThat(status).isEmpty();

		clientStatusService.updateStatus(pipelineId, REPLICATING);

		assertThat(clientStatusService.getClientStatuses()).contains(new ClientStatusTo(pipelineId, REPLICATING));
		assertThat(clientStatusService.getClientStatus(pipelineId)).isNotEmpty();

		eventPublisher.publishEvent(new PipelineDeletedEvent(pipelineId));

		assertThat(clientStatusService.getClientStatus(pipelineId)).isEmpty();
	}
}
