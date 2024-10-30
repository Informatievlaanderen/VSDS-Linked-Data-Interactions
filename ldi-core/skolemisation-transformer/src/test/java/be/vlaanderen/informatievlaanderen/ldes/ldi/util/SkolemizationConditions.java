package be.vlaanderen.informatievlaanderen.ldes.ldi.util;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.assertj.core.api.Condition;

public class SkolemizationConditions {
	public static Condition<Model> noBlankNodes() {
		return new Condition<>(model -> model.listObjects().toList().stream().noneMatch(RDFNode::isAnon), "Model cannot have blank nodes");
	}

	public static Condition<Model> skolemizedSubjectsWithPrefix(int count, String prefix) {
		return new Condition<>(actual -> actual.listSubjects()
				.filterKeep(object -> object.toString().contains(prefix))
				.toList()
				.size() == count,"Expected to find %d subjects with skolemized id", count);
	}

	public static Condition<Model> skolemizedObjectsWithPrefix(int count, String prefix) {
		return new Condition<>(actual -> actual.listObjects()
				.filterKeep(object -> object.toString().contains(prefix))
				.toList()
				.size() == count,"Expected to find %d objects with skolemized id", count);
	}
}
