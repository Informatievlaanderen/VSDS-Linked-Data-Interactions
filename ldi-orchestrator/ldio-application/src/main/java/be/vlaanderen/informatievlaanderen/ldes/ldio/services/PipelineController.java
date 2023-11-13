package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.*;

@RestController
@RequestMapping(path = "/admin/api/v1/pipeline")
// TODO redefine pipelinestatus feature
public class PipelineController {

	private final ApplicationEventPublisher applicationEventPublisher;
	private final PipelineService pipelineService;

	private PipelineStatus pipelineStatus = PipelineStatus.RUNNING;

	public PipelineController(ApplicationEventPublisher applicationEventPublisher, PipelineService pipelineService) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.pipelineService = pipelineService;
	}

	@GetMapping(path = "/status")
	public ResponseEntity<PipelineStatus> getPipelineStatus() {
		return ResponseEntity.ok(pipelineStatus);
	}

	@PostMapping(path = "/halt")
	public ResponseEntity<PipelineStatus> haltPipeline() {
		return switch (pipelineStatus) {
			case RUNNING, RESUMING -> haltRunningPipeline();
			case HALTED -> ResponseEntity.ok(HALTED);
		};
	}

	@PostMapping(path = "/resume")
	public ResponseEntity<PipelineStatus> resumePipeline() {
		return switch (pipelineStatus) {
			case RUNNING -> ResponseEntity.ok(RUNNING);
			case HALTED -> resumeHaltedPipeline();
			case RESUMING -> ResponseEntity.ok(RESUMING);
		};
	}

	@GetMapping(path = "/overview")
	public ResponseEntity<List<PipelineConfig>> overview() {
		return ResponseEntity.ok(pipelineService.getPipelines());
	}

	@EventListener
	public void handlePipelineStatusResponse(PipelineStatusEvent statusEvent) {
		this.pipelineStatus = statusEvent.status();
	}

	private ResponseEntity<PipelineStatus> resumeHaltedPipeline() {
		applicationEventPublisher.publishEvent(new PipelineStatusEvent(RESUMING));
		return ResponseEntity.ok(RESUMING);
	}

	private ResponseEntity<PipelineStatus> haltRunningPipeline() {
		applicationEventPublisher.publishEvent(new PipelineStatusEvent(HALTED));
		return ResponseEntity.ok(HALTED);
	}
}
