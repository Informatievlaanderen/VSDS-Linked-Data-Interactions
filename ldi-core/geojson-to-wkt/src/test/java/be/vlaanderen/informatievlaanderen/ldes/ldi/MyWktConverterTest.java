package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.MyWktConverter.COORDINATES;
import static org.junit.jupiter.api.Assertions.*;

class MyWktConverterTest {

    private final MyWktConverter wktConverter = new MyWktConverter();

    @ParameterizedTest
    @ArgumentsSource(GeoJsonProvider.class)
    void newName(String source, String expectedResult) {
        Model model = RDFParser.source(source).lang(Lang.JSONLD).build().toModel();
        Statement statement = model.listStatements(null, COORDINATES, (RDFNode) null).nextStatement();
        Model nodeWithChildren = getNodeWithChildren(model, statement);
        final String result = wktConverter.getWktFromModel(
                statement.getObject().asResource(), nodeWithChildren, GeoType.MULTIPOLYGON
        );

        assertEquals(expectedResult, result);
    }

    private Model getNodeWithChildren(Model model, Statement node) {
        Set<Statement> statements = getNodeWithChildren(model, node.getObject().asResource(), new HashSet<>());
        statements.add(node);

        Model newModel = ModelFactory.createDefaultModel();
        newModel.add(statements.stream().toList());
        return newModel;
    }

    private Set<Statement> getNodeWithChildren(Model model, Resource node, Set<Statement> statements) {
        List<Statement> list = model.listStatements(node, null, (RDFNode) null).toList();
        list.forEach(i -> {
            if (i.getObject().isAnon()) {
                getNodeWithChildren(model, i.getObject().asResource(), statements);
            }
            statements.add(i);
        });
        return statements;
    }

    @ParameterizedTest
    @ArgumentsSource(GeoJsonProvider.class)
    void name(String source, String expectedResult) {
        final String result = wktConverter.getWktFromModel(
                RDFParser.source(source).lang(Lang.JSONLD).build().toModel()
        );

        assertEquals(expectedResult, result);
    }

    static class GeoJsonProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("geojson-point.json", "POINT (100 0)"),
                    Arguments.of("geojson-linestring.json", "LINESTRING (100 0, 101 1, 102 2)"),
                    Arguments.of("geojson-polygon.json", "POLYGON ((100 0, 101 0, 101 1, 100 1, 100 0), " +
                            "(100.8 0.8, 100.8 0.2, 100.2 0.2, 100.2 0.8, 100.8 0.8), " +
                            "(100.95 0.9, 100.95 0.5, 100.9 0.2, 100.9 0.5, 100.95 0.9))"),
                    Arguments.of("geojson-multipoint.json", "MULTIPOINT ((100 0), (101 1), (102 2))"),
                    Arguments.of("geojson-multilinestring.json", "MULTILINESTRING ((100 0, 101 1), (102 2, 103 3, 104 4))"),
                    Arguments.of("geojson-multipolygon.json", "MULTIPOLYGON (((102 2, 103 2, 103 3, 102 3, 102 2)), ((100 0, 101 0, 101 1, 100 1, 100 0), (100.2 0.2, 100.2 0.8, 100.8 0.8, 100.8 0.2, 100.2 0.2)))")
            );
        }
    }

}