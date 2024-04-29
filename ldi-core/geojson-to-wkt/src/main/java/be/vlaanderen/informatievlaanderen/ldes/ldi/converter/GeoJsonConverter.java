package be.vlaanderen.informatievlaanderen.ldes.ldi.converter;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import java.util.List;

public interface GeoJsonConverter {
    String GEOSPARQL_URI = "http://www.opengis.net/ont/geosparql";

    List<Statement> createNewGeometryStatements(Statement statement, Model geometryModel);

    default RDFDatatype getWktLiteralDataType() {
        return TypeMapper.getInstance().getSafeTypeByName(GEOSPARQL_URI + "#wktLiteral");
    }
}
