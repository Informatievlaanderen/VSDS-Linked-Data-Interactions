package be.vlaanderen.informatievlaanderen.ldes.ldto.config;

import be.vlaanderen.informatievlaanderen.ldes.ldto.converter.ModelHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutorImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoInput;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ComponentScan
public class FlowAutoConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	@ConditionalOnMissingBean(LdtoInput.class)
	public LdtoInput ldtoInput(OrchestratorConfig orchestratorConfig) {
		return (LdtoInput) applicationContext.getBean(orchestratorConfig.getInput().getName());
	}

	@Bean
	public ComponentExecutor componentExecutor(final ApplicationContext applicationContext,
			final OrchestratorConfig orchestratorConfig) {
		LdtoOutput ldtoOutput = (LdtoOutput) applicationContext.getBean(orchestratorConfig.getOutput().getName());
		List<LdtoTransformer> ldtoTransformers = orchestratorConfig.getTransformers().stream()
				.map(componentDefinition -> (LdtoTransformer) applicationContext
						.getBean(componentDefinition.getName(), componentDefinition.getConfig()))
				.collect(Collectors.toList());
		return new ComponentExecutorImpl(ldtoTransformers, ldtoOutput);
	}

	@Bean
	public ModelHttpConverter memberConverter() {
		return new ModelHttpConverter();
	}

}
