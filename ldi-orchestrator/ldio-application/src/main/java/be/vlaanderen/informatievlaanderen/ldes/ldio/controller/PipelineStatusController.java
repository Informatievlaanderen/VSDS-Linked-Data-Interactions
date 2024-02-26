package be.vlaanderen.informatievlaanderen.ldes.ldio.controller;

import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/admin/api/v1/pipeline")
public class PipelineStatusController {

	private final PipelineStatusService pipelineStatusService;

	public PipelineStatusController(PipelineStatusService pipelineStatusService) {
		this.pipelineStatusService = pipelineStatusService;
	}

	@GetMapping(path = "/status")
	public Map<String, PipelineStatus> getPipelineStatus() {
		return pipelineStatusService.getPipelineStatusOverview();
	}

	@GetMapping(path = "{pipelineId}/status")
	public PipelineStatus getPipelineStatus(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.getPipelineStatus(pipelineId);
	}

	@PostMapping(path = {"{pipelineId}/halt", "{pipelineId}/siesta"})
	public PipelineStatus haltPipeline(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.haltRunningPipeline(pipelineId);
	}

	@PostMapping(path = "{pipelineId}/resume")
	public PipelineStatus resumePipeline(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.resumeHaltedPipeline(pipelineId);
	}

}
