package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.HALTED;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.RUNNING;

public class LdiSender implements Consumer<Model> {
	private final ApplicationEventPublisher applicationEventPublisher;
	private PipelineStatus pipelineStatus;
	private final List<LdiOutput> ldiOutputs;
	private final Queue<Model> queue = new ArrayDeque<>();

	public LdiSender(ApplicationEventPublisher applicationEventPublisher,
			List<LdiOutput> ldiOutputs) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.ldiOutputs = ldiOutputs;
		this.pipelineStatus = RUNNING;
	}

	@Override
	public void accept(Model model) {
		if (pipelineStatus == RUNNING) {
			ldiOutputs.parallelStream().forEach(ldiOutput -> ldiOutput.accept(model));
		} else {
			queue.add(model);
		}
	}

	@SuppressWarnings({"java:S131", "java:S1301"})
	@EventListener
	public void handlePipelineStatus(PipelineStatusEvent statusEvent) {
		switch (statusEvent.getStatus()) {
			case RESUMING -> {
				while (!queue.isEmpty()) {
					ldiOutputs.parallelStream().forEach(ldiOutput -> ldiOutput.accept(queue.poll()));
				}
				this.pipelineStatus = RUNNING;
				applicationEventPublisher.publishEvent(new PipelineStatusEvent(RUNNING));
			}
			case HALTED -> this.pipelineStatus = HALTED;
		}
	}
}
