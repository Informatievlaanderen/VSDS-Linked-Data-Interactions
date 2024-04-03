package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpInAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInProcess.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatusTrigger.HALT;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatusTrigger.RESUME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LdioHttpInController.class)
@AutoConfigureMockMvc
class LdioHttpInputTest {
	private final String endpoint = "endpoint";
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private MockMvc mockMvc;
	private LdiAdapter adapter;
	private LdioInput input;

	@BeforeEach
	void setup() {
		adapter = Mockito.mock(LdiAdapter.class);
		ComponentExecutor executor = Mockito.mock(ComponentExecutor.class);

		when(adapter.apply(any())).thenReturn(Stream.empty());

		input = (LdioInput) new LdioHttpInAutoConfig.LdioHttpInConfigurator(eventPublisher, null)
				.configure(adapter, executor, eventPublisher, new ComponentProperties(endpoint, NAME));
	}

	@Test
	void testHttpEndpoint() throws Exception {
		String content = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
		String contentType = "application/n-quads";
		input.updateStatus(RESUME);

		mockMvc.perform(post("/%s".formatted(endpoint)).content(content).contentType(contentType)).andExpect(status().isAccepted());

		verify(adapter).apply(LdiAdapter.Content.of(content, contentType));
	}
	@Test
	void when_PipelineIsHalted_Then_MessageIsNotProcessed() throws Exception {
		String content = "_:b0 <http://schema.org/name> \"Jane Doe\" .";
		String contentType = "application/n-quads";
		input.updateStatus(HALT);

		mockMvc.perform(post("/%s".formatted(endpoint)).content(content).contentType(contentType)).andExpect(status().is(503));

		verifyNoInteractions(adapter);

		input.updateStatus(RESUME);

		mockMvc.perform(post("/%s".formatted(endpoint)).content(content).contentType(contentType)).andExpect(status().isAccepted());

		verify(adapter).apply(LdiAdapter.Content.of(content, contentType));
	}

}
