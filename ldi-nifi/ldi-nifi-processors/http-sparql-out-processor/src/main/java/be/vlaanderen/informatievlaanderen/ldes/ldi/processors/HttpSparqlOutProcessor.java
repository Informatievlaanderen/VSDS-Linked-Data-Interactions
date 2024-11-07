package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HttpSparqlOut;
import be.vlaanderen.informatievlaanderen.ldes.ldi.SkolemisationTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.factory.DeleteFunctionBuilder;
import be.vlaanderen.informatievlaanderen.ldes.ldi.factory.InsertFunctionBuilder;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.RequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation.EmptySkolemizer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation.Skolemizer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation.SkolemizerImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.DeleteFunction;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.InsertFunction;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.SparqlQuery;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.HttpSparqlOutProcessorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RequestExecutorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.*;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({"http", "sparql", "vsds", "ldes"})
@CapabilityDescription("Transforms blank nodes from LDES members to a skolemized URI")
public class HttpSparqlOutProcessor extends AbstractProcessor {
	private HttpSparqlOut httpSparqlOut;

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(
				ENDPOINT,
				GRAPH,
				REPLACEMENT_ENABLED,
				REPLACEMENT_DEPTH,
				REPLACEMENT_DELETE_FUNCTION,
				SKOLEMISATION_DOMAIN,
				API_KEY_HEADER_PROPERTY,
				API_KEY_PROPERTY,
				OAUTH_CLIENT_ID,
				OAUTH_CLIENT_SECRET,
				OAUTH_TOKEN_ENDPOINT,
				OAUTH_SCOPE,
				AUTHORIZATION_STRATEGY,
				RETRIES_ENABLED,
				MAX_RETRIES,
				STATUSES_TO_RETRY
		);
	}

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		final DeleteFunction deleteFunction = createDeleteFunction(context);
		final InsertFunction insertFunction = getGraph(context)
				.map(InsertFunctionBuilder::withGraph)
				.orElseGet(InsertFunctionBuilder::create)
				.build();
		final Skolemizer skolemizer = getSkolemisationDomain(context)
				.map(SkolemisationTransformer::new)
				.map(skolemisationTransformer -> (Skolemizer) new SkolemizerImpl(skolemisationTransformer))
				.orElseGet(EmptySkolemizer::new);
		final RequestExecutor requestExecutor = new RequestExecutorSupplier().getRequestExecutor(context);

		final SparqlQuery sparqlQuery = new SparqlQuery(insertFunction, deleteFunction);
		httpSparqlOut = new HttpSparqlOut(getEndpoint(context), sparqlQuery, skolemizer, requestExecutor);
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		final FlowFile flowFile = session.get();
		if(flowFile != null) {
			try {
				final Lang mimeType = RDFLanguages.contentTypeToLang(flowFile.getAttribute("mime.type"));
				final Model inputModel = receiveDataAsModel(session, flowFile, mimeType);

				httpSparqlOut.write(inputModel);

				session.transfer(flowFile, SUCCESS);
			} catch (Exception e) {
				getLogger().error("Error executing http sparql out: {}", e.getMessage());
				session.transfer(flowFile, FAILURE);
			}
		}
	}

	private static DeleteFunction createDeleteFunction(ProcessContext context) {
		if(!isReplacementEnabled(context)) {
			return DeleteFunctionBuilder.disabled();
		}
		return getReplacementDeleteFunction(context)
				.map(DeleteFunction::ofQuery)
				.orElseGet(() -> getGraph(context)
						.map(DeleteFunctionBuilder::withGraph)
						.orElseGet(DeleteFunctionBuilder::create)
						.withDepth(getReplacementDepth(context))
				);
	}
}
