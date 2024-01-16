package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromCurrentTimeExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class LdioLdesClient extends LdioInput {
	public static final String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient";
	private final Logger log = LoggerFactory.getLogger(LdioLdesClient.class);
	private final RequestExecutor requestExecutor;
	private final ComponentProperties properties;
	private final StatePersistence statePersistence;

	private boolean threadRunning = true;

	public LdioLdesClient(String pipelineName,
						  ComponentExecutor componentExecutor,
						  ObservationRegistry observationRegistry,
						  RequestExecutor requestExecutor,
						  ComponentProperties properties,
						  StatePersistence statePersistence) {
		super(NAME, pipelineName, componentExecutor, null, observationRegistry);
		this.requestExecutor = requestExecutor;
		this.properties = properties;
		this.statePersistence = statePersistence;
	}

	@SuppressWarnings("java:S2095")
	public void start() {
		final ExecutorService executorService = newSingleThreadExecutor();
		executorService.submit(this::run);
	}

	private void run() {
		try {
			log.info("Starting LdesClientRunner run setup");
			MemberSupplier memberSupplier = getMemberSupplier();
			log.info("LdesClientRunner setup finished");
			while (threadRunning) {
				processModel(memberSupplier.get().getModel());
			}
		} catch (EndOfLdesException e) {
			log.warn(e.getMessage());
		} catch (Exception e) {
			log.error("LdesClientRunner FAILURE", e);
		}
	}

	private MemberSupplier getMemberSupplier() {
		String targetUrl = properties.getProperty(LdioLdesClientProperties.URL);
		Lang sourceFormat = properties.getOptionalProperty(LdioLdesClientProperties.SOURCE_FORMAT)
				.map(RDFLanguages::nameToLang)
				.orElse(Lang.JSONLD);
		LdesMetaData ldesMetaData = new LdesMetaData(targetUrl,
				sourceFormat);
		TimestampExtractor timestampExtractor = properties.getOptionalProperty(LdioLdesClientProperties.TIMESTAMP_PATH_PROP).map(timestampPath -> (TimestampExtractor) new TimestampFromPathExtractor(createProperty(timestampPath)))
				.orElseGet(TimestampFromCurrentTimeExtractor::new);
		TreeNodeProcessor treeNodeProcessor = getTreeNodeProcessor(statePersistence, requestExecutor, ldesMetaData, timestampExtractor);
		boolean keepState = properties.getOptionalBoolean(LdioLdesClientProperties.KEEP_STATE).orElse(false);

		return new MemberSupplier(treeNodeProcessor, keepState);
	}

	private TreeNodeProcessor getTreeNodeProcessor(StatePersistence statePersistenceStrategy,
												   RequestExecutor requestExecutor,
												   LdesMetaData ldesMetaData,
												   TimestampExtractor timestampExtractor) {
		return new TreeNodeProcessor(ldesMetaData, statePersistenceStrategy, requestExecutor, timestampExtractor);
	}

	public void stopThread() {
		threadRunning = false;
	}
}
