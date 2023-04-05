package be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaKeyPropertyPathExtractorTest {

    @Test
    void shouldFollowTheLinkedPath() {
        String modelString = """
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
            <https://example.com/hindrances/zones/a> <https://data.com/ns/mobiliteit#Zone.type> 'my-zone-type' .
            """;
        Model model = RDFParser.fromString(modelString).lang(Lang.NQUADS).build().toModel();

        KafkaKeyPropertyPathExtractor extractor =
                KafkaKeyPropertyPathExtractor.from("<https://data.com/ns/mobiliteit#zone>/<https://data.com/ns/mobiliteit#Zone.type>");

        String result = extractor.getKey(model);

        assertEquals("my-zone-type", result);
    }

    @Test
    void name2() {
        String modelString = """
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'my-zone-type' .
            """;
        Model model = RDFParser.fromString(modelString).lang(Lang.NQUADS).build().toModel();

        KafkaKeyPropertyPathExtractor extractor =
                KafkaKeyPropertyPathExtractor.from("<https://data.com/ns/mobiliteit#zone>");

        String result = extractor.getKey(model);

        assertEquals("my-zone-type", result);
    }

    @Test
    void name3() {
        String modelString = """
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
            """;
        Model model = RDFParser.fromString(modelString).lang(Lang.NQUADS).build().toModel();

        KafkaKeyPropertyPathExtractor extractor =
                KafkaKeyPropertyPathExtractor.from("<https://data.com/ns/mobiliteit#zone>");

        String result = extractor.getKey(model);

        assertEquals("https://example.com/hindrances/zones/a", result);
    }

    @Test
    void name4() {
        String modelString = """
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
            """;
        Model model = RDFParser.fromString(modelString).lang(Lang.NQUADS).build().toModel();

        KafkaKeyPropertyPathExtractor extractor =
                KafkaKeyPropertyPathExtractor.from("<https://not-existing>");

        String result = extractor.getKey(model);

        assertEquals("https://example.com/hindrances/zones/a", result);
    }
}