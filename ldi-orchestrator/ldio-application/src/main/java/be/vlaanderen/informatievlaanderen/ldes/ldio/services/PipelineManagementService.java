package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PipelineManagementService {
	private final Logger log = LoggerFactory.getLogger(PipelineManagementService.class);
	private final PipelineCreatorService pipelineCreatorService;
	private final PipelineRepository pipelineRepository;

	public PipelineManagementService(PipelineCreatorService pipelineCreatorService,
	                                 PipelineFileRepository pipelineRepository) {
		this.pipelineCreatorService = pipelineCreatorService;
		this.pipelineRepository = pipelineRepository;
	}

	public PipelineConfig addPipeline(PipelineConfig pipeline) throws PipelineAlreadyExistsException {
		return addPipeline(pipeline, false);
	}

	public PipelineConfig addPipeline(PipelineConfig pipeline, boolean restoring) throws PipelineException {
		try {
			if (pipelineRepository.exists(pipeline.getName()) && !restoring) {
				throw new PipelineAlreadyExistsException(pipeline.getName());
			} else {
				pipelineCreatorService.initialisePipeline(pipeline);
				if (!restoring) {
					pipelineRepository.save(pipeline);
				}
				return pipeline;
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<PipelineConfigTO> getPipelines() {
		return pipelineRepository.findAll();
	}

	public void deletePipeline(String pipeline) {
		pipelineRepository.delete(pipeline);
	}
}
