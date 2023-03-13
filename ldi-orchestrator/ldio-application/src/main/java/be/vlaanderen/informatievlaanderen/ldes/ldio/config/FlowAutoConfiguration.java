package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.ComponentExecutorImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan
public class FlowAutoConfiguration {

	private final ApplicationContext applicationContext;

	public FlowAutoConfiguration(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Bean
	@ConditionalOnMissingBean(LdiInput.class)
	@DependsOn({ "componentExecutor" })
	public LdiInput ldtoInput(OrchestratorConfig orchestratorConfig) {
		return (LdiInput) getLdiComponent(orchestratorConfig.getInput().getName(),
				orchestratorConfig.getInput().getConfig());
	}

	@Bean("componentExecutor")
	public ComponentExecutor componentExecutor(final OrchestratorConfig orchestratorConfig) {
		List<LdiTransformer> ldiTransformers = orchestratorConfig.getTransformers()
				.stream()
				.map(this::ldtoTransformer)
				.toList();
		List<LdiOutput> ldiOutputs = orchestratorConfig.getOutputs()
				.stream()
				.map(this::ldtoOutput)
				.toList();
		return new ComponentExecutorImpl(ldiTransformers, ldiOutputs);
	}

	@Bean
	public LdiAdapter ldiAdapter(final OrchestratorConfig orchestratorConfig) {
		ComponentDefinition adapterDefinition = orchestratorConfig.getInput().getAdapter();

		return (LdiAdapter) getLdiComponent(adapterDefinition.getName(), adapterDefinition.getConfig());
	}

	private LdiTransformer ldtoTransformer(ComponentDefinition componentDefinition) {
		return (LdiTransformer) getLdiComponent(componentDefinition.getName(), componentDefinition.getConfig());
	}

	private LdiOutput ldtoOutput(ComponentDefinition componentDefinition) {
		return (LdiOutput) getLdiComponent(componentDefinition.getName(), componentDefinition.getConfig());
	}

	private LdiComponent getLdiComponent(String beanName, ComponentProperties config) {
		LdioConfigurator ldioConfigurator = (LdioConfigurator) applicationContext.getBean(beanName);
		return ldioConfigurator.configure(config);
	}

}
