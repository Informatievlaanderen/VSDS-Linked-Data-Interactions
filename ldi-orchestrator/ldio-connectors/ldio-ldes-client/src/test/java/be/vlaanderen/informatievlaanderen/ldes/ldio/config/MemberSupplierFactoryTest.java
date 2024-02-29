package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.MemberSupplierImpl;
import ldes.client.treenodesupplier.VersionMaterialisedMemberSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.URLS;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.USE_VERSION_MATERIALISATION;
import static org.assertj.core.api.Assertions.assertThat;

class MemberSupplierFactoryTest {

    private Map<String, String> defaultInputConfig;

    @BeforeEach
    void setUp() {
        defaultInputConfig = new HashMap<>();
        defaultInputConfig.put(URLS, "http://example.org");
    }

    @Test
    void when_VersionMaterialisationIsEnabled_then_VersionMaterialisedMemberSupplierIsReturned() {
        defaultInputConfig.put(USE_VERSION_MATERIALISATION, "true");
        final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

        MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null).getMemberSupplier();

        assertThat(memberSupplier).isInstanceOf(VersionMaterialisedMemberSupplier.class);
    }

    @Test
    void when_VersionMaterialisationIsNotEnabled_then_MemberSupplierImplIsReturned() {
        final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

        MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null).getMemberSupplier();

        assertThat(memberSupplier).isInstanceOf(MemberSupplierImpl.class);
    }

}