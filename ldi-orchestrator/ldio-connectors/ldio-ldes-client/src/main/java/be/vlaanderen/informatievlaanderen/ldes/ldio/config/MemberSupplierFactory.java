package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.MemberSupplierImpl;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.VersionMaterialisedMemberSupplier;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.*;

public class MemberSupplierFactory {

    private final Logger log = LoggerFactory.getLogger(MemberSupplierFactory.class);

    private final ComponentProperties properties;

    public MemberSupplierFactory(ComponentProperties properties) {
        this.properties = properties;
    }

    public MemberSupplier getMemberSupplier() {
        log.info("Starting LdesClientRunner run setup");
        log.info("LdesClientRunner setup finished");
        final MemberSupplier baseMemberSupplier =
                new MemberSupplierImpl(getTreeNodeProcessor(), getKeepState());
        if (useVersionMaterialisation()) {
            return new VersionMaterialisedMemberSupplier(baseMemberSupplier, createVersionMaterialiser());
        } else {
            return baseMemberSupplier;
        }
    }

    private TreeNodeProcessor getTreeNodeProcessor() {
        String targetUrl = properties.getProperty(LdioLdesClientProperties.URL);
        Lang sourceFormat = getSourceFormat();
        LdesMetaData ldesMetaData = new LdesMetaData(targetUrl, sourceFormat);
        return new TreeNodeProcessor(ldesMetaData, getStatePersistence(), getRequestExecutor());
    }

    private StatePersistence getStatePersistence() {
        return new StatePersistenceFactory().getStatePersistence(properties);
    }

    private RequestExecutor getRequestExecutor() {
        return new LdioRequestExecutorSupplier().getRequestExecutor(properties);
    }

    private Boolean getKeepState() {
        return properties.getOptionalBoolean(LdioLdesClientProperties.KEEP_STATE).orElse(false);
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


}
