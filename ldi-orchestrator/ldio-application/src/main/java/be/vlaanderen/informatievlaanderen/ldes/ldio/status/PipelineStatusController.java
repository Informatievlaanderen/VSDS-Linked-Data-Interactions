package be.vlaanderen.informatievlaanderen.ldes.ldio.status;

import be.vlaanderen.informatievlaanderen.ldes.ldio.status.services.PipelineStatusService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/admin/api/v1/pipeline")
public class PipelineStatusController implements OpenApiPipelineStatusController {

	private final PipelineStatusService pipelineStatusService;

	public PipelineStatusController(PipelineStatusService pipelineStatusService) {
		this.pipelineStatusService = pipelineStatusService;
	}

	@Override
	@GetMapping(path = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, PipelineStatus> getPipelineStatus() {
		return pipelineStatusService.getPipelineStatusOverview();
	}

	@Override
	@GetMapping(path = "{pipelineId}/status",  produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
	public PipelineStatus getPipelineStatus(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.getPipelineStatus(pipelineId);
	}

	@Override
	@PostMapping(path = "{pipelineId}/halt", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
	public PipelineStatus haltPipeline(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.haltRunningPipeline(pipelineId);
	}

	@Override
	@PostMapping(path = "{pipelineId}/resume",  produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
	public PipelineStatus resumePipeline(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.resumeHaltedPipeline(pipelineId);
	}

}
