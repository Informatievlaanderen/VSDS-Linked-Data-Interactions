package be.vlaanderen.informatievlaanderen.ldes.ldto.config;

import be.vlaanderen.informatievlaanderen.ldes.ldto.converter.ModelHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldto.input.LdtoHttpIn;
import be.vlaanderen.informatievlaanderen.ldes.ldto.output.LdtoConsoleOut;
import be.vlaanderen.informatievlaanderen.ldes.ldto.output.LdtoHttpOut;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutorImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldto.transformer.SparqlConstructTransformer;
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
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ComponentScan
public class FlowAutoConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	@ConditionalOnMissingBean(LdtoInput.class)
	public LdtoInput ldtoInput(OrchestratorConfig orchestratorConfig, ComponentExecutor componentExecutor) {
		return switch (orchestratorConfig.getInput().getName()) {
			case "LdtoHttpIn" -> new LdtoHttpIn(componentExecutor);
			default -> throw new RuntimeException();
		};
	}

	public LdtoOutput ldtoOutput(OrchestratorConfig orchestratorConfig) {
		return switch (orchestratorConfig.getOutput().getName()) {
			case "LdtoConsoleOut" -> new LdtoConsoleOut(orchestratorConfig);
			case "LdtoHttpOut" -> new LdtoHttpOut(orchestratorConfig);
			default -> throw new RuntimeException();
		};
	}

	public LdtoTransformer ldtoTransformer(String transformer, Map<String, String> config) {
		return switch (transformer) {
			case "SparqlConstructTransformer" -> new SparqlConstructTransformer(config);
			default -> throw new RuntimeException();
		};
	}

	@Bean
	public ComponentExecutor componentExecutor(final OrchestratorConfig orchestratorConfig) {
		LdtoOutput ldtoOutput = ldtoOutput(orchestratorConfig);
		List<LdtoTransformer> ldtoTransformers = orchestratorConfig.getTransformers().stream()
				.map(componentDefinition -> ldtoTransformer(componentDefinition.getName(), componentDefinition.getConfig()))
				.collect(Collectors.toList());
		return new ComponentExecutorImpl(ldtoTransformers, ldtoOutput);
	}

	@Bean
	public ModelHttpConverter memberConverter() {
		return new ModelHttpConverter();
	}

}
