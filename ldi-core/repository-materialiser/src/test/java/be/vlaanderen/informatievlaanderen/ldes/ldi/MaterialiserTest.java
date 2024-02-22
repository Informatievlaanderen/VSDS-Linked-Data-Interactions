package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sparql.federation.CollectionIteration;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileInputStream;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialiserTest {
	private static final String[] TEST_FILES = new String[]{
			"src/test/resources/people_data_01.nq",
			"src/test/resources/people_data_02.nq"};
	private static final String CHANGED_FILE = "src/test/resources/people_data_03.nq";

	private static final String REPOSITORY_ID = "repo-id";
	private static final int BATCH_TIMEOUT = 2000;
	@Mock
	private RepositoryConnection connection;
	@Mock
	private RepositoryManager repositoryManager;
	@Mock
	private Repository repository;
	private Materialiser materialiser;

	@AfterEach
	void tearDown() {
		materialiser.shutdown();
	}

	@Nested
	class BatchSizeOne {
		private static final int BATCH_SIZE = 1;

		@BeforeEach
		void setUp() {
			materialiser = new Materialiser(repositoryManager, REPOSITORY_ID, "", BATCH_SIZE, BATCH_TIMEOUT);
		}

		@Test
		void when_DataPresent_Then_GetEntityIds() throws Exception {
			var updateModel = Rio.parse(new FileInputStream(TEST_FILES[0]), "", RDFFormat.NQUADS);

			Set<Resource> entityIds = Materialiser.getSubjectsFromModel(updateModel);

			assertThat(entityIds)
					.as("Expected all subjects from test data")
					.hasSize(2);
			verifyNoInteractions(repositoryManager, repository, connection);
		}


		@Test
		void when_DeleteEntities_Then_EntitiesRemovedFromStore() throws Exception {
			when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
			when(repository.getConnection()).thenReturn(connection);
			when(connection.getStatements(any(), isNull(), isNull()))
					.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())));
			Model updateModel = Rio.parse(new FileInputStream(TEST_FILES[0]), "", RDFFormat.NQUADS);
			Set<Resource> entityIds = Materialiser.getSubjectsFromModel(updateModel);

			materialiser.deleteEntitiesFromRepo(entityIds);

			Stream.of("http://somewhere/SarahJones/", "http://somewhere/MattJones/")
					.map(subject -> SimpleValueFactory.getInstance().createIRI(subject))
					.forEach(subjectIri -> verify(connection).remove(subjectIri, null, null));
		}

		@Test
		void when_UpdateEntities_Then_OldTriplesRemoved() throws Exception {
			when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
			when(repository.getConnection()).thenReturn(connection);
			when(connection.getStatements(any(), isNull(), isNull()))
					.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())));
			Model changedModel = Rio.parse(new FileInputStream(CHANGED_FILE), "", RDFFormat.NQUADS);

			materialiser.process(RDFParser.source(CHANGED_FILE).toModel());

			verify(connection).remove(SimpleValueFactory.getInstance().createIRI("http://somewhere/DickJones/"), null, null);
			verify(connection).remove(SimpleValueFactory.getInstance().createIRI("http://somewhere/SarahJones/"), null, null);
			verify(connection).add(changedModel);
			verify(connection).commit();
		}
	}

	@Nested
	class BatchSizeFive {
		private static final int BATCH_SIZE = 5;

		@BeforeEach
		void setUp() {
			when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
			when(repository.getConnection()).thenReturn(connection);
			materialiser = new Materialiser(repositoryManager, REPOSITORY_ID, "", BATCH_SIZE, BATCH_TIMEOUT);
		}

		@Test
		void when_BatchSizeReachedTwice_then_CommitTwice() {
			when(connection.getStatements(any(), isNull(), isNull()))
					.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())));

			RDFParser.source("10_people_data.nq").lang(Lang.NQ).toModel()
					.listStatements()
					.toList()
					.stream()
					.map(statement -> ModelFactory.createDefaultModel().add(statement))
					.forEach(materialiser::process);


			verify(connection, times(10)).remove(any(Resource.class), isNull(), isNull());
			verify(connection, times(10)).add(any(Model.class));
			verify(connection, times(2)).commit();
		}

	}

	@Nested
	class BatchSizeFifteen {
		private static final int BATCH_SIZE = 15;

		@BeforeEach
		void setUp() {
			when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
			when(repository.getConnection()).thenReturn(connection);
			materialiser = new Materialiser(repositoryManager, REPOSITORY_ID, "", BATCH_SIZE, BATCH_TIMEOUT);
		}

		@Test
		void when_BatchSizeIsNotReached_then_CommitAfterBatchTimeout() {
			when(connection.getStatements(any(), isNull(), isNull()))
					.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())));

			readTenModelsFromFile().forEach(materialiser::process);

			verify(connection, times(10)).remove(any(Resource.class), isNull(), isNull());
			verify(connection, times(10)).add(any(Model.class));
			verify(connection, timeout(BATCH_TIMEOUT)).commit();
		}

		@Test
		void when_ErrorOccursHalfway_then_NoModelsAreCommitted() {
			when(connection.getStatements(any(), isNull(), isNull()))
					.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())))
					.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())))
					.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())))
					.thenThrow(RuntimeException.class);

			try {
				readTenModelsFromFile().forEach(materialiser::process);
			} catch (Exception e) {
				assertThat(e)
						.isInstanceOf(MaterialisationFailedException.class)
						.hasCauseInstanceOf(RuntimeException.class);
			}

			verify(connection, times(3)).remove(any(Resource.class), isNull(), isNull());
			verify(connection, times(3)).add(any(Model.class));
			verify(connection).rollback();
			verify(connection, never()).commit();

		}

		private Stream<org.apache.jena.rdf.model.Model> readTenModelsFromFile() {
			return RDFParser.source("10_people_data.nq").lang(Lang.NQ).toModel()
					.listStatements()
					.toList()
					.stream()
					.map(statement -> ModelFactory.createDefaultModel().add(statement));
		}

	}
}