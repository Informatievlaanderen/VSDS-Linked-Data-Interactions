package be.vlaanderen.informatievlaanderen.ldes.ldto;

import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoTransformer;

import java.util.Map;

public interface TransformerConfigurator {
    public LdtoTransformer configure(Map<String, String> properties);
}
