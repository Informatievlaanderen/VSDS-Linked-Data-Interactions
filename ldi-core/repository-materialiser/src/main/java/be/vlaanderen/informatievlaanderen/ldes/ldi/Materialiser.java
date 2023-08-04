package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.base.AbstractIRI;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Materialiser {

    private final String REPOSITORY_ID;
    private final String namedGraph;
    protected RepositoryManager repositoryManager;

    public Materialiser(String repositoryId, String namedGraph) {
        REPOSITORY_ID = repositoryId;
        this.namedGraph = namedGraph;
    }

    protected void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public void initManager(String hostUrl) {
        if (repositoryManager == null) {
            setRepositoryManager(new RemoteRepositoryManager(hostUrl));
        }
    }
    public void process(InputStream in) throws IOException {


        final Repository repository = repositoryManager.getRepository(REPOSITORY_ID);
        final var committed = new AtomicBoolean(false);

        try (RepositoryConnection dbConnection = repository.getConnection()) {
            // As we are bulk-loading, set isolation level to none for improved performance.
            dbConnection.setIsolationLevel(IsolationLevels.NONE);
            dbConnection.begin();

            var updateModel = Rio.parse(in, "", RDFFormat.NQUADS);

            Set<Resource> entityIds = getSubjectsFromModel(updateModel);

            // Delete the old version of the entity (ldes member) from the db.
            deleteEntitiesFromRepo(entityIds, dbConnection);

            // Save the new data to the DB.
            if (namedGraph != null && !namedGraph.isEmpty()) {
                var namedGraphIRI = dbConnection.getValueFactory().createIRI(namedGraph);
                dbConnection.add(updateModel, namedGraphIRI);
            } else {
                dbConnection.add(updateModel);
            }

            dbConnection.commit();
            committed.set(true);
        }
    }

    /**
     * Returns all subjects ('real' URIs) present in the model.
     *
     * @param model
     *            A graph
     * @return A set of subject URIs.
     */
    protected static Set<Resource> getSubjectsFromModel(Model model) {
        Set<Resource> entityIds = new HashSet<>();

        model.subjects().forEach(subject -> {
            if (subject instanceof AbstractIRI) {
                entityIds.add(subject);
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
    protected static void deleteEntitiesFromRepo(Set<Resource> entityIds, RepositoryConnection connection) {
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

            connection.getStatements(subject, null, null).forEach((Statement statement) -> {
                Value object = statement.getObject();
                if (object.isBNode()) {
                    subjectStack.push((Resource) object);
                }
            });

            connection.remove(subject, null, null);
        }
    }
}
