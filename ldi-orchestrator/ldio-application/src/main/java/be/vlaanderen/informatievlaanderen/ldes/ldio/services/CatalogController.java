package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<Set<String>> catalog() {
		return ResponseEntity.ok(Stream.of(inputs(), adapters(), transformers(), outputs())
				.map(HttpEntity::getBody)
				.filter(Objects::nonNull)
				.flatMap(Set::stream)
				.collect(Collectors.toSet()));
	}

	@GetMapping(path = "/inputs")
	public ResponseEntity<Set<String>> inputs() {
		return ResponseEntity.ok(context.getBeansOfType(LdioInputConfigurator.class).keySet());
	}

	@GetMapping(path = "/adapters")
	public ResponseEntity<Set<String>> adapters() {
		return ResponseEntity.ok(context.getBeansOfType(LdioAdapterConfigurator.class).keySet());
	}

	@GetMapping(path = "/transformers")
	public ResponseEntity<Set<String>> transformers() {
		return ResponseEntity.ok(context.getBeansOfType(LdioTransformerConfigurator.class).keySet());
	}

	@GetMapping(path = "/outputs")
	public ResponseEntity<Set<String>> outputs() {
		return ResponseEntity.ok(context.getBeansOfType(LdioOutputConfigurator.class).keySet());
	}
}
