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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan
public class FlowAutoConfiguration {
	private final OrchestratorConfig config;
	private final ConfigurableApplicationContext configContext;

	public FlowAutoConfiguration(OrchestratorConfig config,
			ConfigurableApplicationContext configContext) {
		this.config = config;
		this.configContext = configContext;
	}

	@PostConstruct
	public void registerInputBeans() {
		config.getPipelines().forEach(this::initialiseLdiInput);
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

	public void initialiseLdiInput(PipelineConfig config) {
		SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();
		LdioInputConfigurator configurator = (LdioInputConfigurator) configContext.getBean(
				config.getInput().getName());
		LdiAdapter adapter = (LdiAdapter) getLdiComponent(config.getInput().getAdapter().getName(),
				config.getInput().getAdapter().getConfig());
		ComponentExecutor executor = componentExecutor(config);

		String pipeLineName = config.getName();

		Map<String, String> inputConfig = new HashMap<>(config.getInput().getConfig().getConfig());
		inputConfig.put("pipeline.name", pipeLineName);

		Object ldiInput = configurator.configure(adapter, executor, new ComponentProperties(inputConfig));

		if (!beanRegistry.containsSingleton(pipeLineName)) {
			beanRegistry.registerSingleton(pipeLineName, ldiInput);
		}
	}

	private LdiComponent getLdiComponent(String beanName, ComponentProperties config) {
		LdioConfigurator ldioConfigurator = (LdioConfigurator) configContext.getBean(beanName);
		return ldioConfigurator.configure(config);
	}

}
