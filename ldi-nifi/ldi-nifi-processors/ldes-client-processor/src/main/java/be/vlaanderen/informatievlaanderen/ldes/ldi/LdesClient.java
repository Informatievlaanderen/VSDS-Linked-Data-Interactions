package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdesProcessorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.domain.valueobjects.LdesProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.LdesPropertiesExtractor;
import ldes.client.requestexecutor.domain.valueobjects.ApiKeyConfig;
import ldes.client.requestexecutor.domain.valueobjects.DefaultConfig;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.startingtreenode.StartingTreeNodeFinder;
import ldes.client.startingtreenode.domain.valueobjects.Endpoint;
import ldes.client.startingtreenode.domain.valueobjects.TreeNode;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.entities.SuppliedMember;
import ldes.client.treenodesupplier.domain.valueobject.Ldes;
import ldes.client.treenodesupplier.repository.sqlite.SqliteMemberRepository;
import ldes.client.treenodesupplier.repository.sqlite.SqliteTreeNodeRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
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

import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdesProcessorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdesProcessorRelationships.DATA_RELATIONSHIP;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes-client", "vsds" })
@CapabilityDescription("Extract members from an LDES source and send them to the next processor")
@Stateful(description = "Stores mutable fragments to allow processor restart", scopes = Scope.LOCAL)
public class LdesClient extends AbstractProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdesClient.class);
	TreeNodeProcessor treeNodeProcessor;
	MemberSupplier memberSupplier;
	private LdesProperties ldesProperties;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(DATA_RELATIONSHIP);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(DATA_SOURCE_URL, DATA_SOURCE_FORMAT, DATA_DESTINATION_FORMAT, FRAGMENT_EXPIRATION_INTERVAL,
				STREAM_TIMESTAMP_PATH_PROPERTY, STREAM_VERSION_OF_PROPERTY, STREAM_SHAPE_PROPERTY);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		String dataSourceUrl = LdesProcessorProperties.getDataSourceUrl(context);
		Lang dataSourceFormat = LdesProcessorProperties.getDataSourceFormat(context);
		String apiKey = getApiKey(context);
		RequestExecutor requestExecutor = new DefaultConfig().createRequestExecutor();
		if (!apiKey.equals("")) {
			requestExecutor = new ApiKeyConfig(apiKey, getApiKeyHeader(context)).createRequestExecutor();
		}

		Ldes ldes = getLdes(dataSourceUrl, dataSourceFormat, requestExecutor);

		treeNodeProcessor = new TreeNodeProcessor(ldes, new SqliteTreeNodeRepository(), new SqliteMemberRepository(),
				new TreeNodeFetcher(requestExecutor), true);
		memberSupplier = new MemberSupplier(treeNodeProcessor);
		determineLdesProperties(ldes, requestExecutor, context);

		LOGGER.info("LDES extraction processor {} with base url {} (expected LDES source format: {})",
				context.getName(), dataSourceUrl, dataSourceFormat);
	}

	private void determineLdesProperties(Ldes ldes, RequestExecutor requestExecutor, ProcessContext context) {
		boolean timestampPath = streamTimestampPathProperty(context);
		boolean versionOfPath = streamVersionOfProperty(context);
		boolean shape = streamShapeProperty(context);
		ldesProperties = new LdesPropertiesExtractor(requestExecutor).getLdesProperties(ldes, timestampPath,
				versionOfPath, shape);
	}

	private Ldes getLdes(String dataSourceUrl, Lang dataSourceFormat, RequestExecutor requestExecutor) {
		StartingTreeNodeFinder startingTreeNodeFinder = new StartingTreeNodeFinder(requestExecutor);
		Optional<TreeNode> startingTreeNode = startingTreeNodeFinder
				.determineStartingTreeNode(new Endpoint(dataSourceUrl, dataSourceFormat));
		return new Ldes(startingTreeNode.get().getUrl(), dataSourceFormat);
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
		treeNodeProcessor.destoryState();
	}

	public static String convertModelToString(Model model, Lang dataDestinationFormat) {
		StringWriter out = new StringWriter();

		RDFDataMgr.write(out, model, dataDestinationFormat);

		return out.toString();
	}

}
