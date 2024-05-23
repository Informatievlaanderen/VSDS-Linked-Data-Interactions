package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.ChangeDetectionFilter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdioChangeDetectionFilter extends LdioTransformer {
	public static final String NAME = "Ldio:ChangeDetectionFilter";
	private static final Logger log = LoggerFactory.getLogger(LdioChangeDetectionFilter.class);
	private final ChangeDetectionFilter changeDetectionFilter;

	public LdioChangeDetectionFilter(ChangeDetectionFilter changeDetectionFilter) {
		this.changeDetectionFilter = changeDetectionFilter;
	}

	@Override
	public void apply(Model model) {
		final Model filteredModel = changeDetectionFilter.transform(model);
		if (filteredModel.isEmpty()) {
			log.atInfo().log("State member {} ignored: no changes detected", extractSubjectFromModel(model));
			return;
		}
		this.next(filteredModel);
	}

	public void shutdown() {
		changeDetectionFilter.destroyState();
	}

	private String extractSubjectFromModel(Model model) {
		return model.listSubjects().filterDrop(Resource::isAnon).next().getURI();
	}
}
