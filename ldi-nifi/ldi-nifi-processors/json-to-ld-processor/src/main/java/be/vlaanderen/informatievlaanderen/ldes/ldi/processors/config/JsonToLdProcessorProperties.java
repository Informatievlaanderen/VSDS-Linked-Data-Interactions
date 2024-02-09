package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import static java.lang.Boolean.TRUE;

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

	public static final PropertyDescriptor FORCE_CONTENT_TYPE = new PropertyDescriptor.Builder()
			.name("FORCE_CONTENT_TYPE")
			.displayName("Force content type")
			.description("Force content type of the flowfile to be handled as application/json")
			.required(false)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static String getCoreContext(final ProcessContext context) {
		return context.getProperty(CORE_CONTEXT).getValue();
	}

	public static boolean getForceContentType(final ProcessContext context) {
		return TRUE.equals(context.getProperty(FORCE_CONTENT_TYPE).asBoolean());
	}

}
