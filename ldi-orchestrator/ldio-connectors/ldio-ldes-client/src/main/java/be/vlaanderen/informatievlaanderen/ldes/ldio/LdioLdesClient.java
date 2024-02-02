package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.MemberSupplierImpl;
import ldes.client.treenodesupplier.MemberSupplierVersionMaterialiser;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.*;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

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
        final MemberSupplier baseMemberSupplier =
                new MemberSupplierImpl(getTreeNodeProcessor(), getKeepState());
        if (useVersionMaterialisation()) {
            return new MemberSupplierVersionMaterialiser(baseMemberSupplier, createVersionMaterialiser());
        } else {
            return baseMemberSupplier;
        }
	}

    private Boolean getKeepState() {
        return properties.getOptionalBoolean(LdioLdesClientProperties.KEEP_STATE).orElse(false);
    }

    private TreeNodeProcessor getTreeNodeProcessor() {
        String targetUrl = properties.getProperty(LdioLdesClientProperties.URL);
        Lang sourceFormat = getSourceFormat();
        LdesMetaData ldesMetaData = new LdesMetaData(targetUrl, sourceFormat);
        return new TreeNodeProcessor(ldesMetaData, statePersistence, requestExecutor);
    }

    private boolean useVersionMaterialisation() {
        return properties
                .getOptionalBoolean(USE_VERSION_MATERIALISATION)
                .orElseGet(() -> {
                    log.warn("Version-materialization in the LDES Client hasn’t been turned on. " +
                            "Please note that in the future, this will be the default output of the LDES Client " +
                            "and having version-objects as output will have to be configured explicitly.");
                    return false;
                });
    }

    private Lang getSourceFormat() {
        return properties.getOptionalProperty(LdioLdesClientProperties.SOURCE_FORMAT)
                .map(RDFLanguages::nameToLang)
                .orElse(Lang.JSONLD);
    }

    private VersionMaterialiser createVersionMaterialiser() {
		final Property versionOfProperty = properties
                .getOptionalProperty(VERSION_OF_PROPERTY)
				.map(ResourceFactory::createProperty)
				.orElseGet(() -> ResourceFactory.createProperty("http://purl.org/dc/terms/isVersionOf"));
		final boolean restrictToMembers = properties.getOptionalBoolean(RESTRICT_TO_MEMBERS).orElse(false);
        return new VersionMaterialiser(versionOfProperty, restrictToMembers);
	}

	public void stopThread() {
		threadRunning = false;
	}

}
