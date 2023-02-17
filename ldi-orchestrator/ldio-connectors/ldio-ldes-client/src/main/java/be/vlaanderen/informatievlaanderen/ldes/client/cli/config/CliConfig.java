package be.vlaanderen.informatievlaanderen.ldes.client.cli.config;

import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.EndpointRequester;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.client.cli")
public class CliConfig {

	@Bean
	ExecutorService executorService() {
		return Executors.newSingleThreadExecutor();
	}

	@Bean
	EndpointRequester endpointRequester() {
		return new EndpointRequester();
	}

}
