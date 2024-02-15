package be.vlaanderen.informatievlaanderen.ldes.ldio.controller;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineManagementService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineTO.fromPipelineConfig;

@RestController
@RequestMapping(path = "/admin/api/v1/pipeline")
public class PipelineManagementController {

	private final PipelineManagementService pipelineManagementService;
	private final PipelineStatusService pipelineStatusService;

	public PipelineManagementController(PipelineManagementService pipelineManagementService, PipelineStatusService pipelineStatusService) {
		this.pipelineManagementService = pipelineManagementService;
		this.pipelineStatusService = pipelineStatusService;
	}

	@GetMapping()
	public List<PipelineTO> overview() {
		return pipelineManagementService.getPipelines()
				.stream()
				.map(config -> fromPipelineConfig(config, pipelineStatusService.getPipelineStatus(config.name())))
				.toList();
	}

	@PostMapping()
	public PipelineTO orchestratorConfig(@RequestBody PipelineConfig config) {
		var pipelineConfig = PipelineConfigTO.fromPipelineConfig(pipelineManagementService.addPipeline(config));

		return fromPipelineConfig(pipelineConfig, pipelineStatusService.getPipelineStatus(pipelineConfig.name()));
	}

	@DeleteMapping("/{pipeline}")
	@ResponseStatus(HttpStatus.OK)
	public void deletePipeline(@RequestParam String pipeline) {
		pipelineManagementService.deletePipeline(pipeline);
	}
}
