package be.vlaanderen.informatievlaanderen.ldes.ldio.catalog;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformerConfigurator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(path = "/admin/api/v1/catalog")
public class CatalogController implements OpenApiCatalogController {
	private final ConfigurableApplicationContext context;

	public CatalogController(ConfigurableApplicationContext context) {
		this.context = context;
	}

	@Override
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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
