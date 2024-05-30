package be.vlaanderen.informatievlaanderen.ldes.ldi.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import java.util.List;

public interface GeoJsonConverter {

    /**
     * Creates new Geometry statements based on
     * @param statement
     * @param geometryModel
     * @return
     */
    List<Statement> createNewGeometryStatements(Statement statement, Model geometryModel);

}
