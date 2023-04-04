package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathLib;
import org.apache.jena.sparql.path.PathParser;

public class PlayBox {


    public static void main(String[] args) {
        String myModel = """
            <https://example.com/mobility-hindrances/10034919/29797> <https://data.vlaanderen.be/ns/mobiliteit#zone> <https://example.com/mobility-hindrances/10034919/zones/a> .
            <https://example.com/mobility-hindrances/10034919/zones/a> <https://data.vlaanderen.be/ns/mobiliteit#Zone.type> 'my-zone-type' .
            """;
        Model model = RDFParser.fromString(myModel).lang(Lang.NQUADS).build().toModel();

        Path path = PathParser.parse("<https://data.vlaanderen.be/ns/mobiliteit#zone>/<https://data.vlaanderen.be/ns/mobiliteit#Zone.type>", PrefixMapping.Standard) ;
        String uri = "http://example/ns#myType" ;
        PathLib.install(uri, path);


        System.out.println(
                model.listStatements(null, ResourceFactory.createProperty(uri), (RDFNode) null).toList());

        System.out.println(
                model.listStatements(null, null, ResourceFactory.createProperty(uri)).toList());
        System.out.println(
                model.listStatements(ResourceFactory.createProperty(uri), null, (RDFNode) null).toList());

        final Query query = QueryFactory.create("SELECT ?t where { ?x <http://example/ns#myType> ?t }");
        try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
            ResultSet resultSet = queryExecution.execSelect();
            RDFNode node = resultSet.next().get("t");
            System.out.println(node.toString());
        }

    }

}
