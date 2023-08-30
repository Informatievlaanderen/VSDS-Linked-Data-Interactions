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
    public void testSplitterSander() {
        Model inputModel = RDFParser.source("input.ttl").toModel();
        List<Model> resultModels = new SplitModelSander().split(inputModel);
        assertSplit("member1.ttl", resultModels.get(0));
        assertSplit("member2.ttl", resultModels.get(1));
    }

    @Test
    public void testSplitterTom() {
        Model inputModel = RDFParser.source("input.ttl").toModel();
        List<Model> resultModels = new SplitModelTom().split(inputModel);
        String string = RDFWriter.source(resultModels.get(0)).lang(Lang.TURTLE).toString();
        System.out.println(string);
        assertSplit("member1.ttl", resultModels.get(0));
        assertSplit("member2.ttl", resultModels.get(1));
    }

    void assertSplit(String file, Model actualModel) {
        Model expectedModel = RDFParser.source(file).toModel();
        assertTrue(expectedModel.isIsomorphicWith(actualModel));
    }

}
