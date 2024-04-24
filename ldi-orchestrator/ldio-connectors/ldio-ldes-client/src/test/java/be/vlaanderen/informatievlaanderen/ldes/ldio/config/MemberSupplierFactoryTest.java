package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.ConfigPropertyMissingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplierImpl;
import ldes.client.treenodesupplier.membersuppliers.VersionMaterialisedMemberSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
	void when_VersionMaterialisationAndOnlyOnceFilterAreNotEnabled_then_MemberSupplierImplIsReturned() {
		defaultInputConfig.put(USE_EXACTLY_ONCE_FILTER, "false");
		final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

		MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null).getMemberSupplier();

		assertThat(memberSupplier).isInstanceOf(MemberSupplierImpl.class);
	}
	@Test
	void when_VersionMaterialisationIsNotEnabled_then_OnlyOnceMemberSupplierIsReturned() {
		final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

		MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null).getMemberSupplier();

		assertThat(memberSupplier).isInstanceOf(FilteredMemberSupplier.class);
	}

	@Test
	void when_NoUrlsAreConfigured_then_ThrowException() {
		final String expectedErrorMessage = "Pipeline \"pipelineName\": \"cName\" : Missing value for property \"urls\" .";
		final var componentProperties = new ComponentProperties("pipelineName", "cName", Map.of("url", "http://localhost:8080/ldes"));
		final MemberSupplierFactory memberSupplierFactory = new MemberSupplierFactory(componentProperties, null);
		assertThatThrownBy(memberSupplierFactory::getMemberSupplier)
				.isInstanceOf(ConfigPropertyMissingException.class)
				.hasMessage(expectedErrorMessage);
	}
}