package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SplitModelTest {

    @Test
    public void testSplitter() {
        Model inputModel = RDFParser.source("input.ttl").toModel();
        Map<String, Model> split = new SplitModel(inputModel).split();
        List<Model> resultModels = split.values().stream().toList();
        assertSplit("member1.ttl", resultModels.get(0));
        assertSplit("member2.ttl", resultModels.get(1));
    }

    void assertSplit(String file, Model actualModel) {
        Model expectedModel = RDFParser.source(file).toModel();
        assertTrue(expectedModel.isIsomorphicWith(actualModel));
    }

}
