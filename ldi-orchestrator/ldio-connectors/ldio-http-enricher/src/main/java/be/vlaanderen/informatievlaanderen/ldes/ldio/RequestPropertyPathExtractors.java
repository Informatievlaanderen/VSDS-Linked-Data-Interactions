package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;

public record RequestPropertyPathExtractors(PropertyExtractor urlPropertyPathExtractor,
                                            PropertyExtractor bodyPropertyPathExtractor,
                                            PropertyExtractor headerPropertyPathExtractor,
                                            PropertyExtractor httpMethodPropertyPathExtractor) {
}
