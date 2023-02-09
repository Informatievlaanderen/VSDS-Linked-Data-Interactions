package be.vlaanderen.informatievlaanderen.ldes.ldto.transformer;

import be.vlaanderen.informatievlaanderen.ldes.ldto.TransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoTransformer;

import java.util.Map;

public class SparqlConstructorConfigurator implements TransformerConfigurator {

    public LdtoTransformer configure(Map<String, String> properties) {
        return new SparqlConstructTransformer(properties);
    }
}
