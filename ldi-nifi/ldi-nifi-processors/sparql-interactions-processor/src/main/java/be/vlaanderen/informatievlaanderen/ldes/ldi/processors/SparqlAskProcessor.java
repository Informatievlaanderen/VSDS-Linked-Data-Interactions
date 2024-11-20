package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.getDataSourceFormat;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlAskRelationships.FALSE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlAskRelationships.TRUE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SparqlProcessorProperties.SPARQL_ASK_QUERY;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.receiveDataAsModel;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({"ldes, vsds, SPARQL"})
@CapabilityDescription("SPARQL ASK query on an RDF flowfile.")
public class SparqlAskProcessor extends AbstractProcessor {

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(TRUE, FALSE, FAILURE);
	}

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(SPARQL_ASK_QUERY, DATA_SOURCE_FORMAT);
	}

	@Override
	protected Collection<ValidationResult> customValidate(ValidationContext validationContext) {
		final String value = validationContext.getProperty(SPARQL_ASK_QUERY).getValue();
		try {
			QueryFactory.create(value);
		} catch (QueryException e) {
			return List.of(new ValidationResult.Builder()
					.subject(SPARQL_ASK_QUERY.getDisplayName())
					.valid(false)
					.explanation(e.getMessage())
					.build()
			);
		}
		return super.customValidate(validationContext);
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		final FlowFile flowFile = session.get();
		if (flowFile != null) {
			final String query = context.getProperty(SPARQL_ASK_QUERY).getValue();
			try (QueryExecution queryExecution = QueryExecution.create(query, receiveDataAsModel(session, flowFile, getDataSourceFormat(context)))) {
				session.transfer(flowFile, queryExecution.execAsk() ? TRUE : FALSE);
			} catch (Exception e) {
				getLogger().error("Error executing SPARQL ASK query: {}", e.getMessage());
				session.transfer(flowFile, FAILURE);
			}
		}
	}
}
