package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.geosparql.implementation.vocabulary.GeoSPARQL_URI;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.vocabulary.RDF;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.MyWktConverter.COORDINATES;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.MyWktConverter.GEOMETRY;

public class GeoJsonToWktTransformer implements LdiTransformer {

    private final MyWktConverter wktConverter = new MyWktConverter();

    // TODO: 21/03/2023 support geometry
    @Override
    public Model apply(Model model) {

        System.out.println(RDFWriter.source(model).lang(Lang.JSONLD).build().asString());


        List<Statement> geometryNodes = model.listStatements(null, GEOMETRY, (RDFNode) null).toList();
        Map<Statement, Statement> geometries = new HashMap<>(); // key
        geometryNodes.forEach(geometryStatement -> {
            GeoType type = getType(model, geometryStatement.getObject().asResource());
//            Statement coordinatesStatement = model.listStatements(geometryNode.getObject().asResource(), COORDINATES, (RDFNode) null).nextStatement();
            Model geometryModel = getNodeWithChildren(model, geometryStatement);
            final String wktString = wktConverter.getWktFromModel(geometryModel);

            Literal wkt = ResourceFactory.createTypedLiteral(wktString, new WktLiteral());
            Statement newCoords = ResourceFactory.createStatement(geometryStatement.getSubject(), ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry"), wkt);

            geometries.put(geometryStatement, newCoords);
//            modelsToRemove.add(coordinatesModel);
//            model.remove(coordinatesModel);
            model.add(newCoords);
            System.out.println(wktString);
        });


        geometries.keySet().stream().map(k -> getNodeWithChildren(model, k)).forEach(model::remove);

        System.out.println(RDFWriter.source(model).lang(Lang.JSONLD).build().asString());
        return model;
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

    private GeoType getType(Model geojson, Resource geometryId) {
        final List<Statement> typeList = geojson.listStatements(geometryId, RDF.type, (RDFNode) null).toList();
        if (typeList.size() != 1) {
            final String errorMsg = "Could not determine %s of %s".formatted(RDF.type.getURI(), GEOMETRY.getURI());
            throw new IllegalArgumentException(errorMsg);
        }

        final String type = typeList.get(0).getObject().asResource().getURI();
        return GeoType.fromUri(type)
                .orElseThrow(() -> new IllegalArgumentException("Geotype %s not supported".formatted(type)));
    }

    private Resource getGeometryId(Model geojson) {
        // TODO: 22/03/2023 add check there is only one?
        return geojson.listStatements(null, GEOMETRY, (RDFNode) null)
                .toList()
                .stream()
                .map(Statement::getObject)
                .map(RDFNode::asResource)
                .findFirst()
                .orElseThrow();
    }

}
