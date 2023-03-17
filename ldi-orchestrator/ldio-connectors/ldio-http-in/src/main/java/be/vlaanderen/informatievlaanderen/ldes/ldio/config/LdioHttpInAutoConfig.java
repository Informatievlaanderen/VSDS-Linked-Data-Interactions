package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableConfigurationProperties()
@ComponentScan(value = "be.vlaanderen.informatievlaanderen.ldes")
public class LdioHttpInAutoConfig {


	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn")
	public LdioHttpInConfigurator ldioConfigurator() {
		return new LdioHttpInConfigurator();
	}

	public static class LdioHttpInConfigurator implements LdioInputConfigurator {

		@Autowired
		ConfigurableApplicationContext configContext;
		@Override
		public Object configure(LdiAdapter adapter,
				ComponentExecutor executor,
				ComponentProperties config) {

			SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();

			if (!beanRegistry.containsSingleton(config.getProperty("pipeline.name"))) {
				beanRegistry.registerSingleton(config.getProperty("pipeline.name"), mapping(adapter, executor, config));
			}
			return beanRegistry.getSingleton(config.getProperty("pipeline.name"));
		}

		RouterFunction<ServerResponse> mapping(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties config) {
			return route(POST("/%s".formatted(config.getProperty("pipeline.name"))),
					req -> {
						String contentType = req.headers().contentType().orElseThrow().toString();
						return req.bodyToMono(String.class)
								.doOnNext(System.out::println)
								.flatMap(body -> ServerResponse.noContent().build());
					});
		}

	}
}
