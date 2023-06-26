package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.domain.valueobjects.LdesProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.LdesPropertiesExtractor;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.StartingTreeNodeSupplier;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.StartingTreeNode;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.apache.nifi.annotation.behavior.Stateful;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnRemoved;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.state.Scope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorRelationships.DATA_RELATIONSHIP;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes-client", "vsds" })
@CapabilityDescription("Extract members from an LDES source and send them to the next processor")
@Stateful(description = "Stores mutable fragments to allow processor restart", scopes = Scope.LOCAL)
public class LdesClient extends AbstractProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdesClient.class);
	private MemberSupplier memberSupplier;
	private LdesProperties ldesProperties;
	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(DATA_RELATIONSHIP);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(DATA_SOURCE_URL, DATA_SOURCE_FORMAT, DATA_DESTINATION_FORMAT, KEEP_STATE,
				STATE_PERSISTENCE_STRATEGY,
				STREAM_TIMESTAMP_PATH_PROPERTY, STREAM_VERSION_OF_PROPERTY, STREAM_SHAPE_PROPERTY,
				API_KEY_HEADER_PROPERTY,
				API_KEY_PROPERTY, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_TOKEN_ENDPOINT, AUTHORIZATION_STRATEGY,
				RETRIES_ENABLED, MAX_RETRIES, STATUSES_TO_RETRY);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		String dataSourceUrl = LdesProcessorProperties.getDataSourceUrl(context);
		Lang dataSourceFormat = LdesProcessorProperties.getDataSourceFormat(context);
		final RequestExecutor requestExecutor = getRequestExecutorWithPossibleRetry(context);
		StartingTreeNode startingTreeNode = new StartingTreeNodeSupplier(requestExecutor).getStart(dataSourceUrl,
				dataSourceFormat);
		TreeNodeProcessor treeNodeProcessor = new TreeNodeProcessor(startingTreeNode,
				StatePersistence.from(LdesProcessorProperties.getStatePersistenceStrategy(context)),
				new TreeNodeFetcher(requestExecutor));
		memberSupplier = new MemberSupplier(treeNodeProcessor, LdesProcessorProperties.stateKept(context));

		determineLdesProperties(startingTreeNode, requestExecutor, context);

		LOGGER.info("LDES extraction processor {} with base url {} (expected LDES source format: {})",
				context.getName(), dataSourceUrl, dataSourceFormat);
	}

	private RequestExecutor getRequestExecutorWithPossibleRetry(final ProcessContext context) {
		final RequestExecutor requestExecutor = getRequestExecutor(context);
		return retriesEnabled(context)
				? requestExecutorFactory.createRetryExecutor(requestExecutor, getMaxRetries(context),
						getStatusesToRetry(context))
				: requestExecutor;
	}

	private RequestExecutor getRequestExecutor(final ProcessContext context) {
		return switch (getAuthorizationStrategy(context)) {
			case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor();
			case API_KEY -> requestExecutorFactory.createApiKeyExecutor(getApiKeyHeader(context), getApiKey(context));
			case OAUTH2_CLIENT_CREDENTIALS ->
				requestExecutorFactory.createClientCredentialsExecutor(getOauthClientId(context),
						getOauthClientSecret(context), getOauthTokenEndpoint(context));
		};
	}

	private void determineLdesProperties(StartingTreeNode startingTreeNode, RequestExecutor requestExecutor,
			ProcessContext context) {
		boolean timestampPath = streamTimestampPathProperty(context);
		boolean versionOfPath = streamVersionOfProperty(context);
		boolean shape = streamShapeProperty(context);
		ldesProperties = new LdesPropertiesExtractor(requestExecutor).getLdesProperties(startingTreeNode, timestampPath,
				versionOfPath, shape);
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		SuppliedMember memberRecord = memberSupplier.get();

		FlowFile flowFile = session.create();

		if (streamTimestampPathProperty(context)) {
			session.putAttribute(flowFile, "ldes.timestamppath", ldesProperties.getTimestampPath());
		}
		if (streamVersionOfProperty(context)) {
			session.putAttribute(flowFile, "ldes.isversionofpath", ldesProperties.getVersionOfPath());
		}
		if (streamShapeProperty(context)) {
			session.putAttribute(flowFile, "ldes.shacleshapes", ldesProperties.getShape());
		}
		Lang dataDestinationFormat = LdesProcessorProperties.getDataDestinationFormat(context);
		FlowManager.sendRDFToRelation(session, flowFile,
				convertModelToString(memberRecord.getModel(), dataDestinationFormat),
				DATA_RELATIONSHIP, dataDestinationFormat);
	}

	@OnRemoved
	public void onRemoved() {
		memberSupplier.destroyState();
	}

	public static String convertModelToString(Model model, Lang dataDestinationFormat) {
		return RDFWriter.source(model).lang(dataDestinationFormat).asString();
	}

}
