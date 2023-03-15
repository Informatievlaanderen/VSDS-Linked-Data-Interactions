package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PipelineController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RecordApplicationEvents
public class PipelineControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ApplicationEvents applicationEvents;

	private final String base_url = "/admin/api/v1/pipeline";


	@Test
	void when_PipeLineIsHalted_EventIsSent() throws Exception {
		mockMvc.perform(post(base_url + "/halt"))
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(post(base_url + "/halt"))
				.andExpect(status().isOk());

		MvcResult result = mockMvc.perform(get(base_url + "/status"))
				.andExpect(status().isOk())
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains(HALTED.name()));
		assertEquals(1, applicationEvents.stream(PipelineStatusEvent.class)
				.filter(statusEvent -> statusEvent.getStatus() == HALTED)
				.count());
	}

	@Test
	void whenPipelineIsResumedWhenHalted_EventIsSent() throws Exception {
		mockMvc.perform(post(base_url + "/halt"))
				.andExpect(status().isOk());

		MvcResult result = mockMvc.perform(post(base_url + "/resume"))
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(post(base_url + "/resume"))
				.andExpect(status().isOk())
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains(RESUMING.name()));
		assertEquals(1, applicationEvents.stream(PipelineStatusEvent.class)
				.filter(statusEvent -> statusEvent.getStatus() == RESUMING)
				.count());
	}

	@Test
	void whenPipelineIsResumedWhenRunning_EventIsNotSent() throws Exception {
		MvcResult result = mockMvc.perform(post(base_url + "/resume"))
				.andExpect(status().isOk())
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains(RUNNING.name()));
	}

}
