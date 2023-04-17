package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioLdesClientAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.*;

class LdesClientTest {
	private ComponentExecutor componentExecutor;
	private LdiAdapter adapter;

	@BeforeEach
	void setUp() {
		adapter = mock(LdiAdapter.class);
		componentExecutor = mock(ComponentExecutor.class);
	}

	@Test
	void when_invalidConfigProvided_then_noInteractionsExpected() {
		LdioLdesClientAutoConfig autoConfig = new LdioLdesClientAutoConfig();
		autoConfig.ldioConfigurator().configure(adapter, componentExecutor, new ComponentProperties());

		verifyNoInteractions(componentExecutor, adapter);
	}

	@Test
	void test() {
		LdioLdesClientAutoConfig autoConfig = new LdioLdesClientAutoConfig();
		autoConfig.ldioConfigurator().configure(adapter, componentExecutor,
				new ComponentProperties(Map.of(LdioLdesClientProperties.URL, "http://localhost:8080/my-ldes")));

		 verify(componentExecutor).transformLinkedData(any());
	}
}
