package be.vlaanderen.informatievlaanderen.ldes.ldto.config;

import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutorImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoInput;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@ComponentScan
public class FlowAutoConfiguration {

	@Autowired
	private DefaultListableBeanFactory beanFactory;

	@Bean
	@ConditionalOnMissingBean(LdtoInput.class)
	public LdtoInput ldtoInput(OrchestratorConfig orchestratorConfig)
			throws ClassNotFoundException {
		return (LdtoInput) getBean(orchestratorConfig.getInput().getName(), orchestratorConfig.getInput().getConfig());
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

	private LdtoTransformer ldtoTransformer(ComponentDefinition componentDefinition) {
		try {
			return (LdtoTransformer) getClass(componentDefinition.getName(), componentDefinition.getConfig());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private LdtoOutput ldtoOutput(ComponentDefinition componentDefinition) {
		try {
			return (LdtoOutput) getClass(componentDefinition.getName(), componentDefinition.getConfig());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private Object getClass(String beanName, Object... args) throws ClassNotFoundException {
		try {
			Constructor<?>[] constructors = Class.forName(beanName).getConstructors();

			if (Arrays.stream(args).noneMatch(Objects::isNull)) {
				return Arrays.stream(constructors)
						.filter(constructor -> matchesConstructor(constructor, args))
						.findFirst()
						.orElseThrow()
						.newInstance(args);
			}
			else {
				return Arrays.stream(constructors)
						.filter(constructor -> constructor.getParameterCount() == 0)
						.findFirst()
						.orElseThrow(() -> new RuntimeException("No config was defined for " + beanName + ". No default contructor present to fall back on"));
			}

		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private Object getBean(String beanName, Object... args) throws ClassNotFoundException {
		if (!beanFactory.containsBeanDefinition(beanName)) {
			GenericBeanDefinition gbd = new GenericBeanDefinition();
			gbd.setBeanClass(Class.forName(beanName));

			beanFactory.registerBeanDefinition(beanName, gbd);
		}

		return beanFactory.getBean(beanName, args);
	}

	private boolean matchesConstructor(Constructor c, Object... args) {
		if (c.getParameterCount() != args.length) {
			return false;
		}

		for (int i=0; i < c.getParameterCount(); i++) {
			if (c.getParameterTypes()[i].isAssignableFrom(args[i].getClass())) {
				return true;
			}
		}

		return false;
	}

}
