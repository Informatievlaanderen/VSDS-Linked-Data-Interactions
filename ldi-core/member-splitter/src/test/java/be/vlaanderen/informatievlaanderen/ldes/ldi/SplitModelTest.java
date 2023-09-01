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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SplitModelTest {

    @Test
    public void test_generic() {
        Model inputModel = RDFParser.source("generic/input.ttl").toModel();
        List<Model> resultModels = new SplitModel().split(inputModel, "http://schema.org/Movie");
        assertSplit("generic/member1.ttl", resultModels.get(0));
        assertSplit("generic/member2.ttl", resultModels.get(1));
    }

    @Test
    public void test_crowdscan() {
        Model inputModel = RDFParser.source("crowdscan/input.ttl").toModel();
        List<Model> resultModels = new SplitModel().split(inputModel, "http://def.isotc211.org/iso19156/2011/Observation#OM_Observation");
        String string = RDFWriter.source(resultModels.get(1)).lang(Lang.TURTLE).toString();
        System.out.println(string);
        assertSplit("crowdscan/observation3.ttl", resultModels.get(0));
        assertSplit("crowdscan/observation1.ttl", resultModels.get(1));
        assertSplit("crowdscan/observation2.ttl", resultModels.get(2));
    }

    @Test
    public void test_traffic() {
        Model inputModel = RDFParser.source("traffic/input.ttl").toModel();
        List<Model> resultModels = new SplitModel().split(inputModel, "https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");

        assertEquals(10, resultModels.size());
        assertSplit("traffic/measure8.ttl", resultModels.get(0));
        assertSplit("traffic/measure6.ttl", resultModels.get(1));
        assertSplit("traffic/measure10.ttl", resultModels.get(2));
        assertSplit("traffic/measure5.ttl", resultModels.get(3));
        assertSplit("traffic/measure2.ttl", resultModels.get(4));
        assertSplit("traffic/measure9.ttl", resultModels.get(5));
        assertSplit("traffic/measure3.ttl", resultModels.get(6));
        assertSplit("traffic/measure1.ttl", resultModels.get(7));
        assertSplit("traffic/measure7.ttl", resultModels.get(8));
        assertSplit("traffic/measure4.ttl", resultModels.get(9));
    }

    @Disabled
    @Test
    public void test_traffic_old() {
        Model inputModel = RDFParser.source("traffic/input.ttl").toModel();
        Map<String, Model> resultMap = new SplitModel().splitToMap(inputModel, "https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");


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


    @Disabled
    @Test
    public void recursive() {
        Model inputModel = RDFParser.source("traffic/measure_temp.ttl").toModel();
        Map<String, Model> resultMap = new SplitModel().splitToMap(inputModel, "https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");

        List<Model> list = resultMap
                .values()
                .stream()
                .peek(model -> {
                    List<Model> allModels = new ArrayList<>();
                    List<Model> currentModels = new ArrayList<>();
//                    do {
//                        currentModels.addAll(getObjects(model, resultMap));
//                    } while (currentModels)

                })
                .toList();


        resultMap.values().forEach(subject -> {
            List<Model> modelsToAddToMember = getObjects(subject, resultMap);
            List<Model> second = modelsToAddToMember.stream().flatMap(model -> getObjects(model, resultMap).stream()).toList();
            List<Model> third = second.stream().flatMap(model -> getObjects(model, resultMap).stream()).toList();
            System.out.println("subject: " + subject);
            System.out.println("objects: " + modelsToAddToMember.size());
            System.out.println("nested objects: " + second.size());
            System.out.println("nested x2 objects: " + third.size());
        });
    }

    private static List<Model> getObjects(Model subject, Map<String, Model> resultMap) {
        return subject
                .listObjects()
                .filterKeep(RDFNode::isResource)
                .mapWith(RDFNode::toString)
                .mapWith(resultMap::get)
                .filterKeep(Objects::nonNull)
                .toList();
    }

    /**
     * select members
     * voor elk subject filter objects
     * voor elk object haal model uit resultmap
     * voor elk model uit resultmap doe stap hierboven
     */
    @Disabled
    @Test
    public void test_trafficzzzzzzzzzzzzzzzzzzzzz() {

        Model inputModel = RDFParser.source("traffic/input.ttl").toModel();
        Map<String, Model> resultMap = new SplitModel().splitToMap(inputModel, "https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");


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
