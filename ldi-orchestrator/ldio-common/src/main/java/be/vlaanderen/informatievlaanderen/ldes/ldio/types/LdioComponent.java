package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDataOutputEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDataTransformEvent;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

public abstract class LdioComponent {
	private final String name;
	private final ApplicationEventPublisher eventPublisher;

	protected LdioComponent(String name, ApplicationEventPublisher eventPublisher) {
		this.name = name;
		this.eventPublisher = eventPublisher;
	}

	@EventListener
	public void handleLdioEvent(PipelineDataTransformEvent event) {
		if (name.equals(event.targetComponent())) {
			sendPipelineDataEvent(event.ldiOrder(), event.data());
		}
	}

	void sendPipelineDataEvent(LdiOrder ldiOrder, Model model) {
		if (!ldiOrder.transformations().isEmpty()) {
			eventPublisher.publishEvent(new PipelineDataTransformEvent(ldiOrder.transformations().poll(), ldiOrder, model));;
		}
		else {
			ldiOrder.outputs().parallelStream().forEach(output -> {
				eventPublisher.publishEvent(new PipelineDataOutputEvent(output, model));
			});
		}
	}
}
