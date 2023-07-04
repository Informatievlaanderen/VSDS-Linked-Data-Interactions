package be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy;

import org.apache.jena.rdf.model.Model;

public interface ConversionStrategy {
	String getFileExtension();

	String getContent(Model model);
}
