package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
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
	@GetMapping(path = "{pipelineId}/status",  produces = MediaType.TEXT_PLAIN_VALUE)
	public PipelineStatus getPipelineStatus(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.getPipelineStatus(pipelineId);
	}

	@Override
	@PostMapping(path = "{pipelineId}/halt", produces = MediaType.APPLICATION_JSON_VALUE)
	public PipelineStatus haltPipeline(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.haltRunningPipeline(pipelineId);
	}

	@Override
	@PostMapping(path = "{pipelineId}/resume",  produces = MediaType.APPLICATION_JSON_VALUE)
	public PipelineStatus resumePipeline(@PathVariable("pipelineId") String pipelineId) {
		return pipelineStatusService.resumeHaltedPipeline(pipelineId);
	}

}
