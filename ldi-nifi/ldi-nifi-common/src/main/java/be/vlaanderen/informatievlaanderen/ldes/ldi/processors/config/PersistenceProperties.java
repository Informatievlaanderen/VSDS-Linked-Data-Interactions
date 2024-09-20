package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class PersistenceProperties {
	private PersistenceProperties() {
	}

	public static final PropertyDescriptor KEEP_STATE = new PropertyDescriptor.Builder()
			.name("KEEP_STATE")
			.displayName("Keep state when the processor is removed from the flow")
			.required(false)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(FALSE.toString())
			.build();


	public static boolean stateKept(final ProcessContext context) {
		return TRUE.equals(context.getProperty(KEEP_STATE).asBoolean());
	}

}
