package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class VersionMaterialisationProcessorProperties {

	private VersionMaterialisationProcessorProperties() {
	}

	public static final PropertyDescriptor RESTRICT_TO_MEMBERS = new PropertyDescriptor.Builder()
			.name("RESTRICT_TO_MEMBERS")
			.displayName("Restrict output to members")
			.description("When enabled, only the member and the blank nodes references are included.")
			.required(false)
			.defaultValue(FALSE.toString())
			.allowableValues(FALSE.toString(), TRUE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static final PropertyDescriptor VERSION_OF_PROPERTY = new PropertyDescriptor.Builder()
			.name("VERSION_OF_PROPERTY")
			.required(true)
			.defaultValue("http://purl.org/dc/terms/isVersionOf")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.addValidator(StandardValidators.URI_VALIDATOR)
			.build();

	public static boolean restrictToMembers(final ProcessContext context) {
		return TRUE.equals(context.getProperty(RESTRICT_TO_MEMBERS).asBoolean());
	}

	public static String getVersionOfProperty(final ProcessContext context) {
		return context.getProperty(VERSION_OF_PROPERTY).getValue();
	}
}
