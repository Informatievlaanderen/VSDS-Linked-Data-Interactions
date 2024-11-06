package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.Optional;

public class HttpSparqlOutProcessorProperties {
	private HttpSparqlOutProcessorProperties() {
	}

	public static final PropertyDescriptor ENDPOINT = new PropertyDescriptor.Builder()
			.name("ENDPOINT")
			.displayName("Endpoint")
			.description("Endpoint that must be used to write triples to")
			.required(true)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.build();

	public static final PropertyDescriptor GRAPH = new PropertyDescriptor.Builder()
			.name("GRAPH")
			.displayName("Graph")
			.description("Graph that must be used to write triples to")
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.addValidator(StandardValidators.URI_VALIDATOR)
			.build();

	public static final PropertyDescriptor SKOLEMISATION_DOMAIN = new PropertyDescriptor.Builder()
			.name("SKOLEMISATION_DOMAIN")
			.displayName("Skolemisation domain")
			.description("Domain to use for skolemisation, if ommitted, then skolemisation is disabled")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.addValidator(StandardValidators.URI_VALIDATOR)
			.build();

	public static final PropertyDescriptor REPLACEMENT_ENABLED = new PropertyDescriptor.Builder()
			.name("REPLACEMENT_ENABLED")
			.displayName("Replacement enabled")
			.description("Whether replacment should be enabled")
			.required(true)
			.defaultValue(Boolean.TRUE.toString())
			.allowableValues(Boolean.TRUE.toString(), Boolean.FALSE.toString())
			.build();

	public static final PropertyDescriptor REPLACEMENT_DEPTH = new PropertyDescriptor.Builder()
			.name("REPLACEMENT_DEPTH")
			.displayName("Replacement depth")
			.description("Number of levels of nested nodes that must be deleted during the replacement process")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.addValidator(StandardValidators.INTEGER_VALIDATOR)
			.defaultValue("10")
			.dependsOn(REPLACEMENT_ENABLED, Boolean.TRUE.toString())
			.build();

	public static final PropertyDescriptor REPLACEMENT_DELETE_FUNCTION = new PropertyDescriptor.Builder()
			.name("REPLACEMENT_DELETE_FUNCTION")
			.displayName("Replacement delete function")
			.description("Custom delete function that must be used during the replacement process instead of the function created by the replacement depth")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.dependsOn(REPLACEMENT_ENABLED, Boolean.TRUE.toString())
			.build();

	public static String getEndpoint(ProcessContext processContext) {
		return processContext.getProperty(ENDPOINT).getValue();
	}

	public static Optional<String> getGraph(ProcessContext processContext) {
		if(processContext.getProperty(GRAPH).isSet()) {
			return Optional.of(processContext.getProperty(GRAPH).getValue());
		}
		return Optional.empty();
	}

	public static Optional<String> getSkolemisationDomain(ProcessContext processContext) {
		if(processContext.getProperty(SKOLEMISATION_DOMAIN).isSet()) {
			return Optional.of(processContext.getProperty(SKOLEMISATION_DOMAIN).getValue());
		}
		return Optional.empty();
	}

	public static boolean isReplacementEnabled(ProcessContext processContext) {
		return Boolean.TRUE.equals(processContext.getProperty(REPLACEMENT_ENABLED).asBoolean());
	}

	public static int getReplacementDepth(ProcessContext processContext) {
		return processContext.getProperty(REPLACEMENT_DEPTH).asInteger();
	}

	public static Optional<String> getReplacementDeleteFunction(ProcessContext processContext) {
		if(processContext.getProperty(REPLACEMENT_DELETE_FUNCTION).isSet()) {
			return Optional.of(processContext.getProperty(REPLACEMENT_DELETE_FUNCTION).getValue());
		}
		return Optional.empty();
	}
}
