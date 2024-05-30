package ldes.client.treenoderelationsfetcher.services;

import ldes.client.treenoderelationsfetcher.domain.valueobjects.LdesRelation;

import java.util.Comparator;
import java.util.List;

/**
 * Writer utility class that is responsible for writing an LDES relation into a tree kind string representation
 */
public class LdesRelationWriter {
	private static final String RELATION_PREFIX = "+- ";
	private static final String RELATIONS_CONNECTION_STRING = "|  ";
	private static final String SPACER = "   ";

	public String writeToString(LdesRelation ldesRelation) {
		return writeToStringWithChildPrefix(ldesRelation, RELATION_PREFIX);
	}

	private String writeToStringWithChildPrefix(LdesRelation ldesRelation, String prefix) {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(ldesRelation.getUri()).append("\n");

		final List<LdesRelation> relations = ldesRelation.getRelations()
				.stream()
				.sorted(Comparator.comparing(LdesRelation::getUri))
				.toList();
		relations.stream()
				.map(relation -> prefix + writeToStringWithChildPrefix(relation, determineChildPrefix(relations, relation, prefix)))
				.forEach(stringBuilder::append);

		return stringBuilder.toString();
	}

	private String determineChildPrefix(List<LdesRelation> parentRelations, LdesRelation relation, String prefix) {
		final StringBuilder stringBuilder = new StringBuilder();

		final String childStartPrefix = prefix.replace(RELATION_PREFIX, "");
		stringBuilder.append(childStartPrefix);

		if (shouldHaveTrailingConnectionChar(parentRelations, relation)) {
			stringBuilder.append(RELATIONS_CONNECTION_STRING);
		} else {
			stringBuilder.append(SPACER);
		}

		stringBuilder.append(RELATION_PREFIX);

		return stringBuilder.toString();
	}

	private boolean shouldHaveTrailingConnectionChar(List<LdesRelation> parentRelations, LdesRelation relation) {
		return parentRelations.size() > 1 && parentRelations.indexOf(relation) != parentRelations.size() - 1;
	}

}
