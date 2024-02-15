package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import java.util.List;

public interface LdesRelation {
	void addRelation(LdesRelation ldesRelation);
	boolean containsChild(LdesRelation child);
	int countTotalRelations();
	int countChildRelations();
	List<LdesRelation> getRelations();
	String getUri();
	String asString();
}
