package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.PipelineCreatorService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.events.PipelineShutdownEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.PipelineAlreadyExistsException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.PipelineException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PipelineConfigRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PipelineRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.web.dto.PipelineTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class PipelineServiceImpl implements PipelineService {
	private static final Logger log = LoggerFactory.getLogger(PipelineServiceImpl.class);
	private final PipelineCreatorService pipelineCreatorService;
	private final PipelineStatusService pipelineStatusService;
	private final PipelineRepository pipelineRepository;
	private final PipelineConfigRepository pipelineConfigRepository;


	public PipelineServiceImpl(PipelineCreatorService pipelineCreatorService, PipelineStatusService pipelineStatusService,
	                           PipelineRepository pipelineRepository, PipelineConfigRepository pipelineConfigRepository) {
		this.pipelineCreatorService = pipelineCreatorService;
		this.pipelineStatusService = pipelineStatusService;
		this.pipelineRepository = pipelineRepository;
		this.pipelineConfigRepository = pipelineConfigRepository;
	}

	@EventListener
	public void handlePipelineShutdown(PipelineShutdownEvent event) {
		this.requestDeletion(event.pipelineId());
	}

	@Override
	public PipelineConfig addPipeline(PipelineConfig pipeline) throws PipelineException {
		if (pipelineRepository.exists(pipeline.getName())) {
			throw new PipelineAlreadyExistsException(pipeline.getName());
		} else {
			pipelineCreatorService.initialisePipeline(pipeline);
			pipelineRepository.activateNewPipeline(pipeline);
			log.atInfo().log("CREATION of pipeline '{}' successfully finished", pipeline.getName().replaceAll("[\n\r]", "_"));
//			pipelineConfigRepository.save(PipelineConfigEntity.fromConfig(pipeline));
			return pipeline;
		}
	}

	@Override
	public PipelineConfig addPipeline(PipelineConfig pipeline, File persistedFile) throws PipelineException {
		if (pipelineRepository.exists(pipeline.getName())) {
			throw new PipelineAlreadyExistsException(pipeline.getName());
		} else {
			pipelineCreatorService.initialisePipeline(pipeline);
			pipelineRepository.activateExistingPipeline(pipeline, persistedFile);
//			pipelineConfigRepository.save(PipelineConfigEntity.fromConfig(pipeline));
			return pipeline;
		}
	}

	public List<PipelineTO> getPipelines() {
		return pipelineRepository.getActivePipelines()
				.stream()
				.map(config -> PipelineTO.build(config, pipelineStatusService.getPipelineStatus(config.name()), pipelineStatusService.getPipelineStatusChangeSource(config.name())))
				.toList();
	}

	@Override
	public boolean requestDeletion(String pipeline) {
		if (pipelineRepository.exists(pipeline)) {
			pipelineStatusService.stopPipeline(pipeline);
			deletePipelineFromServices(pipeline);
			log.atInfo().log("DELETION of pipeline '{}' successfully finished", pipeline.replaceAll("[\n\r]", "_"));
			return true;
		} else {
			return false;
		}
	}

	private void deletePipelineFromServices(String pipeline) {
		pipelineRepository.delete(pipeline);
		pipelineCreatorService.removePipeline(pipeline);
	}
}
