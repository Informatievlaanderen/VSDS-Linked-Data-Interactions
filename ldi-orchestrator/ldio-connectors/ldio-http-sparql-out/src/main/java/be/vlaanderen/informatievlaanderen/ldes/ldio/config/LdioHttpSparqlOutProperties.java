package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;

import java.util.Optional;

public class LdioHttpSparqlOutProperties {
	public static final String GRAPH = "graph";
	public static final String SKOLEMISATION_SKOLEM_DOMAIN = "skolemisation.skolemDomain";
	public static final String REPLACEMENT_DEPTH = "replacement.depth";
	public static final String REPLACEMENT_ENABLED = "replacement.enabled";
	public static final String REPLACEMENT_DELETE_FUNCTION = "replacement.deleteFunction";
	public static final boolean DEFAULT_REPLACEMENT_ENABLED = true;
	public static final int DEFAULT_REPLACEMENT_DEPTH = 10;

	private final ComponentProperties componentProperties;

	public LdioHttpSparqlOutProperties(ComponentProperties componentProperties) {
		this.componentProperties = componentProperties;
	}

	public String getEndpoint() {
		return componentProperties.getProperty("endpoint");
	}

	public Optional<String> getGraph() {
		return componentProperties.getOptionalProperty(GRAPH);
	}

	public Optional<String> getSkolemisationDomain() {
		return componentProperties.getOptionalProperty(SKOLEMISATION_SKOLEM_DOMAIN);
	}

	public boolean isReplacementEnabled() {
		return componentProperties
				.getOptionalBoolean(REPLACEMENT_ENABLED)
				.orElse(DEFAULT_REPLACEMENT_ENABLED);
	}

	public int getReplacementDepth() {
		return componentProperties
				.getOptionalInteger(REPLACEMENT_DEPTH)
				.orElse(DEFAULT_REPLACEMENT_DEPTH);
	}

	public Optional<String> getReplacementDeleteFunction() {
		return componentProperties.getOptionalProperty(REPLACEMENT_DELETE_FUNCTION);
	}


}
