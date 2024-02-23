package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.UUID.randomUUID;

@Configuration
@ConfigurationProperties(prefix = "orchestrator")
public class OrchestratorConfig {
	public static final String ORCHESTRATOR_NAME = "orchestrator.name";
	public static final String DEBUG = "debug";
	private String name = randomUUID().toString();
	private String directory = "pipelines";
	private List<PipelineConfig> pipelines;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public List<PipelineConfig> getPipelines() {
		return pipelines;
	}

	public void setPipelines(List<PipelineConfig> pipelines) {
		this.pipelines = pipelines;
	}
}
