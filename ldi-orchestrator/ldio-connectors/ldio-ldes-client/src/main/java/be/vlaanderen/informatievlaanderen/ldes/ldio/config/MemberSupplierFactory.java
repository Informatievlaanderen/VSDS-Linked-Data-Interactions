package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromCurrentTimeExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.ConfigPropertyMissingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.InvalidConfigException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import ldes.client.treenodesupplier.filters.ExactlyOnceFilter;
import ldes.client.treenodesupplier.filters.LatestStateFilter;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplierImpl;
import ldes.client.treenodesupplier.membersuppliers.VersionMaterialisedMemberSupplier;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.*;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class MemberSupplierFactory {

    private static final String DEFAULT_VERSION_OF_KEY = "http://purl.org/dc/terms/isVersionOf";
    private static final String DEFAULT_TIMESTAMP_KEY = "http://www.w3.org/ns/prov#generatedAtTime";

    private final Logger log = LoggerFactory.getLogger(MemberSupplierFactory.class);

    private final ComponentProperties properties;
    private final RequestExecutor requestExecutor;

    public MemberSupplierFactory(ComponentProperties properties, RequestExecutor requestExecutor) {
        this.properties = properties;
        this.requestExecutor = requestExecutor;
    }

    public MemberSupplier getMemberSupplier() {
        log.info("Starting LdesClientRunner run setup");
        log.info("LdesClientRunner setup finished");
        MemberSupplier baseMemberSupplier =
                new MemberSupplierImpl(getTreeNodeProcessor(), getKeepState());
        if (useExactlyOnceFilter()) {
            return new FilteredMemberSupplier(baseMemberSupplier, getExactlyOnceFilter());
        } else if (useVersionMaterialisation()) {
            MemberSupplier decoratedMemberSupplier = useLatestStateFilter()
                    ? new FilteredMemberSupplier(baseMemberSupplier, getLatestStateFilter())
                    : baseMemberSupplier;
            return new VersionMaterialisedMemberSupplier(decoratedMemberSupplier, createVersionMaterialiser());
        } else {
            return baseMemberSupplier;
        }
    }

    private LatestStateFilter getLatestStateFilter() {
        String timestampPath = properties.getOptionalProperty(TIMESTAMP_PATH_PROP).orElse(DEFAULT_TIMESTAMP_KEY);
        String versionOfPath = properties.getOptionalProperty(VERSION_OF_PROPERTY).orElse(DEFAULT_VERSION_OF_KEY);
        return new LatestStateFilter(getStatePersistence().getMemberVersionRepository(), getKeepState(), timestampPath, versionOfPath);
    }

    private ExactlyOnceFilter getExactlyOnceFilter() {
        return new ExactlyOnceFilter(getStatePersistence().getMemberIdRepository(), getKeepState());
    }

    private TreeNodeProcessor getTreeNodeProcessor() {
        List<String> targetUrls = properties.getPropertyList(URLS);

        if (targetUrls.isEmpty()) {
            throw new ConfigPropertyMissingException(properties.getPipelineName(), properties.getComponentName(), "urls");
        }

        Lang sourceFormat = getSourceFormat();
        LdesMetaData ldesMetaData = new LdesMetaData(targetUrls, sourceFormat);
        TimestampExtractor timestampExtractor = properties.getOptionalProperty(TIMESTAMP_PATH_PROP)
                .map(timestampPath -> (TimestampExtractor) new TimestampFromPathExtractor(createProperty(timestampPath)))
                .orElseGet(TimestampFromCurrentTimeExtractor::new);

        return new TreeNodeProcessor(ldesMetaData, getStatePersistence(), requestExecutor, timestampExtractor);
    }

    private StatePersistence getStatePersistence() {
        return new StatePersistenceFactory().getStatePersistence(properties);
    }

    private Boolean getKeepState() {
        return properties.getOptionalBoolean(KEEP_STATE).orElse(false);
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

    private boolean useExactlyOnceFilter() {
        Optional<Boolean> exactlyOneFilterProperty = properties.getOptionalBoolean(USE_EXACTLY_ONCE_FILTER);
        if (exactlyOneFilterProperty.isPresent()) {
            // use filter is explicitly set
            boolean useFilter = exactlyOneFilterProperty.get();
            if (useVersionMaterialisation() && useFilter) {
                throw new InvalidConfigException("The exactly once filter can not be enabled with version materialisation.");
            } else {
                return useFilter;
            }
        } else {
            // use filter is not explicitly set
            if (useVersionMaterialisation()) {
                log.warn("The exactly once filter can not be used while version materialisation is active, disabling filter");
                return false;
            } else {
                return true;
            }
        }
    }

    private Lang getSourceFormat() {
        return properties.getOptionalProperty(SOURCE_FORMAT)
                .map(RDFLanguages::nameToLang)
                .orElse(Lang.TURTLE);
    }

    private VersionMaterialiser createVersionMaterialiser() {
        final Property versionOfProperty = properties
                .getOptionalProperty(VERSION_OF_PROPERTY)
                .map(ResourceFactory::createProperty)
                .orElseGet(() -> createProperty(DEFAULT_VERSION_OF_KEY));
        return new VersionMaterialiser(versionOfProperty, false);
    }

    private boolean useLatestStateFilter() {
        return properties.getOptionalBoolean(USE_LATEST_STATE_FILTER).orElse(true);
    }


}
