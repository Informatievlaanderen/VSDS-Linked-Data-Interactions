package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.event.HttpInPipelineCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineDoesNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;
import static java.util.Optional.ofNullable;

@RestController
public class LdioHttpInController {
	private static final Logger log = LoggerFactory.getLogger(LdioHttpInController.class);

	private final Map<String, LdioHttpInProcess> httpInProcesses = new HashMap<>();

	@PostMapping(path = "/{pipeline}")
	ResponseEntity<String> processInput(@RequestHeader("Content-Type") String contentTypeHeader,
	                                    @RequestHeader("Content-Length") String contentLength,
	                                    @PathVariable("pipeline") String pipeline, @RequestBody String content) {
		var contentType = contentTypeHeader.split(";")[0];
		logIncomingRequest(contentType, contentLength, pipeline);


		LdioHttpInProcess inputProcess = ofNullable(httpInProcesses.get(pipeline))
				.orElseThrow(() -> new PipelineDoesNotExistException(pipeline));
		if (inputProcess.isPaused()) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(String.format("The LDIO pipeline named %s is currently paused.", pipeline));
		} else {
			inputProcess.processInput(content, contentType);
		}

		return ResponseEntity.accepted().build();
	}

	@EventListener
	void handleNewPipelines(HttpInPipelineCreatedEvent pipelineCreatedEvent) {
		httpInProcesses.put(pipelineCreatedEvent.pipelineName(), pipelineCreatedEvent.ldioHttpInProcess());
	}

	@EventListener
	void handleDelete(PipelineDeletedEvent event) {
		httpInProcesses.remove(event.pipelineId());
	}

	private void logIncomingRequest(String contentType, String contentLength, String pipelineName) {
		var httpMethod = HttpMethod.POST.name();
		var type = ofNullable(contentType).orElse("(unknown)");
		log.atDebug().log("{} /{} type: {} length: {}", httpMethod, pipelineName, type, ofNullable(contentLength).orElse(valueOf(0L)));
	}
}
