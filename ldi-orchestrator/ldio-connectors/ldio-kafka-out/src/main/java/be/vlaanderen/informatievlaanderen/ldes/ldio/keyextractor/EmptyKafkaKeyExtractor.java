package be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor;

import org.apache.jena.rdf.model.Model;

public class EmptyKafkaKeyExtractor implements KafkaKeyExtractor {

    // TODO: 5/04/2023 test
    @Override
    public String getKey(Model model) {
        return null;
    }

}
