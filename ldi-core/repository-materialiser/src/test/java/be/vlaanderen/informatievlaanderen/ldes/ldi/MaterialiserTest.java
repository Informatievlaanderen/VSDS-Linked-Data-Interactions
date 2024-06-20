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
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sparql.federation.CollectionIteration;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileInputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialiserTest {
	private static final String CHANGED_FILE = "src/test/resources/people_data_03.nq";

	private static final String REPOSITORY_ID = "repo-id";
	@Mock
	private RepositoryConnection connection;
	@Mock
	private RepositoryManager repositoryManager;
	@Mock
	private Repository repository;
	private Materialiser materialiser;

	@BeforeEach
	void setUp() {
		when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
		when(repository.getConnection()).thenReturn(connection);
		materialiser = new Materialiser(repositoryManager, REPOSITORY_ID, "");
	}

	@AfterEach
	void tearDown() {
		materialiser.shutdown();
	}

	@Test
	void when_DeleteEntities_Then_EntitiesRemovedFromStore() throws Exception {
		when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
		when(repository.getConnection()).thenReturn(connection);
		Set<Resource> entityIds = Stream
				.of("http://somewhere/DickJones/", "http://somewhere/SarahJones/")
				.map(stringUri -> SimpleValueFactory.getInstance().createIRI(stringUri))
				.collect(Collectors.toSet());
		when(connection.getStatements(any(), isNull(), isNull()))
				.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())));

		Model modelToDelete = Rio.parse(new FileInputStream(CHANGED_FILE), "", RDFFormat.NQUADS);

		materialiser.deleteEntity(modelToDelete);
		entityIds.forEach(subjectIri -> {
			verify(connection).getStatements(subjectIri, null, null);
			verify(connection).remove(subjectIri, null, null);
		});
	}

	@Test
	void given_RepositoryContainingData_when_UpdateSingleModel_Then_OldTriplesRemoved() throws Exception {
		when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
		when(repository.getConnection()).thenReturn(connection);
		when(connection.getStatements(any(), isNull(), isNull()))
				.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())));
		Model changedModel = Rio.parse(new FileInputStream(CHANGED_FILE), "", RDFFormat.NQUADS);

		materialiser.process(List.of(RDFParser.source(CHANGED_FILE).toModel()));

		verify(connection).remove(SimpleValueFactory.getInstance().createIRI("http://somewhere/DickJones/"), null, null);
		verify(connection).remove(SimpleValueFactory.getInstance().createIRI("http://somewhere/SarahJones/"), null, null);
		verify(connection).add(changedModel);
		verify(connection).commit();
	}

	@Test
	void given_ValidListOfMembers_when_ProcessList_then_CommitToRepository() {
		when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
		when(repository.getConnection()).thenReturn(connection);
		when(connection.getStatements(any(), isNull(), isNull()))
				.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())));

		List<org.apache.jena.rdf.model.Model> models = readTenModelsFromFile().toList();

		materialiser.process(models);

		verify(connection, times(10)).remove(any(Resource.class), isNull(), isNull());
		verify(connection, times(10)).add(any(Model.class));
		verify(connection).commit();
	}

	@Test
	void given_ValidList_when_ProcessList_and_CommitFails_then_RollbackConnection() {
		when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
		when(repository.getConnection()).thenReturn(connection);
		when(connection.getStatements(any(), isNull(), isNull()))
				.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())));
		doThrow(RepositoryException.class).when(connection).commit();

		List<org.apache.jena.rdf.model.Model> models = readTenModelsFromFile().toList();

		assertThatThrownBy(() -> materialiser.process(models))
				.isInstanceOf(MaterialisationFailedException.class)
				.hasCauseInstanceOf(RepositoryException.class);

		verify(connection, times(10)).add(any(Model.class));
		verify(connection).commit();
		verify(connection).rollback();
	}

	@Test
	void given_ListOfMembers_when_ProcessList_and_AddModelToConnectionFails_then_StopAdditionAndRollback() {
		when(repositoryManager.getRepository(REPOSITORY_ID)).thenReturn(repository);
		when(repository.getConnection()).thenReturn(connection);
		when(connection.getStatements(any(), isNull(), isNull()))
				.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())));
		doNothing().doNothing().doThrow(RepositoryException.class).when(connection).add(any(Model.class));

		List<org.apache.jena.rdf.model.Model> models = readTenModelsFromFile().toList();

		assertThatThrownBy(() -> materialiser.process(models))
				.isInstanceOf(MaterialisationFailedException.class)
				.hasCauseInstanceOf(RepositoryException.class);

		verify(connection, times(3)).add(any(Model.class));
		verify(connection, never()).commit();
		verify(connection).rollback();
	}

	@Test
	void given_ListOfMembers_when_ProcessList_and_GetStatementsFails_then_StopAdditionAndRollback() {
		when(connection.getStatements(any(), isNull(), isNull()))
				.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())))
				.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())))
				.thenReturn(new RepositoryResult<>(new CollectionIteration<>(Set.of())))
				.thenThrow(RepositoryException.class);

		List<org.apache.jena.rdf.model.Model> models = readTenModelsFromFile().toList();

		assertThatThrownBy(() -> materialiser.process(models))
				.isInstanceOf(MaterialisationFailedException.class)
				.hasCauseInstanceOf(RepositoryException.class);

		verify(connection, times(4)).getStatements(any(), isNull(), isNull());
		verify(connection, times(3)).remove(any(Resource.class), isNull(), isNull());		verify(connection, times(3)).add(any(Model.class));
		verify(connection, never()).commit();
		verify(connection).rollback();
	}

	private Stream<org.apache.jena.rdf.model.Model> readTenModelsFromFile() {
		return RDFParser.source("10_people_data.nq").lang(Lang.NQ).toModel()
				.listStatements()
				.toList()
				.stream()
				.map(statement -> ModelFactory.createDefaultModel().add(statement));
	}
}
