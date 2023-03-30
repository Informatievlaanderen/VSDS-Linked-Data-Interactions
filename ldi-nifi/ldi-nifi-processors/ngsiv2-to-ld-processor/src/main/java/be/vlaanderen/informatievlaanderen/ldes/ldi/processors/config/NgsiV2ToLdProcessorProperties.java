package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

public class NgsiV2ToLdProcessorProperties {

	public static final String DEFAULT_DATA_IDENTIFIER = "data";
	public static final String DEFAULT_CORE_CONTEXT = "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld";

	private NgsiV2ToLdProcessorProperties() {
	}

	public static final PropertyDescriptor DATA_IDENTIFIER = new PropertyDescriptor.Builder()
			.name("DATA_IDENTIFIER")
			.displayName("Data array key")
			.description("Key of the data array in the data json")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.defaultValue(DEFAULT_DATA_IDENTIFIER)
			.build();

	public static final PropertyDescriptor CORE_CONTEXT = new PropertyDescriptor.Builder()
			.name("CORE_CONTEXT")
			.displayName("NGSI LD core context")
			.description("URL of the NGSI LD context")
			.required(false)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.defaultValue(DEFAULT_CORE_CONTEXT)
			.build();

	public static final PropertyDescriptor LD_CONTEXT = new PropertyDescriptor.Builder()
			.name("LD_CONTEXT")
			.displayName("Data-specific NGSI LD context")
			.description("URL of the NGSI LD context for this dataset")
			.required(false)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.build();

	public static String getDataIdentifier(final ProcessContext context) {
		return context.getProperty(DATA_IDENTIFIER).getValue();
	}

	public static String getCoreContext(final ProcessContext context) {
		return context.getProperty(CORE_CONTEXT).getValue();
	}

	public static String getLdContext(final ProcessContext context) {
		return context.getProperty(LD_CONTEXT).getValue();
	}
}
