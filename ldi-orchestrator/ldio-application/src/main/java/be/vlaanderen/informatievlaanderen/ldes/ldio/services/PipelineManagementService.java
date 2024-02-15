package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.PipelineCreationException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.creation.PipelineBeanCreatorService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PipelineManagementService {
	private final Logger log = LoggerFactory.getLogger(PipelineManagementService.class);
	private final List<PipelineConfig> pipelines;
	private final PipelineBeanCreatorService pipelineBeanCreatorService;
	private final PipelineRepository pipelineRepository;

	public PipelineManagementService(PipelineBeanCreatorService pipelineBeanCreatorService,
	                                 PipelineFileRepository pipelineRepository) {
		this.pipelineBeanCreatorService = pipelineBeanCreatorService;
		this.pipelineRepository = pipelineRepository;
		this.pipelines = new ArrayList<>();
	}

	public PipelineConfig addPipeline(PipelineConfig pipeline) throws PipelineCreationException {
		try {
			if (pipelineRepository.exists(pipeline.getName())) {
				throw new PipelineCreationException(pipeline.getName());
			} else {
				pipelineBeanCreatorService.initialisePipeline(pipeline);
				pipelineRepository.save(pipeline);
				pipelines.add(pipeline);
				return pipeline;
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<PipelineConfigTO> getPipelines() {
		return pipelineRepository.findAll();
	}


	public boolean deletePipeline(String pipeline) {
		return pipelineRepository.delete(pipeline);
	}
}
