package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.repository.SparqlSelectService;
import org.apache.jena.rdf.model.Model;
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

import com.google.gson.JsonElement;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlProcessorProperties.SPARQL_SELECT_QUERY;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.*;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes, vsds, SPARQL" })
@CapabilityDescription("SPARQL construct manipulation of an RDF flowfile.")
public class SparqlSelectProcessor extends AbstractProcessor {

	private SparqlSelectService sparqlSelectService;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(SPARQL_SELECT_QUERY, DATA_SOURCE_FORMAT);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		sparqlSelectService = new SparqlSelectService();
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		final FlowFile flowFile = session.get();
		if (flowFile != null) {
			try {
				Model inputModel = receiveDataAsModel(session, flowFile,
						CommonProperties.getDataSourceFormat(context));
				String queryString = context.getProperty(SPARQL_SELECT_QUERY).getValue();

				final Iterable<JsonElement> queryResult = sparqlSelectService.executeSelect(inputModel, queryString);

				sendRDFToRelation(session, flowFile, queryResult.toString(), SUCCESS, APPLICATION_JSON.getMimeType());
			} catch (Exception e) {
				getLogger().error("Error executing SPARQL SELECT query: {}", e.getMessage());
				session.transfer(flowFile, FAILURE);
			}
		}
	}

}
