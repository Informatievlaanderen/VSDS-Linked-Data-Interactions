package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.base.AbstractIRI;

import java.util.HashSet;
import java.util.Set;

public class ModelSubjectsExtractor {
	private ModelSubjectsExtractor() {

	}

	/**
	 * Returns all subjects ('real' URIs) present in the model.
	 *
	 * @param model A graph
	 * @return A set of subject URIs.
	 */
	public static Set<Resource> extractSubjects(Model model) {
		Set<Resource> entityIds = new HashSet<>();

		model.subjects().forEach((Resource subject) -> {
			if (subject instanceof AbstractIRI) {
				entityIds.add(subject);
			}
		});

		return entityIds;
	}

}
