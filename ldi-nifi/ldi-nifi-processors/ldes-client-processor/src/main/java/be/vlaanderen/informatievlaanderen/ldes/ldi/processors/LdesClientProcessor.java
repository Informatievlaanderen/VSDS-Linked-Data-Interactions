package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateUtil;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.NiFiDBCPDataSource;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.RequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers.MemberSupplierWrappersBuilder;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.eventstreamproperties.EventStreamPropertiesFetcher;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.eventstreamproperties.valueobjects.PropertiesRequest;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.*;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplierImpl;
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
import org.apache.nifi.dbcp.DBCPService;
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
import java.util.function.Consumer;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.DATA_DESTINATION_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorRelationships.DATA_RELATIONSHIP;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RequestExecutorProperties.*;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({"ldes-client", "vsds"})
@CapabilityDescription("Extract members from an LDES source and send them to the next processor")
@Stateful(description = "Stores mutable fragments to allow processor restart", scopes = Scope.LOCAL)
public class LdesClientProcessor extends AbstractProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdesClientProcessor.class);
	private MemberSupplier memberSupplier;
	private EventStreamProperties eventStreamProperties;
	private boolean hasLdesEnded;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(DATA_RELATIONSHIP);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(DATA_SOURCE_URLS,
				DATA_SOURCE_FORMAT,
				DATA_DESTINATION_FORMAT,
				STATE_PERSISTENCE_STRATEGY,
				DBCP_SERVICE,
				KEEP_STATE,
				USE_EXACTLY_ONCE_FILTER,
				USE_VERSION_MATERIALISATION,
				USE_LATEST_STATE_FILTER,
				AUTHORIZATION_STRATEGY,
				API_KEY_PROPERTY,
				API_KEY_HEADER_PROPERTY,
				OAUTH_CLIENT_ID,
				OAUTH_CLIENT_SECRET,
				OAUTH_TOKEN_ENDPOINT,
				OAUTH_SCOPE,
				RETRIES_ENABLED,
				MAX_RETRIES,
				STATUSES_TO_RETRY,
				RESTRICT_TO_MEMBERS,
				STREAM_TIMESTAMP_PATH_PROPERTY,
				STREAM_VERSION_OF_PROPERTY,
				STREAM_SHAPE_PROPERTY
		);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		List<String> dataSourceUrls = LdesProcessorProperties.getDataSourceUrl(context);
		Lang dataSourceFormat = LdesProcessorProperties.getDataSourceFormat(context);
		final RequestExecutorSupplier requestExecutorSupplier = new RequestExecutorSupplier();
		final RequestExecutor requestExecutor = requestExecutorSupplier.getRequestExecutor(context);
		LdesMetaData ldesMetaData = new LdesMetaData(dataSourceUrls, dataSourceFormat);
		LdesClientRepositories ldesClientRepositories = getClientRepositories(context);

		eventStreamProperties = fetchEventStreamProperties(ldesMetaData, requestExecutor);
		TimestampExtractor timestampExtractor = new TimestampFromPathExtractor(createProperty(eventStreamProperties.getTimestampPath()));
		TreeNodeProcessor treeNodeProcessor = new TreeNodeProcessor(ldesMetaData, ldesClientRepositories, requestExecutor,
				timestampExtractor, clientStatusConsumer());
		final MemberSupplier baseMemberSupplier = new MemberSupplierImpl(treeNodeProcessor, stateKept(context));
		memberSupplier = new MemberSupplierWrappersBuilder()
				.withContext(context)
				.withEventStreamProperties(eventStreamProperties)
				.withClientRepositories(ldesClientRepositories)
				.build()
				.wrapMemberSupplier(baseMemberSupplier);

		memberSupplier.init();
		LOGGER.info("LDES Client processor {} configured to follow (sub)streams {} (expected LDES source format: {})",
				context.getName(), dataSourceUrls, dataSourceFormat);
	}

	private EventStreamProperties fetchEventStreamProperties(LdesMetaData ldesMetaData, RequestExecutor requestExecutor) {
		PropertiesRequest request = new PropertiesRequest(ldesMetaData.getStartingNodeUrl(), ldesMetaData.getLang());
		return new EventStreamPropertiesFetcher(requestExecutor).fetchEventStreamProperties(request);
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		if (hasLdesEnded) {
			return;
		}

		try {
			processNextMember(context, session);
		} catch (EndOfLdesException exception) {
			LOGGER.warn(exception.getMessage());
			hasLdesEnded = true;
		}
	}

	private void processNextMember(ProcessContext context, ProcessSession session) {
		SuppliedMember memberRecord = memberSupplier.get();

		FlowFile flowFile = session.create();

		if (streamTimestampPathProperty(context)) {
			session.putAttribute(flowFile, "ldes.timestamppath", eventStreamProperties.getTimestampPath());
		}
		if (streamVersionOfProperty(context)) {
			session.putAttribute(flowFile, "ldes.isversionofpath", eventStreamProperties.getVersionOfPath());
		}
		if (streamShapeProperty(context)) {
			session.putAttribute(flowFile, "ldes.shacleshapes", eventStreamProperties.getShaclShapeUri());
		}
		Lang dataDestinationFormat = LdesProcessorProperties.getDataDestinationFormat(context);
		FlowManager.sendRDFToRelation(session, flowFile,
				convertModelToString(memberRecord.getModel(), dataDestinationFormat),
				DATA_RELATIONSHIP, dataDestinationFormat);
	}

	@OnRemoved
	public void onRemoved() {
		if (memberSupplier != null) {
			memberSupplier.destroyState();
		}
	}

	public static String convertModelToString(Model model, Lang dataDestinationFormat) {
		return RDFWriter.source(model).lang(dataDestinationFormat).asString();
	}

	private Consumer<ClientStatus> clientStatusConsumer() {
		return status -> LOGGER.info("LDES Client is now {}", status);
	}

	private LdesClientRepositories getClientRepositories(ProcessContext context) {
		final DBCPService dbcpService = context.getProperty(DBCP_SERVICE).asControllerService(DBCPService.class);
		final NiFiDBCPDataSource dataSource = new NiFiDBCPDataSource(dbcpService);
		final StatePersistenceStrategy state = getStatePersistenceStrategy(context);

		boolean keepState = switch (state) {
			case MEMORY -> false;
			case SQLITE, POSTGRES -> PersistenceProperties.stateKept(context);
		};
		var entityManager = HibernateUtil.createEntityManagerFromDatasource(dataSource, keepState);
		return LdesClientRepositories.from(state, entityManager);
	}

}
