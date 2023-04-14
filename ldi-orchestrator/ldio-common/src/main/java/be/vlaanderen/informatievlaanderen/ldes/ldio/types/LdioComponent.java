package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDataEvent;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LdioComponent {
	private final Logger log = LoggerFactory.getLogger(LdioComponent.class);
	private final String name;

	protected LdioComponent() {
		this.name = "name";
	}

	@EventListener
	public void handleLdioEvent(PipelineDataEvent event) {
		if (name.equals(event.targetComponent())) {
			log.info(RDFWriter.source(event.data())
					.lang(Lang.TURTLE)
					.asString());
		}
	}
}
