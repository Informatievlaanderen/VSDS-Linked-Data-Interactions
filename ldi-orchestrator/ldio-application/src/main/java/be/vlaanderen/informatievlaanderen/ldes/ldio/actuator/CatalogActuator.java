package be.vlaanderen.informatievlaanderen.ldes.ldio.actuator;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Endpoint(id = "catalog")
public class CatalogActuator {
	private final ConfigurableApplicationContext context;

	public CatalogActuator(ConfigurableApplicationContext context) {
		this.context = context;
	}

	@ReadOperation
	public LdioCatalog catalog() {
		return new LdioCatalog(inputs(), adapters(), transformers(), outputs());
	}

	private Set<String> inputs() {
		return context.getBeansOfType(LdioInputConfigurator.class).keySet();
	}

	private Set<String> adapters() {
		return context.getBeansOfType(LdioAdapterConfigurator.class).keySet();
	}

	private Set<String> transformers() {
		return context.getBeansOfType(LdioTransformerConfigurator.class).keySet();
	}

	private Set<String> outputs() {
		return context.getBeansOfType(LdioOutputConfigurator.class).keySet();
	}

	public record LdioCatalog(Set<String> inputs, Set<String> adapters, Set<String> transformers, Set<String> outputs) {
	}
}
