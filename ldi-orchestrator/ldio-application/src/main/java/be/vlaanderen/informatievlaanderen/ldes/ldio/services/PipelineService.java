package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class PipelineService {
	private final PipelineCreatorService pipelineCreatorService;
	private final PipelineStatusService pipelineStatusService;
	private final PipelineRepository pipelineRepository;
	private final ApplicationEventPublisher eventPublisher;

	public PipelineService(PipelineCreatorService pipelineCreatorService, PipelineStatusService pipelineStatusService,
						   PipelineRepository pipelineRepository, ApplicationEventPublisher eventPublisher) {
		this.pipelineCreatorService = pipelineCreatorService;
		this.pipelineStatusService = pipelineStatusService;
		this.pipelineRepository = pipelineRepository;
		this.eventPublisher = eventPublisher;
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

	public List<PipelineTO> getPipelines() {
		return pipelineRepository.getActivePipelines()
				.stream()
				.map(config -> PipelineTO.build(config, pipelineStatusService.getPipelineStatus(config.name()), pipelineStatusService.getPipelineStatusChangeSource(config.name())))
				.toList();
	}

	public boolean requestDeletion(String pipeline) {
		if (pipelineRepository.exists(pipeline)) {
			pipelineStatusService.stopPipeline(pipeline);
			eventPublisher.publishEvent(new PipelineDeletedEvent(pipeline));
			pipelineRepository.delete(pipeline);
			return true;
		} else {
			return false;
		}
	}
}
