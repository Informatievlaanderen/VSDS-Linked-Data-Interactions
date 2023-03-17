package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.ComponentExecutorImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ComponentScan
public class FlowAutoConfiguration {

	private final ApplicationContext applicationContext;

	public FlowAutoConfiguration(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Bean
	public List<Object> ldtoInput(OrchestratorConfig config) {
		return config.getPipelines()
				.stream()
				.map(this::getLdiInput)
				.collect(Collectors.toList());
	}

	public ComponentExecutor componentExecutor(final PipelineConfig pipelineConfig) {
		List<LdiTransformer> ldiTransformers = pipelineConfig.getTransformers()
				.stream()
				.map(this::ldtoTransformer)
				.toList();
		List<LdiOutput> ldiOutputs = pipelineConfig.getOutputs()
				.stream()
				.map(this::ldtoOutput)
				.toList();
		return new ComponentExecutorImpl(ldiTransformers, ldiOutputs);
	}

	private LdiTransformer ldtoTransformer(ComponentDefinition componentDefinition) {
		return (LdiTransformer) getLdiComponent(componentDefinition.getName(), componentDefinition.getConfig());
	}

	private LdiOutput ldtoOutput(ComponentDefinition componentDefinition) {
		return (LdiOutput) getLdiComponent(componentDefinition.getName(), componentDefinition.getConfig());
	}

	public Object getLdiInput(PipelineConfig config) {
		LdioInputConfigurator configurator = (LdioInputConfigurator) applicationContext.getBean(
				config.getInput().getName());
		LdiAdapter adapter = (LdiAdapter) getLdiComponent(config.getInput().getAdapter().getName(),
				config.getInput().getAdapter().getConfig());
		ComponentExecutor executor = componentExecutor(config);

		Map<String, String> inputConfig = new HashMap<>(config.getInput().getConfig().getConfig());
		inputConfig.put("pipeline.name", config.getName());

		return configurator.configure(adapter, executor, new ComponentProperties(inputConfig));
	}

	private LdiComponent getLdiComponent(String beanName, ComponentProperties config) {
		LdioConfigurator ldioConfigurator = (LdioConfigurator) applicationContext.getBean(beanName);
		return ldioConfigurator.configure(config);
	}

}
