package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class GeoJsonToWktProcessorProperties {

	private GeoJsonToWktProcessorProperties() {
	}

	public static final PropertyDescriptor TRANSFORM_TO_RDF_WKT = new PropertyDescriptor.Builder()
			.name("TRANSFORM_TO_RDF_WKT")
			.displayName("Transform to RDF wkt")
			.description("Transform GeoJson to RDF+WKT format when enabled, otherwise the GeoJson is transformed to WKT")
			.required(false)
			.defaultValue(FALSE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static boolean getTransformToRdfWkt(final ProcessContext context) {
		return TRUE.equals(context.getProperty(TRANSFORM_TO_RDF_WKT).asBoolean());
	}

}
