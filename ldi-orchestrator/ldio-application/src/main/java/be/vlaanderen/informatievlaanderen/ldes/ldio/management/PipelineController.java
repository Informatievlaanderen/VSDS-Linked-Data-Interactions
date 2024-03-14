package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.PipelineStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineTO;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/api/v1/pipeline")
public class PipelineController implements OpenApiPipelineController {

	private final PipelineService pipelineService;
	private final PipelineStatusService pipelineStatusService;

	public PipelineController(PipelineService pipelineService, PipelineStatusService pipelineStatusService) {
		this.pipelineService = pipelineService;
		this.pipelineStatusService = pipelineStatusService;
	}

	/**
	 * Provides an overview of all available running pipelines.
	 *
	 * @return A list of pipeline objects that containing the configuration and its current state.
	 */
	@Override
	@GetMapping(produces = {"application/json", "application/yaml"})
	public List<PipelineTO> overview() {
		return pipelineService.getPipelines();
	}

	/**
	 * Creates a pipeline given its configuration.
	 *
	 * @param config The configuration to create a pipeline.
	 * @return A pipeline object that containing the configuration and its current state.
	 */
	@Override
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping(consumes = {"application/json", "application/yaml"}, produces = {"application/json", "application/yaml"})
	public PipelineTO addPipeline(@RequestBody PipelineConfigTO config) {
		var pipelineConfig = PipelineConfigTO.fromPipelineConfig(pipelineService.addPipeline(config.toPipelineConfig()));

		return PipelineTO.build(pipelineConfig, pipelineStatusService.getPipelineStatus(pipelineConfig.name()), pipelineStatusService.getPipelineStatusChangeSource(config.name()));
	}

	/**
	 * Deletes a pipeline given its name.
	 *
	 * @param pipeline The name of the pipeline to be deleted.
	 * @return A ResponseEntity indicating the result of the deletion operation.
	 *         Returns ResponseEntity.ok() if the pipeline was found and successfully deleted,
	 *         ResponseEntity.noContent() if the pipeline was not found or could not be deleted.
	 */
	@Override
	@DeleteMapping("/{pipeline}")
	public ResponseEntity<Void> deletePipeline(@PathVariable String pipeline) {
		if (pipelineService.requestDeletion(pipeline)) {
			return ResponseEntity.accepted().build();
		} else return ResponseEntity.noContent().build();
	}

}
