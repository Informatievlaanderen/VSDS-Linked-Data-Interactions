package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SplitModelSanderTest {

    @Test
    public void testSplitter() {
        Model inputModel = RDFParser.source("input.ttl").toModel();
        List<Model> resultModels = new SplitModelSander().split(inputModel);
        assertSplit("member1.ttl", resultModels.get(0));
        assertSplit("member2.ttl", resultModels.get(1));
    }

    void assertSplit(String file, Model actualModel) {
        Model expectedModel = RDFParser.source(file).toModel();
        assertTrue(expectedModel.isIsomorphicWith(actualModel));
    }

}
