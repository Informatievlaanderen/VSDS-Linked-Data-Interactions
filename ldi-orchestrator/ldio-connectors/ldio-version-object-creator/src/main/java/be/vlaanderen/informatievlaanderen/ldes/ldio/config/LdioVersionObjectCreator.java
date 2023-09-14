package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioProcessor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class LdioVersionObjectCreator extends LdioProcessor {

	private final VersionObjectCreator versionObjectCreator;

	public LdioVersionObjectCreator(PropertyExtractor dateObservedPropertyExtractor, Resource memberType,
			String delimiter, Property generatedAtProperty, Property versionOfProperty) {
		this.versionObjectCreator = new VersionObjectCreator(dateObservedPropertyExtractor, memberType, delimiter,
				generatedAtProperty, versionOfProperty);
	}

	@Override
	public void apply(Model model) {
		next(versionObjectCreator.transform(model));
	}
}
