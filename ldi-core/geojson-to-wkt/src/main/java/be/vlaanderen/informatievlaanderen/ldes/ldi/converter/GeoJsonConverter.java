package be.vlaanderen.informatievlaanderen.ldes.ldi.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import java.util.List;

public interface GeoJsonConverter {

    List<Statement> createNewGeometryStatement(Statement statement, Model geometryModel);

}
