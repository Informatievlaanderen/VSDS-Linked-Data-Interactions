package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "orchestrator")
public class OrchestratorConfig {
	private List<PipelineConfig> pipelines;

	public List<PipelineConfig> getPipelines() {
		return pipelines;
	}

	public void setPipelines(List<PipelineConfig> pipelines) {
		this.pipelines = pipelines;
	}
}
