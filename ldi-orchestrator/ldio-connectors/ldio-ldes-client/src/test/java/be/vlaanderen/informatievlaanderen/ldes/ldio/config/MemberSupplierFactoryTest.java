package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.MemberSupplierImpl;
import ldes.client.treenodesupplier.VersionMaterialisedMemberSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MemberSupplierFactoryTest {

    private Map<String, String> defaultInputConfig;

    @BeforeEach
    void setUp() {
        defaultInputConfig = new HashMap<>();
        defaultInputConfig.put("url", "http://example.org");
        defaultInputConfig.put("pipeline.name", "demo-pipeline");
    }

    @Test
    void when_VersionMaterialisationIsEnabled_then_VersionMaterialisedMemberSupplierIsReturned() {
        defaultInputConfig.put("use-version-materialisation", "true");
        final var componentProperties = new ComponentProperties(defaultInputConfig);

        MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null).getMemberSupplier();

        assertThat(memberSupplier).isInstanceOf(VersionMaterialisedMemberSupplier.class);
    }

    @Test
    void when_VersionMaterialisationIsNotEnabled_then_MemberSupplierImplIsReturned() {
        final var componentProperties = new ComponentProperties(defaultInputConfig);

        MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null).getMemberSupplier();

        assertThat(memberSupplier).isInstanceOf(MemberSupplierImpl.class);
    }

}