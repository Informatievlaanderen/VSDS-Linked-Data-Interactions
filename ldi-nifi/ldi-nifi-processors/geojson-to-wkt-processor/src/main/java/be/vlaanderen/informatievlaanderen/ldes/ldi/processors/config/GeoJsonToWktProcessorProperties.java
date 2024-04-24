package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

public class GeoJsonToWktProcessorProperties {

	private GeoJsonToWktProcessorProperties() {
	}

	public static final PropertyDescriptor DATA_SOURCE_FORMAT = new PropertyDescriptor.Builder()
			.name("DATA_SOURCE_FORMAT")
			.displayName("Data source format")
			.required(true)
			.defaultValue(Lang.NQUADS.getHeaderString())
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor TRANSFORM_TO_RDF_WKT = new PropertyDescriptor.Builder()
			.name("TRANSFORM_TO_RDF_WKT")
			.displayName("Transform GeoJson to RDF+WKT format, defaults to false and the default format is WKT")
			.required(false)
			.defaultValue("false")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static Lang getDataSourceFormat(final ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_SOURCE_FORMAT).getValue());
	}

	public static boolean getTransformToRdfWkt(final ProcessContext context) {
		return context.getProperty(TRANSFORM_TO_RDF_WKT).asBoolean();
	}

}
