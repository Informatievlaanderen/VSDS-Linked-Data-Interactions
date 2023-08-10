package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;

import java.util.*;

public class Materialiser {

	private final String sparqlEndpoint;
	private final String namedGraph;
	protected RDFConnectionRemoteBuilder builder;

	public Materialiser(String hostUrl, String repositoryId, String namedGraph) {
		this(constructRDF4JSparqlEndpoint(hostUrl, repositoryId), namedGraph);
	}

	public Materialiser(String sparqlEndpoint, String namedGraph) {
		this.sparqlEndpoint = sparqlEndpoint;
		this.namedGraph = namedGraph;
	}

	protected void setConnectionBuilder(RDFConnectionRemoteBuilder builder) {
		this.builder = builder;
	}

	public void initConnection() {
		if (builder == null) {
			setConnectionBuilder(
					RDFConnectionRemote.service(sparqlEndpoint));
		}
	}

	public void process(Model content) {
		try (RDFConnection connection = builder.build()) {

			Set<Resource> entityIds = getSubjectsFromModel(content);
			deleteEntitiesFromRepo(entityIds, connection);

			if (namedGraph != null && !namedGraph.isEmpty()) {
				connection.load(namedGraph, content);
			} else {
				connection.load(content);
			}
			connection.commit();
		}
	}

	/**
	 * Returns all subjects ('real' URIs) present in the model.
	 *
	 * @param model
	 *            A graph
	 * @return A set of subject URIs.
	 */
	protected Set<Resource> getSubjectsFromModel(Model model) {
		Set<Resource> entityIds = new HashSet<>();

		model.listStatements().forEach(statement -> {
			if (statement.getSubject().isURIResource()) {
				entityIds.add(statement.getSubject());
			}
		});

		return entityIds;
	}

	/**
	 * Delete an entity, including its blank nodes, from a repository.
	 *
	 * @param entityIds
	 *            The subjects of the entities to delete.
	 * @param connection
	 *            The DB connection.
	 */
	protected void deleteEntitiesFromRepo(Set<Resource> entityIds, RDFConnection connection) {
		Deque<Resource> subjectStack = new ArrayDeque<>();
		entityIds.forEach(subjectStack::push);

		/*
		 * Entities can contain blank node references. All statements with those blank
		 * node identifiers need to be removed as well. As blank nodes can be nested
		 * inside blank nodes, we need to keep track of them as they are encountered by
		 * adding them to the stack.
		 */

		while (!subjectStack.isEmpty()) {
			Resource subject = subjectStack.pop();

			connection.fetch(subject.toString()).listStatements().forEach(statement -> {
				RDFNode object = statement.getObject();
				if (object.isAnon()) {
					subjectStack.push((Resource) object);
				}
			});

			connection.delete(subject.toString());
		}
	}

	private static String constructRDF4JSparqlEndpoint(String hostUrl, String repositoryId) {
		return hostUrl + "/repositories/" + repositoryId + "/statements";
	}
}
