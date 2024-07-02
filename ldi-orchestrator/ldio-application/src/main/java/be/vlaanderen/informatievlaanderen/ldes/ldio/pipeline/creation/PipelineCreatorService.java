package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.*;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.model.*;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.InvalidComponentException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.InvalidPipelineNameException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.LdiAdapterMissingException;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.OrchestratorConfig.DEBUG;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.OrchestratorConfig.ORCHESTRATOR_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig.NAME_PATTERN;

@Service
public class PipelineCreatorService {
	private static final Logger log = LoggerFactory.getLogger(PipelineCreatorService.class);
	private final Pattern validPipelineNamePattern = Pattern.compile(NAME_PATTERN);
	private final String orchestratorName;
	private final ConfigurableApplicationContext configContext;
	private final ApplicationEventPublisher eventPublisher;
	private final ObservationRegistry observationRegistry;
	private final DefaultListableBeanFactory beanFactory;

	public PipelineCreatorService(OrchestratorConfig orchestratorConfig, ConfigurableApplicationContext configContext, ApplicationEventPublisher eventPublisher, ObservationRegistry observationRegistry) {
		this.orchestratorName = orchestratorConfig.getName();
		this.configContext = configContext;
		this.eventPublisher = eventPublisher;
		this.observationRegistry = observationRegistry;
		this.beanFactory = (DefaultListableBeanFactory) configContext.getBeanFactory();
	}

	/**
	 * Initializes a pipeline with the provided pipeline config
	 *
	 * @param config Definition of the pipeline
	 * @throws InvalidComponentException    when no configurator could be found for the defined component name
	 * @throws InvalidPipelineNameException when the pipeline name does not match RegEx {@link PipelineConfig#NAME_PATTERN}
	 * @throws LdiAdapterMissingException   when a ldi adapter is expected, but not configured
	 */
	public void initialisePipeline(PipelineConfig config) {
		try {
			String pipeLineName = config.getName();
			validateName(pipeLineName);

			String inputName = config.getInput().getName();
			LdioInputConfigurator configurator = (LdioInputConfigurator) configContext.getBean(inputName);

			LdiAdapter adapter = Optional.ofNullable(config.getInput().getAdapter())
					.map(this::getLdioAdapter)
					.orElse(null);

			ComponentExecutor executor = componentExecutor(config);

			Map<String, String> inputConfig = new HashMap<>(config.getInput().getConfig().getConfig());
			inputConfig.put(ORCHESTRATOR_NAME, orchestratorName);

			verifyAdapter(config, configurator);

			LdioInput ldiInput = configurator.configure(adapter, executor, eventPublisher, new ComponentProperties(pipeLineName, inputName, inputConfig));

			registerBean(pipeLineName, ldiInput);
			eventPublisher.publishEvent(new InputCreatedEvent(config.getName(), ldiInput));
		} catch (NoSuchBeanDefinitionException e) {
			throw new InvalidComponentException(config.getName(), e.getBeanName());
		}
	}

	private static void verifyAdapter(PipelineConfig config, LdioInputConfigurator configurator) {
		final ComponentDefinition adapter = config.getInput().getAdapter();
		if (configurator.isAdapterRequired() && adapter == null) {
			throw new LdiAdapterMissingException(config.getName(), config.getInput().getName());
		}
		if (!configurator.isAdapterRequired() && adapter != null) {
			log.warn("Pipeline \"{}\": Input: \"{}\": \"{}\" ignored", config.getName(), config.getInput().getName(), adapter.getName());
		}
	}

	/**
	 * Removes the pipeline from the spring bean registry
	 *
	 * @param pipeline name of the pipeline to delete
	 */
	public void removePipeline(String pipeline) {
		LdioInput ldioInput = beanFactory.getBean(pipeline, LdioInput.class);
		ldioInput.shutdown();
		beanFactory.destroyBean(pipeline);
	}

	private ComponentExecutor componentExecutor(final PipelineConfig pipelineConfig) {
		List<LdioTransformer> ldioTransformers = pipelineConfig.getTransformers()
				.stream()
				.map(this::getLdioTransformer)
				.toList();

		List<LdiOutput> ldiOutputs = pipelineConfig.getOutputs()
				.stream()
				.map(componentDefinition -> addPipelineNameIfMissingToComponentDefinition(componentDefinition, pipelineConfig.getName()))
				.map(this::getLdioOutput)
				.toList();

		LdioSender ldioSender = new LdioSender(pipelineConfig.getName(), ldiOutputs);

		List<LdioTransformer> processorChain = new ArrayList<>(ldioTransformers.subList(0, ldioTransformers.size()));

		processorChain.add(ldioSender);

		LdioTransformer ldioTransformerPipeline = processorChain.get(0);

		if (processorChain.size() > 1) {
			ldioTransformerPipeline = LdioTransformer.link(processorChain.get(0), processorChain);
		}

		registerBean(pipelineConfig.getName() + "-ldiSender", ldioSender);

		eventPublisher.publishEvent(new SenderCreatedEvent(pipelineConfig.getName(), ldioSender));

		return new ComponentExecutorImpl(ldioTransformerPipeline);
	}

	private LdiAdapter getLdioAdapter(ComponentDefinition componentDefinition) {
		boolean debug = componentDefinition.getConfig().getOptionalBoolean(DEBUG).orElse(false);

		LdiAdapter adapter = (LdiAdapter) getLdiComponent(componentDefinition.getName(),
				componentDefinition.getConfig());

		return debug ? new AdapterDebugger(adapter) : adapter;
	}

	private LdioTransformer getLdioTransformer(ComponentDefinition componentDefinition) {
		boolean debug = componentDefinition.getConfig().getOptionalBoolean(DEBUG).orElse(false);

		LdioTransformer ldiTransformer = ((LdioTransformerConfigurator) configContext
				.getBean(componentDefinition.getName()))
				.configure(componentDefinition.getConfig());

		return debug ? new TransformerDebugger(ldiTransformer) : ldiTransformer;
	}

	private LdiOutput getLdioOutput(ComponentDefinition componentDefinition) {
		boolean debug = componentDefinition.getConfig().getOptionalBoolean(DEBUG).orElse(false);

		LdiOutput ldiOutput = (LdiOutput) getLdiComponent(componentDefinition.getName(),
				componentDefinition.getConfig());


		return debug ?
				new LdiOutputLogger(new OutputDebugger(ldiOutput), observationRegistry) :
				new LdiOutputLogger(ldiOutput, observationRegistry);
	}

	private LdiComponent getLdiComponent(String beanName, ComponentProperties config) {
		return ((LdioConfigurator) configContext.getBean(beanName)).configure(config);
	}

	private void registerBean(String pipelineName, Object bean) {
		if (!beanFactory.containsSingleton(pipelineName)) {
			beanFactory.registerSingleton(pipelineName, bean);
		}
	}

	private void validateName(String name) {
		Matcher matcher = validPipelineNamePattern.matcher(name);

		if (!matcher.matches()) {
			throw new InvalidPipelineNameException(name);
		}
	}

	/**
	 * When a pipeline is loaded from the spring properties, the pipeline name is missing, which will be added here.
	 * This method should be deleted if loading pipelines from the properties is not supported anymore
	 */
	private ComponentDefinition addPipelineNameIfMissingToComponentDefinition(ComponentDefinition componentDefinition, String pipelineName) {
		if (componentDefinition.getConfig().getPipelineName() == null) {
			componentDefinition.getConfig().setPipelineName(pipelineName);
		}
		return componentDefinition;
	}
}
