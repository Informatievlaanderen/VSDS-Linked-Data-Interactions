package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.ComponentExecutorImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan
public class FlowAutoConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	@ConditionalOnMissingBean(LdiInput.class)
	public LdiInput ldtoInput(OrchestratorConfig orchestratorConfig) {
		return (LdiInput) getBean(orchestratorConfig.getInput().getName(), orchestratorConfig.getInput().getConfig());
	}

	@Bean
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

	private LdiTransformer ldtoTransformer(ComponentDefinition componentDefinition) {
		return (LdiTransformer) getBean(componentDefinition.getName(), componentDefinition.getConfig());
	}

	private LdiOutput ldtoOutput(ComponentDefinition componentDefinition) {
		return (LdiOutput) getBean(componentDefinition.getName(), componentDefinition.getConfig());
	}

	private LdiComponent getBean(String beanName, ComponentProperties config) {
		LdioConfigurator ldioConfigurator = (LdioConfigurator) applicationContext.getBean(beanName);
		return ldioConfigurator.configure(config);
	}

}
