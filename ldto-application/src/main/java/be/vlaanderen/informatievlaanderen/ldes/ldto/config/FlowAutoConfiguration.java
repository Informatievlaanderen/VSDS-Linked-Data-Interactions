package be.vlaanderen.informatievlaanderen.ldes.ldto.config;

import be.vlaanderen.informatievlaanderen.ldes.ldto.converter.ModelHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutorImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoInput;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ComponentScan
public class FlowAutoConfiguration {

	@Autowired
	private DefaultListableBeanFactory beanFactory;

	@Bean
	@ConditionalOnMissingBean(LdtoInput.class)
	public LdtoInput ldtoInput(OrchestratorConfig orchestratorConfig)
			throws ClassNotFoundException {
		return (LdtoInput) beanFactory.createBean(Class.forName(orchestratorConfig.getInput().getName()));
	}

	@Bean
	public ComponentExecutor componentExecutor(final OrchestratorConfig orchestratorConfig) {
		List<LdtoTransformer> ldtoTransformers = orchestratorConfig.getTransformers()
				.stream()
				.map(this::ldtoTransformer)
				.toList();
		List<LdtoOutput> ldtoOutputs = orchestratorConfig.getOutputs()
				.stream()
				.map(this::ldtoOutput)
				.toList();
		return new ComponentExecutorImpl(ldtoTransformers, ldtoOutputs);
	}

	@Bean
	public ModelHttpConverter memberConverter() {
		return new ModelHttpConverter();
	}

	private LdtoTransformer ldtoTransformer(ComponentDefinition componentDefinition) {
		try {
			LdtoTransformer ldtoTransformer = (LdtoTransformer) beanFactory.createBean(Class.forName(
					componentDefinition.getName()));
			ldtoTransformer.init(componentDefinition.getConfig());
			return ldtoTransformer;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private LdtoOutput ldtoOutput(ComponentDefinition componentDefinition) {
		try {
			LdtoOutput ldtoOutput = (LdtoOutput) beanFactory.createBean(Class.forName(
					componentDefinition.getName()));
			ldtoOutput.init(componentDefinition.getConfig());
			return ldtoOutput;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
