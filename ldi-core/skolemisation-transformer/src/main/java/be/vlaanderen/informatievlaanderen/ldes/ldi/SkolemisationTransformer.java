package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToOneTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.SkolemizedModel;
import org.apache.jena.rdf.model.Model;

public class SkolemisationTransformer implements LdiOneToOneTransformer {
	public static final String SKOLEM_URI = "/.well-known/genid/";
	private final String skolemUriTemplate;

	public SkolemisationTransformer(String skolemDomain) {
		this.skolemUriTemplate = skolemDomain + SKOLEM_URI + "%s";
	}

	@Override
	public Model transform(Model model) {
		return new SkolemizedModel(skolemUriTemplate, model).getModel();
	}
}
