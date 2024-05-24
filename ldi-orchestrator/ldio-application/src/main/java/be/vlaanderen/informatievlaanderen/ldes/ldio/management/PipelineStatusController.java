package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.HaltedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.ResumedPipelineStatus;
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
	public Map<String, PipelineStatus.Value> getPipelineStatus() {
		return pipelineStatusService.getPipelineStatusOverview();
	}

	@Override
	@GetMapping(path = "{pipelineId}/status",  produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
	public PipelineStatus.Value getPipelineStatus(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.getPipelineStatus(pipelineId).getStatusValue();
	}

	@Override
	@PostMapping(path = "{pipelineId}/halt", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
	public PipelineStatus.Value haltPipeline(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.updatePipelineStatus(pipelineId, new HaltedPipelineStatus());
	}

	@Override
	@PostMapping(path = "{pipelineId}/resume",  produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE})
	public PipelineStatus.Value resumePipeline(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.updatePipelineStatus(pipelineId, new ResumedPipelineStatus());
	}

}
