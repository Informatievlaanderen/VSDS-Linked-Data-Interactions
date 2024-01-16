package be.vlaanderen.informatievlaanderen.ldes.ldio.controller;

import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineCreationService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.SenderQueueingService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/admin/api/v1/pipeline")
public class PipelineController {

	private final SenderQueueingService senderQueueingService;
	private final PipelineCreationService pipelineCreationService;

	public PipelineController(SenderQueueingService senderQueueingService, PipelineCreationService pipelineCreationService) {
		this.senderQueueingService = senderQueueingService;
		this.pipelineCreationService = pipelineCreationService;
	}

	// Pipeline Creation
	@GetMapping(path = "/overview")
	public ResponseEntity<List<PipelineConfigTO>> overview() {
		return ResponseEntity.ok(pipelineCreationService.getPipelines().stream().map(PipelineConfigTO::fromPipelineConfig).toList());
	}

	// Status Endpoints

	@GetMapping(path = "/status")
	public ResponseEntity<Map<String, PipelineStatus>> getPipelineStatus() {
		return ResponseEntity.ok(senderQueueingService.getPipelineStatusOverview());
	}

	@GetMapping(path = "/status/{pipelineId}")
	public ResponseEntity<PipelineStatus> getPipelineStatus(@PathVariable("pipelineId") String pipelineId) {
		return ResponseEntity.ok(senderQueueingService.getPipelineStatus(pipelineId));
	}

	@PostMapping(path = "/status/{pipelineId}/halt")
	public ResponseEntity<PipelineStatus> haltPipeline(@PathVariable("pipelineId") String pipelineId) {
		return ResponseEntity.ok(senderQueueingService.haltRunningPipeline(pipelineId));
	}

	@PostMapping(path = "/status/{pipelineId}/resume")
	public ResponseEntity<PipelineStatus> resumePipeline(@PathVariable("pipelineId") String pipelineId) {
		return ResponseEntity.ok(senderQueueingService.resumeHaltedPipeline(pipelineId));
	}

}
