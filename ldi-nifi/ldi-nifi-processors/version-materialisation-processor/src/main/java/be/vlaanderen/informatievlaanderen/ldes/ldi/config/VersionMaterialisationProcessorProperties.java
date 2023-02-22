package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.util.StandardValidators;

public class VersionMaterialisationProcessorProperties {

	private VersionMaterialisationProcessorProperties() {
	}

	public static final PropertyDescriptor IS_VERSION_OF = new PropertyDescriptor.Builder().name("isVersionOf")
			.displayName("Predicate used for isVersionOf")
			.required(true)
			.defaultValue("http://purl.org/dc/terms/isVersionOf")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.addValidator(StandardValidators.URI_VALIDATOR)
			.build();

	public static final PropertyDescriptor RESTRICT_OUTPUT_TO_MEMBER = new PropertyDescriptor.Builder()
			.name("MembersOnly")
			.displayName("Restrict output to members")
			.description("When enabled, only the member and the blank nodes references are included.")
			.required(false)
			.defaultValue("false")
			.allowableValues("true", "false")
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

}
