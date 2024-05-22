package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

public class ArchiveFileOutProperties {

	private ArchiveFileOutProperties() {
	}

	public static final PropertyDescriptor TIMESTAMP_PATH = new PropertyDescriptor.Builder()
			.name("TIMESTAMP_PATH")
			.displayName("Timestamp path")
			.description("The timestamp path used for naming the file")
			.required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor ARCHIVE_ROOT_DIR = new PropertyDescriptor.Builder()
			.name("ARCHIVE_ROOT_DIR")
			.displayName("The archive directory")
			.required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static String getArchiveRootDirectory(final ProcessContext context) {
		return context.getProperty(ARCHIVE_ROOT_DIR).getValue();
	}

	public static String getTimestampPath(final ProcessContext context) {
		return context.getProperty(TIMESTAMP_PATH).getValue();
	}
}
