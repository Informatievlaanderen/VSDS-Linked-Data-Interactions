package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoInput;
import org.apache.jena.rdf.model.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class LdioHttpIn implements LdtoInput {

	private final ComponentExecutor componentExecutor;

	public LdioHttpIn(ComponentExecutor componentExecutor) {
		this.componentExecutor = componentExecutor;
	}

	@PostMapping("data")
	public void receiveLinkedData(@RequestBody Model model) {
		passLinkedData(model);
	}

	@Override
	public void passLinkedData(Model linkedDataModel) {
		componentExecutor.transformLinkedData(linkedDataModel);
	}
}
