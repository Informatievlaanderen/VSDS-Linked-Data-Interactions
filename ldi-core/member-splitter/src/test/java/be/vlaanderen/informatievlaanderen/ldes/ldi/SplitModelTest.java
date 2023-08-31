package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    void assertSplit(String file, Model actualModel) {
        Model expectedModel = RDFParser.source(file).toModel();
        assertTrue(expectedModel.isIsomorphicWith(actualModel));
    }

}
