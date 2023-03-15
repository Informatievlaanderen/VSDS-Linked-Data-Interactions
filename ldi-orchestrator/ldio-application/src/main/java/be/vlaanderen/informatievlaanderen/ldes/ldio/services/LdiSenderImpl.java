package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.LdiSender;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.HALTED;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.RUNNING;

public class LdiSenderImpl implements LdiSender {
	private final ApplicationEventPublisher applicationEventPublisher;
	private PipelineStatus pipelineStatus;
	private final List<LdiOutput> ldiOutputs;
	private final Queue<Model> queue = new LinkedList<>();

	public LdiSenderImpl(ApplicationEventPublisher applicationEventPublisher,
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

	@EventListener
	public void handlePipelineStatus(PipelineStatusEvent statusEvent) {
		if (statusEvent.getStatus() == HALTED) {
			this.pipelineStatus = HALTED;
		}
		if (statusEvent.getStatus() == RESUMING) {
			while (!queue.isEmpty()) {
				ldiOutputs.parallelStream().forEach(ldiOutput -> ldiOutput.accept(queue.poll()));
			}
			applicationEventPublisher.publishEvent(new PipelineStatusEvent(RUNNING));
		}
		if (statusEvent.getStatus() == RUNNING) {
			this.pipelineStatus = RUNNING;
		}
	}

	public List<LdiOutput> getLdiOutputs() {
		return ldiOutputs;
	}

	public Queue<Model> getQueue() {
		return queue;
	}
}
