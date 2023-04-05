package be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor;

import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmptyKafkaKeyExtractorTest {

    @Test
    void getKeyShouldReturnNull() {
        assertNull(new EmptyKafkaKeyExtractor().getKey(null));
        assertNull(new EmptyKafkaKeyExtractor().getKey(ModelFactory.createDefaultModel()));
    }

}