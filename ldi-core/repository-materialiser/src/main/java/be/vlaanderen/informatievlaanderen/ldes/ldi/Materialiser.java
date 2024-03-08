package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.JenaToRDF4JConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ModelSubjectsExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MaterialiserConnection;
import org.apache.jena.rdf.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

import java.util.List;
import java.util.Set;

public class Materialiser {
	private final MaterialiserConnection materialiserConnection;

	public Materialiser(String hostUrl, String repositoryId, String namedGraph) {
		this(new RemoteRepositoryManager(hostUrl), repositoryId, namedGraph);
	}

	public Materialiser(RepositoryManager repositoryManager, String repositoryId, String namedGraph) {
		this.materialiserConnection = new MaterialiserConnection(repositoryManager, repositoryId, namedGraph);
	}

	protected MaterialiserConnection getMaterialiserConnection() {
		return materialiserConnection;
	}

	public void process(List<Model> jenaModels) {
		try {
			jenaModels.stream().map(JenaToRDF4JConverter::convert).forEach(updateModel -> {
				Set<Resource> entityIds = ModelSubjectsExtractor.extractSubjects(updateModel);
				deleteEntities(entityIds);
				materialiserConnection.add(updateModel);
			});
			materialiserConnection.commit();
		} catch (Exception e) {
			materialiserConnection.rollback();
			throw new MaterialisationFailedException(e, jenaModels);
		}
	}

	public void shutdown() {
		materialiserConnection.shutdown();
	}

	protected void deleteEntities(Set<Resource> subjects) {
		subjects.forEach(this::deleteEntity);
	}

	protected void deleteEntity(Resource subject) {
		final String query = """
				DELETE {
				      ?s ?p ?o .
				      ?blankNode ?blankNodePredicate ?blankNodeObject .
				    }
				    WHERE {
				      ?s ?p ?o .
				      FILTER(?s = <%s>)

				      OPTIONAL {
				        {
				          ?o ?blankNodePredicate ?blankNodeObject .
				          FILTER(isBlank(?o))
				          BIND(?o AS ?blankNode)
				        }
				        UNION
				        {
				          ?o ?nestedPredicate ?nestedObject .
				          FILTER(isBlank(?nestedObject))
				          ?nestedObject ?blankNodePredicate ?blankNodeObject .
				          BIND(?nestedObject AS ?blankNode)
				        }
				      }
				    }
				    """.formatted(subject.stringValue());

		materialiserConnection.executeUpdate(query);
	}
}
