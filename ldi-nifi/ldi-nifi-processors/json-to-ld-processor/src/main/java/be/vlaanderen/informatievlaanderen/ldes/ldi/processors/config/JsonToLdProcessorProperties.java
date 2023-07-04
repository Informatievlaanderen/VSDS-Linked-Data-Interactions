package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

public class JsonToLdProcessorProperties {

	private JsonToLdProcessorProperties() {
	}

	public static final PropertyDescriptor CORE_CONTEXT = new PropertyDescriptor.Builder()
			.name("CORE_CONTEXT")
			.displayName("JSON-LD core context")
			.description("URL of the JSON-LD context")
			.required(true)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.build();

	public static final PropertyDescriptor LD_CONTEXT = new PropertyDescriptor.Builder()
			.name("LD_CONTEXT")
			.displayName("additional JSON-LD context")
			.description("URL of an additional JSON-LD context")
			.required(false)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.build();

	public static String getCoreContext(final ProcessContext context) {
		return context.getProperty(CORE_CONTEXT).getValue();
	}

	public static String getLdContext(final ProcessContext context) {
		return context.getProperty(LD_CONTEXT).getValue();
	}
}
