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
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.LdiSender;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.UUID.randomUUID;

@Configuration
@ComponentScan
public class FlowAutoConfiguration {
	private final OrchestratorConfig orchestratorConfig;
	private final ConfigurableApplicationContext configContext;
	private final ApplicationEventPublisher eventPublisher;

	public FlowAutoConfiguration(OrchestratorConfig orchestratorConfig,
			ConfigurableApplicationContext configContext, ApplicationEventPublisher eventPublisher) {
		this.orchestratorConfig = orchestratorConfig;
		this.configContext = configContext;
		this.eventPublisher = eventPublisher;
	}

	@PostConstruct
	public void registerInputBeans() {
		if (orchestratorConfig.getName() == null) {
			orchestratorConfig.setName(randomUUID().toString());
		}
		orchestratorConfig.getPipelines().forEach(this::initialiseLdiInput);
	}

	public ComponentExecutor componentExecutor(final PipelineConfig pipelineConfig) {
		List<LdiTransformer> ldiTransformers = pipelineConfig.getTransformers()
				.stream()
				.map(this::getLdioTransformer)
				.toList();
		List<LdiOutput> ldiOutputs = pipelineConfig.getOutputs()
				.stream()
				.map(this::getLdioOutput)
				.toList();

		LdiSender ldiSender = new LdiSender(eventPublisher, ldiOutputs);
		registerBean(pipelineConfig.getName() + "-ldiSender", ldiSender);

		return new ComponentExecutorImpl(ldiTransformers, ldiSender);
	}

	public void initialiseLdiInput(PipelineConfig config) {
		LdioInputConfigurator configurator = (LdioInputConfigurator) configContext.getBean(
				config.getInput().getName());
		LdiAdapter adapter = (LdiAdapter) getLdiComponent(config.getInput().getAdapter().getName(),
				config.getInput().getAdapter().getConfig());
		ComponentExecutor executor = componentExecutor(config);

		String pipeLineName = config.getName();

		Map<String, String> inputConfig = new HashMap<>(config.getInput().getConfig().getConfig());
		inputConfig.put("orchestrator.name", orchestratorConfig.getName());
		inputConfig.put("pipeline.name", pipeLineName);

		Object ldiInput = configurator.configure(adapter, executor, new ComponentProperties(inputConfig));

		registerBean(pipeLineName, ldiInput);
	}

	private LdiTransformer getLdioTransformer(ComponentDefinition componentDefinition) {
		return (LdiTransformer) getLdiComponent(componentDefinition.getName(), componentDefinition.getConfig());
	}

	private LdiOutput getLdioOutput(ComponentDefinition componentDefinition) {
		return (LdiOutput) getLdiComponent(componentDefinition.getName(), componentDefinition.getConfig());
	}

	private LdiComponent getLdiComponent(String beanName, ComponentProperties config) {
		LdioConfigurator ldioConfigurator = (LdioConfigurator) configContext.getBean(beanName);
		return ldioConfigurator.configure(config);
	}

	private void registerBean(String pipelineName, Object bean) {
		SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();
		if (!beanRegistry.containsSingleton(pipelineName)) {
			beanRegistry.registerSingleton(pipelineName, bean);
		}
	}

}
