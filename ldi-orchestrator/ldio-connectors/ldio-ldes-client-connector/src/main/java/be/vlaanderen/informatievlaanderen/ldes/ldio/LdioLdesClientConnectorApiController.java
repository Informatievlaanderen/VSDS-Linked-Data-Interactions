package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.collection.LdioLdesClientConnectorApiCollection;
import be.vlaanderen.informatievlaanderen.ldes.ldio.event.LdesClientConnectorApiCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdioLdesClientConnectorApiController {
	private final LdioLdesClientConnectorApiCollection clientConnectorApis;

	public LdioLdesClientConnectorApiController(LdioLdesClientConnectorApiCollection clientConnectorApis) {
		this.clientConnectorApis = clientConnectorApis;
	}

	@PostMapping(path = "/{pipeline}/token")
	public ResponseEntity<String> handleToken(@PathVariable("pipeline") String pipeline, @RequestBody String token) {
		clientConnectorApis.get(pipeline)
				.orElseThrow(() -> new IllegalArgumentException("Not a valid pipeline"))
				.handleToken(token);
		return ResponseEntity.accepted().build();
	}

	@PostMapping(path = "/{pipeline}/transfer")
	public ResponseEntity<String> handleTransfer(@PathVariable("pipeline") String pipeline, @RequestBody String transfer) {
		return ResponseEntity.accepted()
				.body(clientConnectorApis.get(pipeline)
						.orElseThrow(() -> new IllegalArgumentException("Not a valid pipeline"))
						.handleTransfer(transfer));
	}

	@EventListener
	void handleNewPipelines(LdesClientConnectorApiCreatedEvent connectorApiCreatedEvent) {
		clientConnectorApis.add(connectorApiCreatedEvent.pipelineName(), connectorApiCreatedEvent.ldesClientConnectorApi());
	}

	@EventListener
	void deletePipeline(PipelineDeletedEvent deletedEvent) {
		clientConnectorApis.remove(deletedEvent.pipelineId()).ifPresent(LdioLdesClientConnectorApi::shutdown);
	}

	@EventListener
	public void handlePipelineStatusEvent(PipelineStatusEvent event) {
		clientConnectorApis.get(event.pipelineId()).ifPresent(connectorApi -> {
			switch (event.status()) {
				case RUNNING -> connectorApi.resume();
				case HALTED -> connectorApi.pause();
				default -> {
					// do nothing with the other status events
				}
			}
		});
	}

}
