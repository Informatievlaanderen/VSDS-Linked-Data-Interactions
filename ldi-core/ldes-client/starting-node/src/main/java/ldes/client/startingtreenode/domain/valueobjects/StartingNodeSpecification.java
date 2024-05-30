package ldes.client.startingtreenode.domain.valueobjects;

import org.apache.jena.rdf.model.Model;

import java.util.function.Predicate;

/**
 * Interface to declare the specification of the provided starting node of an LDES
 */
public interface StartingNodeSpecification extends Predicate<Model> {

	StartingTreeNode extractStartingNode(Model model);
}
