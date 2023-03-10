package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientImplFactory;
import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import be.vlaanderen.informatievlaanderen.ldes.client.converters.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.EndpointRequester;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint.ApiKey;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint.Endpoint;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode.StartingNode;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.LdesPropertyNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesService;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import org.apache.jena.riot.Lang;
import org.apache.nifi.annotation.behavior.Stateful;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnAdded;
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

	protected String identifier = null;
	protected LdesClientConfig config = new LdesClientConfig();
	protected LdesService ldesService;
	private final EndpointRequester endpointRequester = new EndpointRequester();

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(DATA_RELATIONSHIP);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(DATA_SOURCE_URL, DATA_SOURCE_FORMAT, DATA_DESTINATION_FORMAT, FRAGMENT_EXPIRATION_INTERVAL,
				STREAM_TIMESTAMP_PATH_PROPERTY, STREAM_VERSION_OF_PROPERTY, STREAM_SHAPE_PROPERTY);
	}

	@OnAdded
	public void onAdded() {
		if (identifier == null) {
			identifier = getIdentifier();
		}
		config.setPersistenceDbName(identifier + "-" + config.getPersistenceDbName());
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		String dataSourceUrl = LdesProcessorProperties.getDataSourceUrl(context);
		Lang dataSourceFormat = LdesProcessorProperties.getDataSourceFormat(context);
		Long fragmentExpirationInterval = LdesProcessorProperties.getFragmentExpirationInterval(context);

		config.setApiKey(getApiKey(context));
		config.setApiKeyHeader(getApiKeyHeader(context));

		ldesService = LdesClientImplFactory.getLdesService(config);

		ldesService.setDataSourceFormat(dataSourceFormat);
		ldesService.setFragmentExpirationInterval(fragmentExpirationInterval);
		ldesService.queueFragment(getStartingUrl(dataSourceUrl, dataSourceFormat));

		LOGGER.info("LDES extraction processor {} with base url {} (expected LDES source format: {})",
				context.getName(), dataSourceUrl, dataSourceFormat);
	}

	private String getStartingUrl(String dataSourceUrl, Lang dataSourceFormat) {
		final ApiKey apiKey = new ApiKey(config.getApiKeyHeader(), config.getApiKey());
		return endpointRequester
				.determineStartingNode(new Endpoint(dataSourceUrl, dataSourceFormat, apiKey))
				.map(StartingNode::url)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Starting url could not be determined for fragmentId: " + dataSourceUrl));
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		if (ldesService.hasFragmentsToProcess()) {
			Lang dataDestinationFormat = LdesProcessorProperties.getDataDestinationFormat(context);
			LdesFragment fragment = ldesService.processNextFragment();

			String timestampPath = streamTimestampPathProperty(context) ? fragment.getTimestampPath()
					.orElseThrow(() -> new LdesPropertyNotFoundException(LdesFragment.LDES_TIMESTAMP_PATH.toString()))
					: null;
			String versionOfPath = streamVersionOfProperty(context) ? fragment.getVersionOfPath()
					.orElseThrow(() -> new LdesPropertyNotFoundException(LdesFragment.LDES_VERSION_OF.toString()))
					: null;
			String shape = streamShapeProperty(context) ? fragment.getShaclShape()
					.orElseThrow(() -> new LdesPropertyNotFoundException(LdesFragment.TREE_SHAPE.toString()))
					: null;

			// Send the processed members to the next Nifi processor
			fragment.getMembers()
					.forEach(ldesMember -> {
						FlowFile flowFile = session.create();

						if (streamTimestampPathProperty(context)) {
							session.putAttribute(flowFile, "ldes.timestamppath", timestampPath);
						}
						if (streamVersionOfProperty(context)) {
							session.putAttribute(flowFile, "ldes.isversionofpath", versionOfPath);
						}
						if (streamShapeProperty(context)) {
							session.putAttribute(flowFile, "ldes.shacleshapes", shape);
						}

						FlowManager.sendRDFToRelation(session, flowFile,
								ModelConverter.convertModelToString(ldesMember.getMemberModel(), dataDestinationFormat),
								DATA_RELATIONSHIP, dataDestinationFormat);
					});
		}
	}

	@OnRemoved
	public void onRemoved() {
		ldesService.getStateManager().destroyState();
	}

}
