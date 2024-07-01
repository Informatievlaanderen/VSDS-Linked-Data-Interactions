package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class DocumentationTest {

	ApplicationModules modules = ApplicationModules.of(Application.class);

	@Test
	void writeDocumentationSnippets() {

		System.out.println(modules);

		new Documenter(modules)
				.writeModulesAsPlantUml()
				.writeIndividualModulesAsPlantUml();
	}
}
