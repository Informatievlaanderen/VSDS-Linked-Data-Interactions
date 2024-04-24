package be.vlaanderen.informatievlaanderen.ldes.ldi.converter;

import be.vlaanderen.informatievlaanderen.ldes.ldi.WktConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.WktResult;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;

import java.util.List;

public class GeoJsonToWktConverter implements GeoJsonConverter {

    private final WktConverter wktConverter = new WktConverter();

    public List<Statement> createNewGeometryStatement(Statement oldStatement, Model geometryModel) {
        final WktResult wktResult = wktConverter.getWktFromModel(geometryModel);
        final Literal wktLiteral = ResourceFactory.createTypedLiteral(wktResult.wkt(), getWktLiteralDataType());
        final Property geometryPredicate = ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry");
        return List.of(ResourceFactory.createStatement(oldStatement.getSubject(), geometryPredicate, wktLiteral));
    }

    private RDFDatatype getWktLiteralDataType() {
        return TypeMapper.getInstance().getSafeTypeByName("http://www.opengis.net/ont/geosparql#wktLiteral");
    }

}
