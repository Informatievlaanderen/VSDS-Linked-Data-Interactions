package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RmlAdapterProperties {
	private RmlAdapterProperties() {
	}

	public static final PropertyDescriptor RML_MAPPING_CONTENT = new PropertyDescriptor.Builder()
			.name("RML_MAPPING_CONTENT")
			.displayName("RML mapping content")
			.description("Content of the RML mapping")
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.required(false)
			.build();

	public static final PropertyDescriptor RML_MAPPING_FILE = new PropertyDescriptor.Builder()
			.name("RML_MAPPING_FILE")
			.displayName("RML mapping file uri")
			.description("File uri of the RML mapping")
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
			.required(false)
			.build();

	public static String getRmlMapping(ProcessContext context) {
		if (context.getProperty(RML_MAPPING_CONTENT).isSet()) {
			return context.getProperty(RML_MAPPING_CONTENT).getValue();
		}

		final String fileUri = context.getProperty(RML_MAPPING_FILE).getValue();

		final Path path = Path.of(fileUri);

		if (!Files.isReadable(path)) {
			throw new IllegalArgumentException("File does not exist or is not readable: %s".formatted(fileUri));
		}

		try {
			return Files.readString(path);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
