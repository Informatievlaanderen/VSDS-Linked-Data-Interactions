package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import ldes.client.treenoderelationsfetcher.services.LdesRelationWriter;

import java.util.ArrayList;
import java.util.List;

public class LdesStructure implements LdesRelation {
	private final String rootUrl;
	private final List<LdesRelation> relations;

	public LdesStructure(String rootUrl) {
		this.rootUrl = rootUrl;
		this.relations = new ArrayList<>();
	}

	@Override
	public void addRelation(LdesRelation ldesRelation) {
		relations.add(ldesRelation);
	}

	@Override
	public boolean containsChild(LdesRelation child) {
		return relations.contains(child);
	}

	@Override
	public int countTotalRelations() {
		return countChildRelations() + relations.stream().mapToInt(LdesRelation::countTotalRelations).sum();
	}

	@Override
	public int countChildRelations() {
		return relations.size();
	}

	@Override
	public List<LdesRelation> getRelations() {
		return List.copyOf(relations);
	}

	@Override
	public String getUri() {
		return rootUrl;
	}

	/**
	 * @return a tree representation of the ldes structure
	 */
	@Override
	public String asString() {
		final LdesRelationWriter ldesRelationWriter = new LdesRelationWriter();
		return ldesRelationWriter.writeToString(this);
	}

	/**
	 * @return a string representation of the ldes structure, including a first string statement of how many relations
	 * there are included in the structure
	 */
	@Override
	public String toString() {
		return "%s contains a total of %d relations:%n%s".formatted(rootUrl, countTotalRelations(), asString());
	}
}
