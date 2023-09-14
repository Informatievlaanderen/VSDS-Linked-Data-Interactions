package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.GeoJsonToWktTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.GeoJsonToWktProcessorProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.GeoJsonToWktProcessorProperties.DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.*;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes, vsds, geojson, wkt" })
@CapabilityDescription("Replaces geojson:geometry with wkt alternative")
public class GeoJsonToWktProcessor extends AbstractProcessor {

	private GeoJsonToWktTransformer geoJsonToWktTransformer;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(DATA_SOURCE_FORMAT);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		geoJsonToWktTransformer = new GeoJsonToWktTransformer();
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		final FlowFile flowFile = session.get();
		if (flowFile != null) {
			try {
				Lang dataSourceFormat = GeoJsonToWktProcessorProperties.getDataSourceFormat(context);
				Model inputModel = receiveDataAsModel(session, flowFile, dataSourceFormat);
				Model result = geoJsonToWktTransformer.transform(inputModel);
				sendRDFToRelation(session, flowFile, result, SUCCESS, dataSourceFormat);
			} catch (Exception e) {
				getLogger().error("Error transforming geojson to wkt: {}", e.getMessage());
				sendRDFToRelation(session, flowFile, FAILURE);
			}
		}
	}

}
