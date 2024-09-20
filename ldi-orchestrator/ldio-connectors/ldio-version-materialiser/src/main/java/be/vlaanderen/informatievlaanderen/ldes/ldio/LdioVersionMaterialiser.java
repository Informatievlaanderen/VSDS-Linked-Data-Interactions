package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

public class LdioVersionMaterialiser extends LdioTransformer {
	public static final String NAME = "Ldio:VersionMaterialiser";
	private final VersionMaterialiser versionMaterialiser;

	public LdioVersionMaterialiser(Property versionOfProperty, boolean restrictToMembers) {
		this.versionMaterialiser = new VersionMaterialiser(versionOfProperty, restrictToMembers);
	}

	@Override
	public void apply(Model model) {
		this.next(versionMaterialiser.transform(model));
	}
}
