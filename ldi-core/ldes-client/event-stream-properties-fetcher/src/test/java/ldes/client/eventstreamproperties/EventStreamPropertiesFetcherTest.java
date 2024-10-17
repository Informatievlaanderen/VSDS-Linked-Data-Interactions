package ldes.client.eventstreamproperties;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.eventstreamproperties.valueobjects.PropertiesRequest;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest(httpPort = EventStreamPropertiesFetcherTest.WIREMOCK_PORT)
class EventStreamPropertiesFetcherTest {
	public static final int WIREMOCK_PORT = 12121;
	private static final EventStreamProperties eventStreamProperties = new EventStreamProperties(
			"http://localhost:12121/observations",
			"http://purl.org/dc/terms/isVersionOf",
			"http://www.w3.org/ns/prov#generatedAtTime",
			""
	);
	private EventStreamPropertiesFetcher fetcher;

	@BeforeEach
	void setUp() {
		RequestExecutor requestExecutor = new RequestExecutorFactory().createNoAuthExecutor();
		fetcher = new EventStreamPropertiesFetcher(requestExecutor);
	}

	@Test
	void given_EventStream_when_FetchProperties_then_ReturnValidProperties() throws IOException, URISyntaxException {
		URL resource = getClass().getClassLoader().getResource("models/eventstream.ttl");
		final byte[] responseBytes = Files.readAllBytes(Path.of(Objects.requireNonNull(resource).toURI()));
		stubFor(get("/observations").willReturn(ok().withBody(responseBytes)));

		final EventStreamProperties properties = fetcher.fetchEventStreamProperties(new PropertiesRequest("http://localhost:12121/observations", Lang.TTL));

		verify(getRequestedFor(urlEqualTo("/observations")));
		assertThat(properties)
				.usingRecursiveComparison()
				.isEqualTo(eventStreamProperties);
	}

	@Test
	void given_View_when_FetchProperties_then_ReturnValidProperties() throws IOException, URISyntaxException {
		URL resource = getClass().getClassLoader().getResource("models/view.ttl");
		final byte[] responseBytes = Files.readAllBytes(Path.of(Objects.requireNonNull(resource).toURI()));
		stubFor(get("/observations/by-page").willReturn(ok().withBody(responseBytes)));

		final EventStreamProperties properties = fetcher.fetchEventStreamProperties(new PropertiesRequest("http://localhost:12121/observations/by-page", Lang.TTL));

		verify(getRequestedFor(urlEqualTo("/observations/by-page")));
		assertThat(properties)
				.usingRecursiveComparison()
				.isEqualTo(eventStreamProperties);
	}

	@Test
	void given_Fragment_when_FetchProperties_then_ReturnValidProperties() throws IOException, URISyntaxException {
		URL treeNodeResource = getClass().getClassLoader().getResource("models/treenode.ttl");
		final byte[] treeNodeBody = Files.readAllBytes(Path.of(Objects.requireNonNull(treeNodeResource).toURI()));
		stubFor(get("/observations/by-page?pageNumber=1").willReturn(ok().withBody(treeNodeBody)));
		URL eventSourceResource = getClass().getClassLoader().getResource("models/eventstream.ttl");
		final byte[] eventStreamBody = Files.readAllBytes(Path.of(Objects.requireNonNull(eventSourceResource).toURI()));
		stubFor(get("/observations").willReturn(ok().withBody(eventStreamBody)));

		final EventStreamProperties properties = fetcher.fetchEventStreamProperties(new PropertiesRequest("http://localhost:12121/observations/by-page?pageNumber=1", Lang.TTL));

		verify(getRequestedFor(urlEqualTo("/observations/by-page?pageNumber=1")));
		verify(getRequestedFor(urlEqualTo("/observations")));
		assertThat(properties)
				.usingRecursiveComparison()
				.isEqualTo(eventStreamProperties);
	}
}