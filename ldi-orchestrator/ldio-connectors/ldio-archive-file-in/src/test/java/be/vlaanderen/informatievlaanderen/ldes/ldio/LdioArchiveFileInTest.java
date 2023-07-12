package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import static org.junit.jupiter.api.Assertions.*;

class LdioArchiveFileInTest {

    public static void main(String[] args) {
        Model model = RDFParser.source("2022-04-20-08-27-27-110000000.nq").lang(null).toModel();
        System.out.println(model.listStatements().toList());
    }

}