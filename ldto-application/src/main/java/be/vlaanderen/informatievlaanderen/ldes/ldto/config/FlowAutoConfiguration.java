package be.vlaanderen.informatievlaanderen.ldes.ldto.config;

import be.vlaanderen.informatievlaanderen.ldes.ldto.converter.ModelHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutorImpl;
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
	@ConditionalOnMissingBean(LdtoOutput.class)
	public LdtoOutput ldtoOutput(OrchestratorConfig orchestratorConfig, DefaultListableBeanFactory beanFactory)
			throws ClassNotFoundException {
		return (LdtoOutput) beanFactory.createBean(Class.forName(orchestratorConfig.getOutput().getName()));
	}

	public LdtoTransformer ldtoTransformer(String transformer, Map<String, String> config)
			throws ClassNotFoundException {
		LdtoTransformer ldtoTransformer = (LdtoTransformer) beanFactory.createBean(Class.forName(transformer));
		ldtoTransformer.init(config);
		return ldtoTransformer;
	}

	@Bean
	public ComponentExecutor componentExecutor(final OrchestratorConfig orchestratorConfig, LdtoOutput ldtoOutput) {
		List<LdtoTransformer> ldtoTransformers = orchestratorConfig.getTransformers().stream()
				.map(componentDefinition -> {
					try {
						return ldtoTransformer(componentDefinition.getName(), componentDefinition.getConfig());
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toList());
		return new ComponentExecutorImpl(ldtoTransformers, ldtoOutput);
	}

	@Bean
	public ModelHttpConverter memberConverter() {
		return new ModelHttpConverter();
	}

}
