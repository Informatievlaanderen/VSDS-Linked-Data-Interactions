package be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KafkaKeyPropertyPathExtractorTest {

    @Test
    void shouldReturnLiteralStringWhenLinkedPath() {
        String modelString = """
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
            <https://example.com/hindrances/zones/a> <https://data.com/ns/mobiliteit#Zone.type> 'my-zone-type' .
            """;
        Model model = RDFParser.fromString(modelString).lang(Lang.NQUADS).build().toModel();

        KafkaKeyPropertyPathExtractor extractor =
                new KafkaKeyPropertyPathExtractor("<https://data.com/ns/mobiliteit#zone>/<https://data.com/ns/mobiliteit#Zone.type>");

        String result = extractor.getKey(model);

        assertEquals("my-zone-type", result);
    }

    @Test
    void shouldReturnLiteralStringWhenSimplePath() {
        String modelString = """
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'my-zone-type' .
            """;
        Model model = RDFParser.fromString(modelString).lang(Lang.NQUADS).build().toModel();

        KafkaKeyPropertyPathExtractor extractor =
                new KafkaKeyPropertyPathExtractor("<https://data.com/ns/mobiliteit#zone>");

        String result = extractor.getKey(model);

        assertEquals("my-zone-type", result);
    }

    @Test
    void shouldReturnAnyResultStringWhenMultipleResults() {
        String modelString = """
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'my-zone-type' .
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'other-zone-type' .
            """;
        Model model = RDFParser.fromString(modelString).lang(Lang.NQUADS).build().toModel();

        KafkaKeyPropertyPathExtractor extractor =
                new KafkaKeyPropertyPathExtractor("<https://data.com/ns/mobiliteit#zone>");

        String result = extractor.getKey(model);

        assertTrue(result.contains("-zone-type"));
    }

    @Test
    void shouldReturnUriAsStringWhenObjectIsResource() {
        String modelString = """
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
            """;
        Model model = RDFParser.fromString(modelString).lang(Lang.NQUADS).build().toModel();

        KafkaKeyPropertyPathExtractor extractor =
                new KafkaKeyPropertyPathExtractor("<https://data.com/ns/mobiliteit#zone>");

        String result = extractor.getKey(model);

        assertEquals("https://example.com/hindrances/zones/a", result);
    }

    @Test
    void shouldReturnNullIfPathIsNotFound() {
        String modelString = """
            <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
            """;
        Model model = RDFParser.fromString(modelString).lang(Lang.NQUADS).build().toModel();

        KafkaKeyPropertyPathExtractor extractor =
                new KafkaKeyPropertyPathExtractor("<https://not-existing>");

        assertNull(extractor.getKey(model));
    }
}