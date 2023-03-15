package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules.DummyIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules.DummyOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.ComponentExecutorImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.LdiSenderImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.IntStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.HALTED;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.RESUMING;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class FlowAutoConfigurationTest {

	@Autowired
	LdiInput ldiInput;

	@Test
	void verifyBasicPipelineFlow() {
		DummyIn dummyIn = (DummyIn) ldiInput;
		ComponentExecutorImpl executor = (ComponentExecutorImpl) ldiInput.getExecutor();
		LdiSenderImpl ldiSender = (LdiSenderImpl) executor.getLdiSender();
		DummyOut dummyOut = (DummyOut) ldiSender.getLdiOutputs().get(0);
		dummyIn.sendData();

		Model expected = RDFParserBuilder.create()
				.fromString(
						"""
								_:Beced3445712fba4a1f51b6fae28e0147 <http://schema.org/description> "Transformed" .
								_:Beced3445712fba4a1f51b6fae28e0147 <http://schema.org/integer> "0"^^<http://www.w3.org/2001/XMLSchema#integer> .""")
				.lang(Lang.NQUADS)
				.toModel();

		await().until(() -> dummyOut.output.size() == 1);
		assertTrue(expected.isIsomorphicWith(dummyOut.output.get(0)));
	}

	@Test
	void verifyHaltedPipelineFlow() {
		DummyIn dummyIn = (DummyIn) ldiInput;
		ComponentExecutorImpl executor = (ComponentExecutorImpl) ldiInput.getExecutor();
		LdiSenderImpl ldiSender = (LdiSenderImpl) executor.getLdiSender();
		DummyOut dummyOut = (DummyOut) ldiSender.getLdiOutputs().get(0);

		// Initial Run
		IntStream.range(0, 10)
				.forEach(value -> dummyIn.sendData());
		await().until(() -> dummyOut.output.size() == 10);

		// Halt Pipeline
		ldiSender.handlePipelineStatus(new PipelineStatusEvent(HALTED));

		IntStream.range(0, 10)
				.forEach(value -> dummyIn.sendData());
		await().until(() -> ldiSender.getQueue().size() == 10);
		assertEquals(10, dummyOut.output.size());

		// Resume Pipeline
		ldiSender.handlePipelineStatus(new PipelineStatusEvent(RESUMING));

		await().until(() -> ldiSender.getQueue().isEmpty());
		assertEquals(20, dummyOut.output.size());

	}

}
