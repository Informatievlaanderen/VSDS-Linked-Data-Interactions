package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusConsumer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;
import ldes.client.treenodesupplier.filters.LatestStateFilter;
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
import static org.mockito.Mockito.mock;

class MemberSupplierFactoryTest {

	private Map<String, String> defaultInputConfig;
	private final ClientStatusConsumer statusConsumer = mock(ClientStatusConsumer.class);

	@BeforeEach
	void setUp() {
		defaultInputConfig = new HashMap<>();
		defaultInputConfig.put(URLS, "http://example.org");
	}

	@Test
	void when_VersionMaterialisationIsEnabled_then_VersionMaterialisedMemberSupplierIsReturned() {
		defaultInputConfig.put(USE_VERSION_MATERIALISATION, "true");
		final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

		MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null, statusConsumer).getMemberSupplier();

		assertThat(memberSupplier).isInstanceOf(VersionMaterialisedMemberSupplier.class);
	}

	@Test
	void when_VersionMaterialisationAndOnlyOnceFilterAreNotEnabled_then_MemberSupplierImplIsReturned() {
		defaultInputConfig.put(USE_EXACTLY_ONCE_FILTER, "false");
		final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

		MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null, statusConsumer).getMemberSupplier();

		assertThat(memberSupplier).isInstanceOf(MemberSupplierImpl.class);
	}
	@Test
	void when_VersionMaterialisationIsNotEnabled_then_OnlyOnceMemberSupplierIsReturned() {
		final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

		MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null, statusConsumer).getMemberSupplier();

		assertThat(memberSupplier).isInstanceOf(FilteredMemberSupplier.class);
	}

	@Test
	void when_LatestStateFilterIsEnabled_then_returnVersionMaterialisedMemberSupplierWithLatestStateFilter() {
		defaultInputConfig.put(USE_VERSION_MATERIALISATION, "true");
		defaultInputConfig.put(USE_LATEST_STATE_FILTER, "true");
		final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

		MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null, statusConsumer).getMemberSupplier();

		assertThat(memberSupplier)
				.isInstanceOf(VersionMaterialisedMemberSupplier.class)
				.extracting("memberSupplier").isInstanceOf(FilteredMemberSupplier.class)
				.extracting("filter").isInstanceOf(LatestStateFilter.class);
	}

	@Test
	void when_LatestStateFilterIsDisabled_then_returnVersionMaterialisedMemberSupplierImpl() {
		defaultInputConfig.put(USE_VERSION_MATERIALISATION, "true");
		defaultInputConfig.put(USE_LATEST_STATE_FILTER, "false");
		final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

		MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null, statusConsumer).getMemberSupplier();

		assertThat(memberSupplier)
				.isInstanceOf(VersionMaterialisedMemberSupplier.class)
				.extracting("memberSupplier").isInstanceOf(MemberSupplierImpl.class);
	}

	@Test
	void when_LatestStateFilterIsEnabledWithoutMaterialisation_then_returnMemberSupplierImpl() {
		defaultInputConfig.put(USE_VERSION_MATERIALISATION, "false");
		defaultInputConfig.put(USE_EXACTLY_ONCE_FILTER, "false");
		defaultInputConfig.put(USE_LATEST_STATE_FILTER, "true");
		final var componentProperties = new ComponentProperties("pipelineName", "cName", defaultInputConfig);

		MemberSupplier memberSupplier = new MemberSupplierFactory(componentProperties, null, statusConsumer).getMemberSupplier();

		assertThat(memberSupplier).isInstanceOf(MemberSupplierImpl.class);
	}

	@Test
	void when_NoUrlsAreConfigured_then_ThrowException() {
		final String expectedErrorMessage = "Pipeline \"pipelineName\": \"cName\" : Missing value for property \"urls\" .";
		final var componentProperties = new ComponentProperties("pipelineName", "cName", Map.of("url", "http://localhost:8080/ldes"));
		final MemberSupplierFactory memberSupplierFactory = new MemberSupplierFactory(componentProperties, null, statusConsumer);
		assertThatThrownBy(memberSupplierFactory::getMemberSupplier)
				.isInstanceOf(ConfigPropertyMissingException.class)
				.hasMessage(expectedErrorMessage);
	}
}