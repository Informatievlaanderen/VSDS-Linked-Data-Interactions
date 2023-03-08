package be.vlaanderen.informatievlaanderen.ldes.ldi.client.services;

import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.FragmentFetcherException;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.UnparseableFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesService;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FragmentProcessorTest {
	LdesService ldesService = mock(LdesService.class);
	LdesFragment mockLdesFragment = mock(LdesFragment.class);
	ComponentExecutor componentExecutor = mock(ComponentExecutor.class);

	@Test
	void when_LdesServerHasFragments_TheyAreConvertedAndPrintedOut() throws IOException, URISyntaxException {
		when(ldesService.hasFragmentsToProcess()).thenReturn(true);
		LdesFragment ldesFragment = new LdesFragment();
		LdesMember ldesMember = readLdesMemberFromFile(getClass().getClassLoader(), "member1.txt");
		ldesFragment.addMember(ldesMember);
		when(ldesService.processNextFragment()).thenReturn(ldesFragment);

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, componentExecutor, 1L);
		fragmentProcessor.processLdesFragments();

		ArgumentCaptor<Model> argumentCaptor = ArgumentCaptor.forClass(Model.class);
		verify(componentExecutor, times(1)).transformLinkedData(argumentCaptor.capture());

		assertTrue(ldesMember.getMemberModel().isIsomorphicWith(argumentCaptor.getValue()));
	}

	@Test
	void when_LdesServerHasNoFragments_NothingIsPrinted() {
		when(ldesService.hasFragmentsToProcess()).thenReturn(false);

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, componentExecutor, 1L);
		fragmentProcessor.processLdesFragments();
	}

	@Test
	void when_LdesServerThrowsUnparseableFragmentException_UnparseableFragmentExceptionIsRethrown() {
		when(ldesService.hasFragmentsToProcess())
				.thenThrow(new UnparseableFragmentException("fragmentURL", new RuntimeException()));

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, componentExecutor, 1L);
		UnparseableFragmentException unparseableFragmentException = assertThrows(UnparseableFragmentException.class,
				fragmentProcessor::processLdesFragments);

		assertEquals("LDES Client: fragmentURL", unparseableFragmentException.getMessage());
	}

	@Test
	void when_LdesServerThrowsFragmentFetcherException_FragmentFetcherExceptionIsRethrown() {
		when(ldesService.hasFragmentsToProcess())
				.thenThrow(new FragmentFetcherException("fragmentURL", new RuntimeException()));

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, componentExecutor, 1L);
		FragmentFetcherException fragmentFetcherException = assertThrows(FragmentFetcherException.class,
				fragmentProcessor::processLdesFragments);

		assertEquals("fragmentURL", fragmentFetcherException.getMessage());
	}

	@Test
	void when_FragmentHasExpirationButNotExpired_FragmentProcessorWaits() {
		Awaitility.reset();
		when(ldesService.hasFragmentsToProcess()).thenReturn(true);
		when(ldesService.processNextFragment()).thenReturn(mockLdesFragment);
		when(mockLdesFragment.getExpirationDate()).thenReturn(LocalDateTime.now().plusSeconds(2));

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, componentExecutor, 60L);
		fragmentProcessor.processLdesFragments();
		when(ldesService.hasFragmentsToProcess()).thenReturn(false);
		await()
				.atLeast(1, TimeUnit.SECONDS)
				.atMost(3, TimeUnit.SECONDS)
				.untilAsserted(fragmentProcessor::processLdesFragments);
	}

	@Test
	void when_FragmentHasExpired_FragmentProcessorDoesNotWait() {
		Awaitility.reset();
		when(ldesService.hasFragmentsToProcess()).thenReturn(true);
		when(ldesService.processNextFragment()).thenReturn(mockLdesFragment);
		when(mockLdesFragment.getExpirationDate()).thenReturn(LocalDateTime.now().minusSeconds(2));

		FragmentProcessor fragmentProcessor = new FragmentProcessor(ldesService, componentExecutor, 60L);
		fragmentProcessor.processLdesFragments();
		when(ldesService.hasFragmentsToProcess()).thenReturn(false);
		await()
				.atMost(1, TimeUnit.SECONDS)
				.untilAsserted(fragmentProcessor::processLdesFragments);
	}

	private LdesMember readLdesMemberFromFile(ClassLoader classLoader, String fileName)
			throws URISyntaxException, IOException {
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		String memberString = Files.lines(Paths.get(file.toURI())).collect(Collectors.joining());
		Model outputModel = convertToModel(memberString);

		return new LdesMember("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				outputModel);
	}

	private Model convertToModel(String memberString) {
		return RDFParserBuilder.create().fromString(memberString).lang(Lang.NQUADS).toModel();
	}

}
