package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.MissingHeaderException;
import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.*;

@WireMockTest(httpPort = 10101)
class HttpInputPollerTest {
	private final LdiAdapter adapter = mock(LdiAdapter.class);
	private final ComponentExecutor executor = mock(ComponentExecutor.class);
	private static final String BASE_URL = "http://localhost:10101";
	private static final String ENDPOINT = "/resource";
	private static final String CONTENT = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
	private static final String CONTENT_TYPE = "application/n-quads";
	private HttpInputPoller httpInputPoller;

	@BeforeEach
	void setUp() {
		when(adapter.apply(any())).thenReturn(Stream.empty());

		httpInputPoller = new HttpInputPoller(executor, adapter, BASE_URL + ENDPOINT, true);
	}

	@Test
	void testClientPolling() {
		stubFor(get(ENDPOINT).willReturn(ok().withHeader("Content-Type",
				CONTENT_TYPE).withBody(CONTENT)));

		httpInputPoller.poll();
		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void whenPolling_andMissesHeader() {
		stubFor(get(ENDPOINT).willReturn(ok().withBody(CONTENT)));

		httpInputPoller = new HttpInputPoller(executor, adapter, BASE_URL + ENDPOINT, false);
		Executable polling = () -> httpInputPoller.poll();

		assertThrows(MissingHeaderException.class, polling);
		Mockito.verifyNoInteractions(adapter);
		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void whenPeriodicPolling_thenReturnTwoTimesTheSameResponse() {
		stubFor(get(ENDPOINT).willReturn(ok().withHeader("Content-Type", CONTENT_TYPE).withBody(CONTENT)));

		httpInputPoller.schedulePoller(1);

		Mockito.verify(adapter, timeout(1500).times(2)).apply(LdiAdapter.Content.of(CONTENT, CONTENT_TYPE));
		WireMock.verify(2, getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void when_OnContinueIsTrueAndPeriodPollingReturnsNot2xx_thenKeepPolling() {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		httpInputPoller.schedulePoller(1);

		Mockito.verify(adapter, after(2000).never()).apply(any());
		WireMock.verify(new CountMatchingStrategy(CountMatchingStrategy.GREATER_THAN_OR_EQUAL, 2),
				getRequestedFor(urlEqualTo(ENDPOINT)));

	}

	@Test
	void when_OnContinueIsFalseAndPeriodPollingReturnsNot2xx_thenStopPolling() {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		httpInputPoller = new HttpInputPoller(executor, adapter, BASE_URL + ENDPOINT, false);
		httpInputPoller.schedulePoller(1);

		Mockito.verify(adapter, after(2000).never()).apply(any());
		WireMock.verify(1, getRequestedFor(urlEqualTo(ENDPOINT)));
	}

	@Test
	void when_EndpointDoesNotExist_Then_NoDataIsSent() {
		String wrongEndpoint = "/non-existing-resource";
		httpInputPoller = new HttpInputPoller(executor, adapter, BASE_URL + wrongEndpoint, true);

		httpInputPoller.poll();

		WireMock.verify(getRequestedFor(urlEqualTo(wrongEndpoint)));
		Mockito.verifyNoInteractions(adapter);
	}

	@Test
	void when_ResponseIsNot200_Then_NoDataIsSent() {
		stubFor(get(ENDPOINT).willReturn(forbidden()));

		httpInputPoller.poll();

		WireMock.verify(getRequestedFor(urlEqualTo(ENDPOINT)));
		Mockito.verifyNoInteractions(adapter);
	}

}
