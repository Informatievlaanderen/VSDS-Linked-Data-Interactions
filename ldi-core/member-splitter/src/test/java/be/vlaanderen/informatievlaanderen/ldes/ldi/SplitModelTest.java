package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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
import static org.junit.jupiter.api.Assertions.*;

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
        List<Model> resultModels = new SplitModel().split(inputModel);
        Map<String, Model> resultMap = new SplitModel().splitToMap(inputModel);
        String string = RDFWriter.source(resultModels.get(0)).lang(Lang.TURTLE).toString();
//        System.out.println(string);


        Property property = createProperty("https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");
        List<Model> members =
                resultModels
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
            String s = RDFWriter.source(baseModel).lang(Lang.TURTLE).asString();
            System.out.println("------ begin ----------");
            System.out.println(s);
            System.out.println("------ end ----------");
        });

        Model model = resultModels.get(23);
        Model baseModel = resultModels.get(10);
        baseModel.add(model);
        RDFWriter.source(baseModel).lang(Lang.TURTLE).asString();

        List<Model> list = baseModel.listObjects().filterKeep(RDFNode::isResource).mapWith(RDFNode::toString).mapWith(resultMap::get).filterKeep(Objects::nonNull).toList();
        list.forEach(baseModel::add);
//resultMap.get(rdfNode.toString());
//resultModels.stream().filter(model -> model.listSubjects())
        RDFWriter.source(baseModel).lang(Lang.TURTLE).asString();
//resultModels.stream().m

        assertSplit("traffic/measure1.ttl", resultModels.get(0));
    }

    void assertSplit(String file, Model actualModel) {
        Model expectedModel = RDFParser.source(file).toModel();
        assertTrue(expectedModel.isIsomorphicWith(actualModel));
    }

}
