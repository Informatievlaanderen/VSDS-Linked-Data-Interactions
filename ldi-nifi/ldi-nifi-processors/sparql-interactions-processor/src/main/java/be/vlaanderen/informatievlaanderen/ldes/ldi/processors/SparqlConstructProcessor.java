package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
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

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.getDataSourceFormat;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlProcessorProperties.INCLUDE_ORIGINAL;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlProcessorProperties.SPARQL_CONSTRUCT_QUERY;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.*;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({"ldes, vsds, SPARQL"})
@CapabilityDescription("SPARQL construct manipulation of an RDF flowfile.")
public class SparqlConstructProcessor extends AbstractProcessor {

	private SparqlConstructTransformer transformer;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(SPARQL_CONSTRUCT_QUERY, INCLUDE_ORIGINAL, DATA_SOURCE_FORMAT);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		Query query = QueryFactory.create(context.getProperty(SPARQL_CONSTRUCT_QUERY).getValue());

		transformer = new SparqlConstructTransformer(query, context.getProperty(INCLUDE_ORIGINAL).asBoolean());
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		final FlowFile flowFile = session.get();
		if (flowFile != null) {
			try {
				final Model inputModel = receiveDataAsModel(session, flowFile, getDataSourceFormat(context));

				transformer.transform(inputModel).forEach(resultModel ->
						sendRDFToRelation(session, resultModel, SUCCESS, getDataSourceFormat(context)));

				session.remove(flowFile);
			} catch (Exception e) {
				getLogger().error("Error executing SPARQL CONSTRUCT query: {}", e.getMessage());
				session.transfer(flowFile, FAILURE);
			}
		}
	}

}
