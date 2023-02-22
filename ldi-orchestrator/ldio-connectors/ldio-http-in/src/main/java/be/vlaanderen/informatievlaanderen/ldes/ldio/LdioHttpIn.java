package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.apache.jena.rdf.model.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class LdioHttpIn implements LdiInput {

	private final ComponentExecutor componentExecutor;

	public LdioHttpIn(ComponentExecutor componentExecutor) {
		this.componentExecutor = componentExecutor;
	}

	@PostMapping("data")
	public void receiveLinkedData(@RequestBody Model model) {
		componentExecutor.transformLinkedData(model);
	}
}
