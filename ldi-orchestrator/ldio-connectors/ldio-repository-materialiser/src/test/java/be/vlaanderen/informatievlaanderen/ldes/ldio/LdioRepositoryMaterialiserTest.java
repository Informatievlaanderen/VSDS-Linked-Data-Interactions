package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import org.apache.jena.rdf.model.Model;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdioRepositoryMaterialiserTest {
	private static final int BATCH_TIMEOUT = 2000;
	@Mock
	private Materialiser materialiser;

	private LdioRepositoryMaterialiser ldioRepositoryMaterialiser;

	@AfterEach
	void tearDown() {
		ldioRepositoryMaterialiser.shutdown();
	}


	@Nested
	class ProcessList {
		private static final int BATCH_SIZE = 10;

		@BeforeEach
		void setUp() {
			ldioRepositoryMaterialiser = new LdioRepositoryMaterialiser(materialiser, BATCH_SIZE, BATCH_TIMEOUT);
			ldioRepositoryMaterialiser.start();
		}

		@Test
		void given_ValidListOfMembers_when_ProcessList_then_CommitToRepository() {
			when(materialiser.processAsync(anyList())).thenReturn(new CompletableFuture<>());

			readTenModelsFromFile()
					.forEach(ldioRepositoryMaterialiser::accept);

			verify(materialiser).processAsync(anyList());
		}

		@Test
		void given_ValidList_when_ProcessList_And_CommitFails_then_RollbackConnection() {
			doThrow(MaterialisationFailedException.class).when(materialiser).processAsync(anyList());

			final List<Model> models = readTenModelsFromFile().toList();
			for (int i = 0; i < models.size(); i++) {
				try {
					ldioRepositoryMaterialiser.accept(models.get(i));
				} catch (Exception e) {
					assertThat(e).isInstanceOf(MaterialisationFailedException.class);
					assertThat(i + 1)
							.as("Exception should only be thrown when batch size has been reached")
							.isEqualTo(BATCH_SIZE);
				}
			}

			verify(materialiser).processAsync(anyList());
		}

		@Test
		void given_ValidList_when_ProcessList_And_AsyncProcessingFailed_then_RollbackConnection() {
			when(materialiser.processAsync(anyList()))
					.thenReturn(CompletableFuture.failedFuture(new MaterialisationFailedException(new RuntimeException())));

			readTenModelsFromFile().forEach(ldioRepositoryMaterialiser::accept);

			verify(materialiser).processAsync(anyList());
		}
	}

	@Nested
	class BatchSizeFive {
		private static final int BATCH_SIZE = 5;

		@BeforeEach
		void setUp() {
			ldioRepositoryMaterialiser = new LdioRepositoryMaterialiser(materialiser, BATCH_SIZE, BATCH_TIMEOUT);
			ldioRepositoryMaterialiser.start();
		}

		@Test
		void when_BatchSizeReachedTwice_then_ProcessListTwice() {
			when(materialiser.processAsync(anyList())).thenReturn(new CompletableFuture<>());

			readTenModelsFromFile().forEach(ldioRepositoryMaterialiser::accept);

			verify(materialiser, times(2)).processAsync(anyList());
		}
	}

	@Nested
	class BatchSizeFifteen {
		private static final int BATCH_SIZE = 15;

		@BeforeEach
		void setUp() {
			ldioRepositoryMaterialiser = new LdioRepositoryMaterialiser(materialiser, BATCH_SIZE, BATCH_TIMEOUT);
			ldioRepositoryMaterialiser.start();
		}

		@Test
		void when_BatchSizeIsNotReached_then_CommitAfterBatchTimeout() {
			readTenModelsFromFile().forEach(ldioRepositoryMaterialiser::accept);

			verify(materialiser, timeout(BATCH_TIMEOUT)).processAsync(anyList());
		}

		@Test
		void when_PausingLdioRepoMaterialiser_then_CommitLastMembers() {
			when(materialiser.processAsync(anyList())).thenReturn(new CompletableFuture<>());

			readTenModelsFromFile().forEach(ldioRepositoryMaterialiser::accept);
			ldioRepositoryMaterialiser.pause();

			verify(materialiser).processAsync(argThat(list -> list.size() == 10));
		}

		@Test
		void when_StoppingLdioRepoMaterialiser_then_ShutdownMaterialiser() {
			readTenModelsFromFile().forEach(ldioRepositoryMaterialiser::accept);
			ldioRepositoryMaterialiser.shutdown();

			verify(materialiser).shutdown();
			verifyNoMoreInteractions(materialiser);
		}
	}

	private Stream<Model> readTenModelsFromFile() {
		return RDFParser.source("10_people_data.nq").lang(Lang.NQ).toModel()
				.listStatements()
				.toList()
				.stream()
				.map(statement -> ModelFactory.createDefaultModel().add(statement));
	}
}