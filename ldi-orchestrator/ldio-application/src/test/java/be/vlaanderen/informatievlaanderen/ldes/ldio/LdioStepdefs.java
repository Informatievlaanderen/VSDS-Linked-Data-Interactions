package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository.EXTENSION_YAML;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.repositories.PipelineFileRepository.EXTENSION_YML;
import static org.junit.Assert.assertEquals;

@CucumberContextConfiguration
public class LdioStepdefs {
	private File testDirectory;
	private ConfigurableWebApplicationContext context;
	private WebTestClient client;
	private PipelineFileRepository repository;
	private Map<File, PipelineConfigTO> initConfig;
	private URI managementURI;

	public void init() throws URISyntaxException {
		repository = context.getBean(PipelineFileRepository.class);
		initConfig = getInitialFiles();
		client = MockMvcWebTestClient.bindToApplicationContext(context).build();
		managementURI = new URI("/admin/api/v1/pipeline");
	}

	@After
	public void after() {
		if (repository != null)
			repository.getActivePipelines()
					.stream()
					.filter(pipelineConfigTO -> !initConfig.containsValue(pipelineConfigTO))
					.forEach(pipelineConfigTO -> repository.delete(pipelineConfigTO.name()));
		var dirFiles = testDirectory.listFiles();
		if (dirFiles != null && dirFiles.length == 0) {
			testDirectory.delete();
		}
		if (context != null && context.isRunning())
			context.close();
	}

	// Startup

	@When("I start LDIO")
	public void iStartLDIO() throws URISyntaxException {
		iStartLDIOWithTheProfile("empty-config");
	}

	@When("I start LDIO with the {string} profile")
	public void iStartLDIOWithTheProfile(String profile) throws URISyntaxException {
		testDirectory = new File("src/test/resources/startup/" + profile);
		context = MinStarterLdioApp.setupApp(profile);
		init();
	}

	// Create pipeline
	@And("I post a {string} {string} pipeline with a {int} response")
	public void iPostAFileTypePipelineWithAStatusCodeResponse(String file, String type, int statusCode) throws IOException {
		var contentType = switch (type) {
			case "json":
				yield "application/json";
			case "yml":
				yield "application/yaml";
			default:
				yield "";
		};

		postPipeline("src/test/resources/management/%s/%s.%s".formatted(type, file, type), contentType, statusCode);
	}

	// Delete Pipeline
	@And("I delete the {string} pipeline with a {int} response")
	public void iDeleteThePipeline(String pipeline, int statusCode) throws URISyntaxException {
		client.delete()
				.uri(new URI("%s/%s".formatted(managementURI.getPath(), pipeline)))
				.exchange()
				.expectStatus()
				.isEqualTo(statusCode);
	}

	// Assertions

	@And("^I expect (\\d+) pipelines$")
	public void iExpectPipelines(int count) {
		client.get()
				.uri(managementURI)
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentType("application/json")
				.expectBody()
				.jsonPath("$.length()")
				.isEqualTo(count);
	}

	@And("^The expected pipeline has (\\d+) transformers$")
	public void theExpectedPipelineHasOneTransformer(int count) {
		var transformer = repository.getActivePipelines().stream().findAny().orElseThrow();
		assertEquals(count, transformer.transformers().size());

	}

	private void postPipeline(String filePath, String contentType, int statusCode) throws IOException {
		var content = IOUtils.toString(new FileInputStream(filePath));
		client.post()
				.uri(managementURI)
				.contentType(MediaType.valueOf(contentType))
				.bodyValue(content)
				.exchange()
				.expectStatus()
				.isEqualTo(statusCode);
	}

	private Map<File, PipelineConfigTO> getInitialFiles() {
		testDirectory.mkdirs();
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		ObjectReader reader = mapper.readerFor(PipelineConfig.class);
		try (Stream<Path> files = Files.list(testDirectory.toPath())) {
			return files
					.filter(path -> !Files.isDirectory(path))
					.filter(path -> path.toFile().getName().endsWith(EXTENSION_YML)
							|| path.toFile().getName().endsWith(EXTENSION_YAML))
					.collect(Collectors.toMap(Path::toFile, path -> {
						try (Stream<String> content = Files.lines(path)) {
							var json = content.collect(Collectors.joining("\n"));
							return reader.readValue(json, PipelineConfigTO.class);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
