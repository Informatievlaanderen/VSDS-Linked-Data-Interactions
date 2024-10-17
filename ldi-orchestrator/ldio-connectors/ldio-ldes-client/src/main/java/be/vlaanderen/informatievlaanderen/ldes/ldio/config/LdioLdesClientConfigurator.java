package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusConsumer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioObserver;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.eventstreamproperties.EventStreamPropertiesFetcher;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.springframework.context.ApplicationEventPublisher;

public class LdioLdesClientConfigurator implements LdioInputConfigurator {
	private final ClientStatusService clientStatusService;
	private final ObservationRegistry observationRegistry;
	private final LdioRequestExecutorSupplier requestExecutorSupplier;

	public LdioLdesClientConfigurator(ClientStatusService clientStatusService, ObservationRegistry observationRegistry) {
		this.clientStatusService = clientStatusService;
		this.observationRegistry = observationRegistry;
		requestExecutorSupplier = new LdioRequestExecutorSupplier(new RequestExecutorFactory(false));
	}

	@Override
	public LdioInput configure(LdiAdapter adapter, ComponentExecutor componentExecutor,
	                           ApplicationEventPublisher applicationEventPublisher,
	                           ComponentProperties properties) {
		final String pipelineName = properties.getPipelineName();
		final LdioLdesClientProperties ldioLdesClientProperties = LdioLdesClientProperties.fromComponentProperties(properties);
		final var requestExecutor = requestExecutorSupplier.getRequestExecutor(properties);
		final EventStreamPropertiesFetcher eventStreamPropertiesFetcher = new EventStreamPropertiesFetcher(requestExecutor);
		final var clientStatusConsumer = new ClientStatusConsumer(pipelineName, clientStatusService);
		final MemberSupplier memberSupplier = new MemberSupplierFactory(ldioLdesClientProperties, eventStreamPropertiesFetcher, requestExecutor, clientStatusConsumer).getMemberSupplier();
		final boolean keepState = ldioLdesClientProperties.isKeepStateEnabled();
		final LdioObserver ldioObserver = LdioObserver.register(LdioLdesClient.NAME, pipelineName, observationRegistry);
		final var ldesClient = new LdioLdesClient(componentExecutor, ldioObserver, memberSupplier, applicationEventPublisher, keepState, clientStatusConsumer);
		ldesClient.start();
		return ldesClient;
	}

	@Override
	public boolean isAdapterRequired() {
		return false;
	}
}
