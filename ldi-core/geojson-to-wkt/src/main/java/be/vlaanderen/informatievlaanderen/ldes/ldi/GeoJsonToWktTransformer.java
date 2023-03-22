package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.MyWktConverter.GEOMETRY;

public class GeoJsonToWktTransformer implements LdiTransformer {

    private final MyWktConverter wktConverter = new MyWktConverter();

    // TODO: 21/03/2023 support geometry
    @Override
    public Model apply(Model model) {
        final List<Statement> geometryStatements = model.listStatements(null, GEOMETRY, (RDFNode) null).toList();
        geometryStatements.forEach(oldGeometryStatement -> {
            final Model geometryModel = createModelWithChildStatements(model, oldGeometryStatement);
            final Statement newGeometryStatement = createNewGeometryStatement(oldGeometryStatement, geometryModel);
            model.remove(createModelWithChildStatements(model, oldGeometryStatement));
            model.add(newGeometryStatement);
        });
        return model;
    }

    private Statement createNewGeometryStatement(Statement oldStatement, Model geometryModel) {
        final String wktString = wktConverter.getWktFromModel(geometryModel);
        Literal wkt = ResourceFactory.createTypedLiteral(wktString, new WktLiteral());
        Property geometry = ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry");
        return ResourceFactory.createStatement(oldStatement.getSubject(), geometry, wkt);
    }

    private Model createModelWithChildStatements(Model model, Statement node) {
        Set<Statement> statements = createModelWithChildStatements(model, node.getObject().asResource(), new HashSet<>());
        statements.add(node);

        Model newModel = ModelFactory.createDefaultModel();
        newModel.add(statements.stream().toList());
        return newModel;
    }

    private Set<Statement> createModelWithChildStatements(Model model, Resource node, Set<Statement> statements) {
        List<Statement> list = model.listStatements(node, null, (RDFNode) null).toList();
        list.forEach(i -> {
            if (i.getObject().isAnon()) {
                createModelWithChildStatements(model, i.getObject().asResource(), statements);
            }
            statements.add(i);
        });
        return statements;
    }

}
