package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules.DummyIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules.DummyOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.ComponentExecutorImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class FlowAutoConfigurationTest {

	@Autowired
	LdiInput ldiInput;

	@Test
	void verifyBeanCreation() {
		DummyIn dummyIn = (DummyIn) ldiInput;
		ComponentExecutorImpl executor = (ComponentExecutorImpl) ldiInput.getExecutor();
		DummyOut dummyOut = (DummyOut) executor.getLdiOutputs().get(0);
		dummyIn.sendData();

		Model expected = RDFParserBuilder.create()
				.fromString(
						"""
								_:Beced3445712fba4a1f51b6fae28e0147 <http://schema.org/description> "Transformed" .
								_:Beced3445712fba4a1f51b6fae28e0147 <http://schema.org/integer> "0"^^<http://www.w3.org/2001/XMLSchema#integer> .""")
				.lang(Lang.NQUADS)
				.toModel();

		await().atMost(2, TimeUnit.SECONDS).until(() -> dummyOut.output != null);
		assertTrue(expected.isIsomorphicWith(dummyOut.output));
	}

}
