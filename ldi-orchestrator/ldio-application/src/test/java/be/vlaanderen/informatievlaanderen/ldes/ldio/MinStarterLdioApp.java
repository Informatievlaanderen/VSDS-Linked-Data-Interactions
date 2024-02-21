package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.hibernate.service.ServiceRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import static org.mockito.Mockito.mock;

public class MinStarterLdioApp {
	static ConfigurableWebApplicationContext setupApp(String file) {
		System.setProperty("spring.main.allow-bean-definition-overriding", "true");

		SpringApplication app = new SpringApplicationBuilder(Application.class)
				.web(WebApplicationType.SERVLET)
				.properties("spring.config.location=classpath:/startup/" + file + ".yml")
				.build();

		return (ConfigurableWebApplicationContext) app.run();
	}

	@Bean
	ServiceRegistry serviceRegistry() {
		return mock(ServiceRegistry.class);
	}
}
