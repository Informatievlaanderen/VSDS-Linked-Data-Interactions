package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.reactive.server.WebTestClient;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.*;

@WebFluxTest
@ComponentScan("be.vlaanderen.informatievlaanderen")
class PipelineControllerTest {
	@Autowired
	WebTestClient client;
	private final String base_url = "/admin/api/v1/pipeline/";
	private final String resume_url = base_url + "resume";
	private final String halt_url = base_url + "halt";

	@Test
	@Disabled("fix urgently")
	void when_PipeLineIsHalted_EventIsSent() {
		client.post()
				.uri(halt_url)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(PipelineStatus.class)
				.isEqualTo(HALTED);

		client.post()
				.uri(halt_url)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(PipelineStatus.class)
				.isEqualTo(HALTED);

		String status_url = base_url + "status";
		client.get()
				.uri(status_url)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(PipelineStatus.class)
				.isEqualTo(HALTED);
	}

	@Test
	@Disabled("fix urgently")
	void when_PipelineIsResumedWhenHalted_ReturnsResuming() {
		client.post()
				.uri(halt_url)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(PipelineStatus.class)
				.isEqualTo(HALTED);

		client.post().uri(resume_url)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(PipelineStatus.class)
				.isEqualTo(RESUMING);

		client.post().uri(resume_url)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(PipelineStatus.class)
				.isEqualTo(RESUMING);
	}

	@Test
	@Disabled("fix urgently")
	void when_PipelineIsResumedWhenRunning_EventIsNotSent() {
		client.post().uri(resume_url)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(PipelineStatus.class)
				.isEqualTo(RUNNING);
	}

}
