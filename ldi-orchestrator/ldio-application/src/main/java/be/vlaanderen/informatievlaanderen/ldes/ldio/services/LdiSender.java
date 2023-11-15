package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import io.micrometer.core.instrument.Metrics;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioMetricValues.LDIO_DATA_OUT;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.HALTED;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.RUNNING;

public class LdiSender extends LdioTransformer {
	private final ApplicationEventPublisher applicationEventPublisher;
	private PipelineStatus pipelineStatus;
	private final List<LdiOutput> ldiOutputs;
	private final Queue<Model> queue = new ArrayDeque<>();
	private final String pipelineName;

	public LdiSender(String pipelineName, ApplicationEventPublisher applicationEventPublisher,
	                 List<LdiOutput> ldiOutputs) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.ldiOutputs = ldiOutputs;
		this.pipelineStatus = RUNNING;
		this.pipelineName = pipelineName;
		Metrics.counter(LDIO_DATA_OUT, PIPELINE_NAME, pipelineName).increment(0);
	}

	@SuppressWarnings({ "java:S131", "java:S1301" })
	@EventListener
	public void handlePipelineStatus(PipelineStatusEvent statusEvent) {
		switch (statusEvent.status()) {
			case RESUMING -> {
				while (!queue.isEmpty()) {
					Metrics.counter(LDIO_DATA_OUT, PIPELINE_NAME, pipelineName).increment();
					ldiOutputs.parallelStream().forEach(ldiOutput -> ldiOutput.accept(queue.poll()));
				}
				this.pipelineStatus = RUNNING;
				applicationEventPublisher.publishEvent(new PipelineStatusEvent(RUNNING));
			}
			case HALTED -> this.pipelineStatus = HALTED;
		}
	}

	@Override
	public void apply(Model model) {
		if (pipelineStatus == RUNNING) {
			Metrics.counter(LDIO_DATA_OUT, PIPELINE_NAME, pipelineName).increment();
			ldiOutputs.parallelStream().forEach(ldiOutput -> ldiOutput.accept(model));
		} else {
			queue.add(model);
		}
	}
}
