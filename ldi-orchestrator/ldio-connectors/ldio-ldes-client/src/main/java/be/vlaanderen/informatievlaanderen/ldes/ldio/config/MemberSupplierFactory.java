package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers.MemberSupplierWrappersBuilder;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusConsumer;
import ldes.client.eventstreamproperties.EventStreamPropertiesFetcher;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.eventstreamproperties.valueobjects.PropertiesRequest;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.LdesClientRepositories;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplierImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class MemberSupplierFactory {
	private static final Logger log = LoggerFactory.getLogger(MemberSupplierFactory.class);
	private final LdioLdesClientProperties clientProperties;
	private final RequestExecutor requestExecutor;
	private final ClientStatusConsumer clientStatusConsumer;
	private final EventStreamPropertiesFetcher eventStreamPropertiesFetcher;

	public MemberSupplierFactory(LdioLdesClientProperties clientProperties,
	                             EventStreamPropertiesFetcher eventStreamPropertiesFetcher,
	                             RequestExecutor requestExecutor,
	                             ClientStatusConsumer clientStatusConsumer ) {
		this.clientProperties = clientProperties;
		this.requestExecutor = requestExecutor;
		this.clientStatusConsumer = clientStatusConsumer;
		this.eventStreamPropertiesFetcher = eventStreamPropertiesFetcher;
	}

	public MemberSupplier getMemberSupplier() {
		log.info("Starting LdesClientRunner run setup");
		final EventStreamProperties eventStreamProperties = eventStreamPropertiesFetcher.fetchEventStreamProperties(new PropertiesRequest(clientProperties.getFirstUrl(), clientProperties.getSourceFormat()));
		MemberSupplier baseMemberSupplier = new MemberSupplierImpl(getTreeNodeProcessor(eventStreamProperties), clientProperties.isKeepStateEnabled());
		baseMemberSupplier = new MemberSupplierWrappersBuilder()
				.withEventStreamProperties(eventStreamProperties)
				.withLdioLdesClientProperties(clientProperties)
				.build()
				.wrapMemberSupplier(baseMemberSupplier);

		log.info("LdesClientRunner setup finished");
		return baseMemberSupplier;
	}

	private TreeNodeProcessor getTreeNodeProcessor(EventStreamProperties eventStreamProperties) {
		final LdesClientRepositories ldesClientRepositories = new LdesClientRepositoriesFactory().getStatePersistence(clientProperties.getProperties());
		LdesMetaData ldesMetaData = new LdesMetaData(clientProperties.getUrls(), clientProperties.getSourceFormat());
		TimestampExtractor timestampExtractor = new TimestampFromPathExtractor(createProperty(eventStreamProperties.getTimestampPath()));
		return new TreeNodeProcessor(ldesMetaData, ldesClientRepositories, requestExecutor, timestampExtractor, clientStatusConsumer);
	}

}
