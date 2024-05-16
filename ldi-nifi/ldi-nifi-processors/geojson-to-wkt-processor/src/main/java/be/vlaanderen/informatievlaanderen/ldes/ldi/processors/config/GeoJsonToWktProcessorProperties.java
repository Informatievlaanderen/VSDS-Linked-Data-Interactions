package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.validators.RDFLanguageValidator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class GeoJsonToWktProcessorProperties {

	private GeoJsonToWktProcessorProperties() {
	}

	public static final PropertyDescriptor DATA_SOURCE_FORMAT = new PropertyDescriptor.Builder()
			.name("DATA_SOURCE_FORMAT")
			.displayName("Data source format")
			.description("RDF format identifier of the data source")
			.required(true)
			.defaultValue(Lang.NQUADS.getHeaderString())
			.addValidator(new RDFLanguageValidator())
			.build();

	public static final PropertyDescriptor TRANSFORM_TO_RDF_WKT = new PropertyDescriptor.Builder()
			.name("TRANSFORM_TO_RDF_WKT")
			.displayName("Transform to RDF wkt")
			.description("Transform GeoJson to RDF+WKT format when enabled, otherwise the GeoJson is transformed to WKT")
			.required(false)
			.defaultValue(FALSE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static Lang getDataSourceFormat(final ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_SOURCE_FORMAT).getValue());
	}

	public static boolean getTransformToRdfWkt(final ProcessContext context) {
		return TRUE.equals(context.getProperty(TRANSFORM_TO_RDF_WKT).asBoolean());
	}

}
