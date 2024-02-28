package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdioMaterialiserRepositoryBatchCollectorTest {

	private static final int BATCH_TIMEOUT = 2000;
	@Mock
	private Materialiser materialiser;
	private LdioMaterialiserRepositoryBatchCollector ldioMaterialiserRepositoryBatchCollector;

	@AfterEach
	void tearDown() {
		ldioMaterialiserRepositoryBatchCollector.shutdown();
	}


	@Nested
	class ProcessList {
		private static final int BATCH_SIZE = 10;

		@BeforeEach
		void setUp() {
			ldioMaterialiserRepositoryBatchCollector = new LdioMaterialiserRepositoryBatchCollector(
					BATCH_SIZE,
					BATCH_TIMEOUT,
					materialiser
			);
		}

		@Test
		void given_ValidListOfMembers_when_ProcessList_then_CommitToRepository() {
			readTenModelsFromFile()
					.forEach(ldioMaterialiserRepositoryBatchCollector::addMemberToCommit);

			verify(materialiser).process(anyList());
		}

		@Test
		void given_ValidList_when_ProcessList_And_CommitFails_then_RollbackConnection() {
			doThrow(MaterialisationFailedException.class).when(materialiser).process(anyList());

			final List<org.apache.jena.rdf.model.Model> models = readTenModelsFromFile().toList();
			for (int i = 0; i < models.size(); i++) {
				try {
					ldioMaterialiserRepositoryBatchCollector.addMemberToCommit(models.get(i));
				} catch (Exception e) {
					assertThat(e).isInstanceOf(MaterialisationFailedException.class);
					assertThat(i + 1)
							.as("Exception should only be thrown when batch size has been reached")
							.isEqualTo(BATCH_SIZE);
				}
			}

			verify(materialiser).process(anyList());
		}
	}

	@Nested
	class BatchSizeFive {
		private static final int BATCH_SIZE = 5;

		@BeforeEach
		void setUp() {
			ldioMaterialiserRepositoryBatchCollector = new LdioMaterialiserRepositoryBatchCollector(BATCH_SIZE, BATCH_TIMEOUT, materialiser);
		}

		@Test
		void when_BatchSizeReachedTwice_then_ProcessListTwice() {
			readTenModelsFromFile().forEach(ldioMaterialiserRepositoryBatchCollector::addMemberToCommit);

			verify(materialiser, times(2)).process(anyList());
		}




	}

	@Nested
	class BatchSizeFifteen {
		private static final int BATCH_SIZE = 15;

		@BeforeEach
		void setUp() {
			ldioMaterialiserRepositoryBatchCollector = new LdioMaterialiserRepositoryBatchCollector(BATCH_SIZE, BATCH_TIMEOUT, materialiser);
		}

		@Test
		void when_BatchSizeIsNotReached_then_CommitAfterBatchTimeout() {
			readTenModelsFromFile().forEach(ldioMaterialiserRepositoryBatchCollector::addMemberToCommit);

			verify(materialiser, timeout(BATCH_TIMEOUT)).process(anyList());
		}
	}

	private Stream<org.apache.jena.rdf.model.Model> readTenModelsFromFile() {
		return RDFParser.source("10_people_data.nq").lang(Lang.NQ).toModel()
				.listStatements()
				.toList()
				.stream()
				.map(statement -> ModelFactory.createDefaultModel().add(statement));
	}
}
