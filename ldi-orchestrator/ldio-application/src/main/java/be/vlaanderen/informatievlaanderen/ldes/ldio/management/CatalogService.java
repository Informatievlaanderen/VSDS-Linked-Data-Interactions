package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Set;

@BrowserCallable
@AnonymousAllowed
public class CatalogService {
	private final ConfigurableApplicationContext context;

	public CatalogService(ConfigurableApplicationContext context) {
		this.context = context;
	}

	public Set<String> inputs() {
		return context.getBeansOfType(LdioInputConfigurator.class).keySet();
	}

	public Set<String> adapters() {
		return context.getBeansOfType(LdioAdapterConfigurator.class).keySet();
	}

	public Set<String> transformers() {
		return context.getBeansOfType(LdioTransformerConfigurator.class).keySet();
	}

	public Set<String> outputs() {
		return context.getBeansOfType(LdioOutputConfigurator.class).keySet();
	}
}
