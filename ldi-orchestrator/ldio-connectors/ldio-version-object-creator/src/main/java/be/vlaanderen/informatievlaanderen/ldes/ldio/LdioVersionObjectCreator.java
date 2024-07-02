package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

public class LdioVersionObjectCreator extends LdioTransformer {
	public static final String NAME = "Ldio:VersionObjectCreator";
	private final VersionObjectCreator versionObjectCreator;

	public LdioVersionObjectCreator(PropertyExtractor dateObservedPropertyExtractor, List<Resource> memberTypes,
	                                String delimiter, Property generatedAtProperty, Property versionOfProperty) {
		this.versionObjectCreator = new VersionObjectCreator(dateObservedPropertyExtractor, memberTypes, delimiter,
				generatedAtProperty, versionOfProperty);
	}

	@Override
	public void apply(Model model) {
		next(versionObjectCreator.transform(model));
	}
}
