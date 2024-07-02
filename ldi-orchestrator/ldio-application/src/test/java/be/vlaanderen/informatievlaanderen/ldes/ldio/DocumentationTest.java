package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class DocumentationTest {



	@Test
	void writeDocumentationSnippets() {
		ApplicationModules modules = ApplicationModules.of(Application.class);

		System.out.println(modules);

		new Documenter(modules)
				.writeModulesAsPlantUml()
				.writeDocumentation()
				.writeModuleCanvases()
				.writeIndividualModulesAsPlantUml();
	}

	@Test
	void x() {
		ApplicationModules modules = ApplicationModules.of(Application.class);

		new Documenter(modules)
				.writeModulesAsPlantUml()
				.writeDocumentation()
				.writeModuleCanvases()
				.writeIndividualModulesAsPlantUml();

		modules.forEach(System.out::println);
		modules.verify();


	}
}
