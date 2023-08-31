package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SplitModelTest {

    @Test
    public void test_generic() {
        Model inputModel = RDFParser.source("generic/input.ttl").toModel();
        List<Model> resultModels = new SplitModel().split(inputModel);
        assertSplit("generic/member1.ttl", resultModels.get(0));
        assertSplit("generic/member2.ttl", resultModels.get(1));
    }

    @Test
    public void test_crowdscan() {
        Model inputModel = RDFParser.source("crowdscan/input.ttl").toModel();
        List<Model> resultModels = new SplitModel().split(inputModel);
        String string = RDFWriter.source(resultModels.get(1)).lang(Lang.TURTLE).toString();
        System.out.println(string);
        assertSplit("crowdscan/observation3.ttl", resultModels.get(0));
        assertSplit("crowdscan/observation1.ttl", resultModels.get(1));
        assertSplit("crowdscan/observation2.ttl", resultModels.get(2));
    }

    @Test
    public void test_traffic() {
        Model inputModel = RDFParser.source("traffic/input.ttl").toModel();
        Map<String, Model> resultMap = new SplitModel().splitToMap(inputModel);


        Property property = createProperty("https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");
        List<Model> members =
                resultMap.values()
                        .stream()
                        .filter(model -> !model.listSubjectsWithProperty(RDF.type, property).toList().isEmpty())
                        .toList();

        members.forEach(baseModel -> {
            List<Model> list =
                    baseModel
                            .listObjects()
                            .filterKeep(RDFNode::isResource)
                            .mapWith(RDFNode::toString)
                            .mapWith(resultMap::get)
                            .filterKeep(Objects::nonNull)
                            .toList();
            list.forEach(baseModel::add);
//            String s = RDFWriter.source(baseModel).lang(Lang.TURTLE).asString();
//            System.out.println("------ begin ----------");
//            System.out.println(s);
//            System.out.println("------ end ----------");
        });

        String s = RDFWriter.source(members.get(2)).lang(Lang.TURTLE).asString();
        System.out.println(s);

        assertSplit("traffic/measure1.ttl", members.get(2));
    }


    @Test
    public void recursive() {
        Model inputModel = RDFParser.source("traffic/input.ttl").toModel();
        Map<String, Model> resultMap = new SplitModel().splitToMap(inputModel);


        resultMap.values().forEach(member -> {
            List<Model> modelsToAddToMember = member
                    .listObjects()
                    .filterKeep(RDFNode::isResource)
                    .mapWith(RDFNode::toString)
                    .mapWith(resultMap::get)
                    .filterKeep(Objects::nonNull)
                    .toList();
        });
    }

    /**
     * select members
     * voor elk subject filter objects
     * voor elk object haal model uit resultmap
     * voor elk model uit resultmap doe stap hierboven
     */
    @Test
    public void test_trafficzzzzzzzzzzzzzzzzzzzzz() {

        Model inputModel = RDFParser.source("traffic/input.ttl").toModel();
        Map<String, Model> resultMap = new SplitModel().splitToMap(inputModel);


        resultMap.values().forEach(member -> {
            List<Model> modelsToAddToMember = member
                    .listObjects()
                    .filterKeep(RDFNode::isResource)
                    .mapWith(RDFNode::toString)
                    .mapWith(resultMap::get)
                    .filterKeep(Objects::nonNull)
                    .toList();
        });

        ///////////////////////////////

        Property property = createProperty("https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");
        List<Model> members =
                resultMap.values()
                        .stream()
                        .filter(model -> !model.listSubjectsWithProperty(RDF.type, property).toList().isEmpty())
                        .toList();

        members.forEach(baseModel -> {
            List<Model> list =
                    baseModel
                            .listObjects()
                            .filterKeep(RDFNode::isResource)
                            .mapWith(RDFNode::toString)
                            .mapWith(resultMap::get)
                            .filterKeep(Objects::nonNull)
                            .toList();
            list.forEach(baseModel::add);
//            String s = RDFWriter.source(baseModel).lang(Lang.TURTLE).asString();
//            System.out.println("------ begin ----------");
//            System.out.println(s);
//            System.out.println("------ end ----------");
        });

        String s = RDFWriter.source(members.get(2)).lang(Lang.TURTLE).asString();
        System.out.println(s);

        assertSplit("traffic/measure1.ttl", members.get(2));
    }

    void assertSplit(String file, Model actualModel) {
        Model expectedModel = RDFParser.source(file).toModel();
        assertTrue(expectedModel.isIsomorphicWith(actualModel));
    }

}
