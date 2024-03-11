package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.services.ModelSubjectsExtractor.extractSubjects;
import static org.assertj.core.api.Assertions.assertThat;

class ModelSubjectsExtractorTest {
	@Test
	void when_ExtractSubjectsFromModel_Then_ReturnEntityIds() throws Exception {
		var updateModel = Rio.parse(new FileInputStream("src/test/resources/people/all.nq"), "", RDFFormat.NQUADS);

		Set<Resource> entityIds = extractSubjects(updateModel);

		assertThat(entityIds)
				.as("Expected all subjects from test data")
				.containsExactlyInAnyOrder(
						SimpleValueFactory.getInstance().createIRI("http://somewhere/SarahJones/"),
						SimpleValueFactory.getInstance().createIRI("http://somewhere/MattJones/"),
						SimpleValueFactory.getInstance().createIRI("http://somewhere/BeckySmith/"),
						SimpleValueFactory.getInstance().createIRI("http://somewhere/JohnSmith/"),
						SimpleValueFactory.getInstance().createIRI("http://somewhere/TaylorKennedy/")
				);
	}

}
