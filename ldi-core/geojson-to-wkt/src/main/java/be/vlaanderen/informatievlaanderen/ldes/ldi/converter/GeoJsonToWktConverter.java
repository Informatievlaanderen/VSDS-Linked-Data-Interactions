package be.vlaanderen.informatievlaanderen.ldes.ldi.converter;

import be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.WktResult;
import org.apache.jena.rdf.model.*;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter.getWktLiteralDataType;

public class GeoJsonToWktConverter implements GeoJsonConverter {

    private final WktConverter wktConverter = new WktConverter();

    public List<Statement> createNewGeometryStatements(Statement oldStatement, Model geometryModel) {
        final WktResult wktResult = wktConverter.getWktFromModel(geometryModel);
        final Literal wktLiteral = ResourceFactory.createTypedLiteral(wktResult.wkt(), getWktLiteralDataType());
        final Property geometryPredicate = ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry");
        return List.of(ResourceFactory.createStatement(oldStatement.getSubject(), geometryPredicate, wktLiteral));
    }

}
