package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import java.util.List;

/**
 * Representation of a relation in an LDES
 * <br />
 * Is in fact the component interface in the composite pattern
 */
public interface LdesRelation {
	/**
	 * Add a relation to this relation
	 *
	 * @param ldesRelation the relation to add to an existing relation
	 */
	void addRelation(LdesRelation ldesRelation);

	/**
	 * Count all the relations below this relation, including all the relations of their child relations and so on
	 *
	 * @return the total number of relations of and their child relations
	 */
	int countTotalRelations();

	/**
	 * @return the number of relations this relation contains
	 */
	int countChildRelations();

	/**
	 * @return list of all the relations this relation has
	 */
	List<LdesRelation> getRelations();

	/**
	 * @return the uri of this relation
	 */
	String getUri();

	/**
	 * @return string representation of this relation and their child relations
	 */
	String asString();
}
