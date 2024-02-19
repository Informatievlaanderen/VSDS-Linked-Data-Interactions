package be.vlaanderen.informatievlaanderen.ldes.ldio.controller;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(path = "/admin/api/v1/catalog")
public class CatalogController {
	private final ConfigurableApplicationContext context;

	public CatalogController(ConfigurableApplicationContext context) {
		this.context = context;
	}

	@GetMapping
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
