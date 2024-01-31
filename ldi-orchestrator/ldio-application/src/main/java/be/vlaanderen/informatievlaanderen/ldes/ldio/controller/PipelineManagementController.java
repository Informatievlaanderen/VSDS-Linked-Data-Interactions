package be.vlaanderen.informatievlaanderen.ldes.ldio.controller;

import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineManagementService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
				.map(config -> fromPipelineConfig(config, pipelineStatusService.getPipelineStatus(config.getName())))
				.toList();
	}
}
