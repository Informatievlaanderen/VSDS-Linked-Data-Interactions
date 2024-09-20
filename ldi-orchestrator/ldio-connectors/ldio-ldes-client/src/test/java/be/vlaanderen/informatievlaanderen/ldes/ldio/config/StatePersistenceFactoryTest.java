package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepository;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatePersistenceFactoryTest {

	private final String H2_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
	private final StatePersistenceFactory statePersistenceFactory = new StatePersistenceFactory(H2_URL, "sa", "");

	@ParameterizedTest
	@ArgumentsSource(ComponentPropertiesArgumentsProvider.class)
	void when_stateIsAllowedValue_then_StatePersistenceIsCreated(ComponentProperties componentProperties,
	                                                             Class<MemberRepository> expectedMemberRepositoryClass,
	                                                             Class<TreeNodeRecordRepository> expectedTreeNodeRecordRepositoryClass) {
		StatePersistence statePersistence = statePersistenceFactory.getStatePersistence(componentProperties);

		MemberRepository memberRepository = statePersistence.memberRepository();
		assertEquals(expectedMemberRepositoryClass, memberRepository.getClass());
		TreeNodeRecordRepository treeNodeRecordRepository = statePersistence.treeNodeRecordRepository();
		assertEquals(expectedTreeNodeRecordRepositoryClass, treeNodeRecordRepository.getClass());

		memberRepository.destroyState();
		treeNodeRecordRepository.destroyState();
	}

	private static class ComponentPropertiesArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(new ComponentProperties("pipelineName", ""),
							SqlMemberRepository.class,
							SqlTreeNodeRepository.class));
		}
	}

}