package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class PipelineService {
	private final PipelineCreatorService pipelineCreatorService;
	private final PipelineRepository pipelineRepository;

	public PipelineService(PipelineCreatorService pipelineCreatorService,
	                       PipelineFileRepository pipelineRepository) {
		this.pipelineCreatorService = pipelineCreatorService;
		this.pipelineRepository = pipelineRepository;
	}

	public PipelineConfig addPipeline(PipelineConfig pipeline) throws PipelineException {
		if (pipelineRepository.exists(pipeline.getName())) {
			throw new PipelineAlreadyExistsException(pipeline.getName());
		} else {
			pipelineCreatorService.initialisePipeline(pipeline);
			pipelineRepository.activateNewPipeline(pipeline);
			return pipeline;
		}
	}

	public PipelineConfig addPipeline(PipelineConfig pipeline, File persistedFile) throws PipelineException {
		if (pipelineRepository.exists(pipeline.getName())) {
			throw new PipelineAlreadyExistsException(pipeline.getName());
		} else {
			pipelineCreatorService.initialisePipeline(pipeline);
			pipelineRepository.activateExistingPipeline(pipeline, persistedFile);
			return pipeline;
		}
	}

	public List<PipelineConfigTO> getPipelines() {
		return pipelineRepository.getActivePipelines();
	}

	public boolean deletePipeline(String pipeline) {
		return pipelineRepository.delete(pipeline);
	}
}
