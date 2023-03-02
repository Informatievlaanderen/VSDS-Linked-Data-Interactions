package ldes.client.startingtreenode.domain.valueobjects;

import org.apache.jena.rdf.model.Model;

import java.util.function.Predicate;

public interface StartingNodeSpecification extends Predicate<Model> {

    TreeNode extractStartingNode(Model model);
}
