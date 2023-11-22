package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/admin/api/v1/catalog")
public class CatalogController {
	private final ConfigurableApplicationContext context;

	public CatalogController(ConfigurableApplicationContext context) {
		this.context = context;
	}

	@GetMapping
	public Set<String> catalog() {
		return Stream.of(inputs(), adapters(), transformers(), outputs())
				.filter(Objects::nonNull)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	@GetMapping(path = "/inputs")
	public Set<String> inputs() {
		return context.getBeansOfType(LdioInputConfigurator.class).keySet();
	}

	@GetMapping(path = "/adapters")
	public Set<String> adapters() {
		return context.getBeansOfType(LdioAdapterConfigurator.class).keySet();
	}

	@GetMapping(path = "/transformers")
	public Set<String> transformers() {
		return context.getBeansOfType(LdioTransformerConfigurator.class).keySet();
	}

	@GetMapping(path = "/outputs")
	public Set<String> outputs() {
		return context.getBeansOfType(LdioOutputConfigurator.class).keySet();
	}
}
