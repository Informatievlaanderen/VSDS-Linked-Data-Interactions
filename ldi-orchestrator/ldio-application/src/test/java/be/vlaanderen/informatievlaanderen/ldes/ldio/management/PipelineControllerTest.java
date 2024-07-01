package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineController;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PipelineController.class})
@AutoConfigureMockMvc
@ComponentScan({
		"be.vlaanderen.informatievlaanderen.ldes.ldio",
		"be.vlaanderen.informatievlaanderen.ldes.ldio.config",
		"be.vlaanderen.informatievlaanderen.ldes.ldio.repositories",
		"be.vlaanderen.informatievlaanderen.ldes.ldio.services"
})
@ActiveProfiles("rest")
class PipelineControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void given_PredefinedPipeline_when_GetOverview_And_JSON_then_ReturnJson() throws Exception {
		mockMvc.perform(get("/admin/api/v1/pipeline").accept("application/json"))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(content().json(readAllPipelinesInJson("management/json/valid.json")));
	}

	@Test
	void given_ValidPipelineConfig_when_PostPipeline_then_ReturnPipelineWithHttpStatus201() throws Exception {
		mockMvc.perform(
						post("/admin/api/v1/pipeline")
								.contentType("application/yaml")
								.content(readPipeline("management/yml/valid-2.yml"))
								.accept("application/json"))
				.andExpect(status().isCreated())
				.andExpect(content().json(readPipeline("management/json/valid-2.json")));
	}

	@Test
	void given_PipelineConfigWithMissingLdiAdapter_when_PostPipeline_ReturnStatus400() throws Exception {
		mockMvc.perform(
						post("/admin/api/v1/pipeline")
								.contentType("application/yaml")
								.content(readPipeline("management/yml/missing-adapter.yml"))
								.accept("application/yaml"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Pipeline \"invalid-pipeline\": Input: \"dummyIn\": Missing LDI Adapter"));
	}

	private String readAllPipelinesInJson(String... filenames) {
		return "[" + Stream.of(filenames)
				.map(this::readPipeline)
				.collect(Collectors.joining(", ")) + "]";
	}

	private String readPipeline(String filename) {
		try {
			final File jsonFile = ResourceUtils.getFile("classpath:" + filename);
			return FileUtils.readFileToString(jsonFile, "utf-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}