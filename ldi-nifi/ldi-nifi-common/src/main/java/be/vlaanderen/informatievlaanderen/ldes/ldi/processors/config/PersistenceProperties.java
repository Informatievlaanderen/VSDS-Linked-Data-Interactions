package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.Arrays;
import java.util.Optional;
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

	public static final PropertyDescriptor POSTGRES_URL = new PropertyDescriptor.Builder()
			.name("POSTGRES_URL")
			.displayName("Postgres database url")
			.description("Postgres database url formatted as \"jdbc:postgresql://localhost:5432/postgres\"")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.dependsOn(STATE_PERSISTENCE_STRATEGY, StatePersistenceStrategy.POSTGRES.name())
			.build();

	public static final PropertyDescriptor POSTGRES_USERNAME = new PropertyDescriptor.Builder()
			.name("POSTGRES_USERNAME")
			.displayName("Postgres database username")
			.description("Username used to connect to the postgres database")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.dependsOn(STATE_PERSISTENCE_STRATEGY, StatePersistenceStrategy.POSTGRES.name())
			.build();

	public static final PropertyDescriptor POSTGRES_PASSWORD = new PropertyDescriptor.Builder()
			.name("POSTGRES_PASSWORD")
			.displayName("Postgres database password")
			.description("Password used to connect to the postgres database")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.sensitive(true)
			.dependsOn(STATE_PERSISTENCE_STRATEGY, StatePersistenceStrategy.POSTGRES.name())
			.build();

	public static final PropertyDescriptor SQLITE_DIRECTORY = new PropertyDescriptor.Builder()
			.name("SQLITE_DIRECTORY")
			.displayName("Sqlite database directory")
			.description("Sqlite database directory where the '.db' file can be stored")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.dependsOn(STATE_PERSISTENCE_STRATEGY, StatePersistenceStrategy.SQLITE.name())
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


	public static String getPostgresUrl(final ProcessContext context) {
		return context.getProperty(POSTGRES_URL).getValue();
	}

	public static String getPostgresUsername(final ProcessContext context) {
		return context.getProperty(POSTGRES_USERNAME).getValue();
	}

	public static String getPostgresPassword(final ProcessContext context) {
		return context.getProperty(POSTGRES_PASSWORD).getValue();
	}

	public static Optional<String> getSqliteDirectory(final ProcessContext context) {
		return Optional.ofNullable(context.getProperty(SQLITE_DIRECTORY).getValue());
	}

}
