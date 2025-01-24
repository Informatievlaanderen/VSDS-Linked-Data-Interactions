package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class PersistenceProperties {
	private PersistenceProperties() {
	}

	public static final PropertyDescriptor STATE_PERSISTENCE_STRATEGY = new PropertyDescriptor.Builder()
			.name("PERSISTENCE_STRATEGY")
			.displayName("How state is persisted (note that memory is volatile).")
			.required(false)
			.allowableValues(
					Arrays.stream(StatePersistenceStrategy.values()).map(Enum::name).collect(Collectors.toSet()))
			.defaultValue(StatePersistenceStrategy.MEMORY.name())
			.build();

	public static final PropertyDescriptor DBCP_SERVICE = new PropertyDescriptor.Builder()
			.name("Database Connection Pooling Service")
			.description("The Controller Service that is used to obtain connection to database")
			.identifiesControllerService(DBCPService.class)
			.required(true)
			.dependsOn(STATE_PERSISTENCE_STRATEGY, StatePersistenceStrategy.POSTGRES.name(), StatePersistenceStrategy.SQLITE.name())
			.build();

	public static final PropertyDescriptor KEEP_STATE = new PropertyDescriptor.Builder()
			.name("KEEP_STATE")
			.displayName("Keep state")
			.description("Keep state when the processor is removed from the flow")
			.required(false)
			.allowableValues(TRUE.toString(), FALSE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(FALSE.toString())
			.build();


	public static StatePersistenceStrategy getStatePersistenceStrategy(final ProcessContext context) {
		return StatePersistenceStrategy.valueOf(context.getProperty(STATE_PERSISTENCE_STRATEGY).getValue());
	}

	public static boolean stateKept(final ProcessContext context) {
		return TRUE.equals(context.getProperty(KEEP_STATE).asBoolean());
	}

}
