package ldes.client.treenodeprocessor.fragmentfetcher;

import org.apache.jena.graph.TripleBoundary;
import org.apache.jena.rdf.model.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class TreeNodeProcessor {

    TreeNodeFetcher treeNodeFetcher = new TreeNodeFetcher();
    protected static final Resource ANY_RESOURCE = null;
    protected static final Property ANY_PROPERTY = null;
    public static final String W3C_TREE = "https://w3id.org/tree#";
    public static final Property W3ID_TREE_RELATION = createProperty(W3C_TREE, "relation");
    public static final Property W3ID_TREE_MEMBER = createProperty(W3C_TREE, "member");
    public static final Property W3ID_TREE_NODE = createProperty(W3C_TREE, "node");
    private final ModelExtract modelExtract = new ModelExtract(new StatementTripleBoundary(TripleBoundary.stopNowhere));


    public TreeNode process(String treeNodeUrl) {
        Model model = treeNodeFetcher.fetchFragment(treeNodeUrl);
        List<String> strings = extractRelations(model).map(relationStatement -> relationStatement.getResource().getProperty(W3ID_TREE_NODE).getResource().toString()).toList();
        List<TreeMember> members = extractMembers(model).map(memberStatement -> processMember(model, memberStatement)).collect(Collectors.toList());

        return new TreeNode(treeNodeUrl, strings, members);
    }


    private Stream<Statement> extractMembers(Model fragmentModel) {
        StmtIterator memberIterator = fragmentModel.listStatements(ANY_RESOURCE, W3ID_TREE_MEMBER, ANY_RESOURCE);

        return Stream.iterate(memberIterator, Iterator::hasNext, UnaryOperator.identity())
                .map(Iterator::next);
    }

    private TreeMember processMember(Model fragmentModel, Statement memberStatement) {
        Model memberModel = modelExtract.extract(memberStatement.getObject().asResource(), fragmentModel);
        String id = memberStatement.getObject().toString();
        memberModel.add(memberStatement);

        // Add reverse properties
        Set<Statement> otherLdesMembers = fragmentModel
                .listStatements(memberStatement.getSubject(), W3ID_TREE_MEMBER, ANY_RESOURCE).toSet().stream()
                .filter(statement -> !memberStatement.equals(statement)).collect(Collectors.toSet());

        fragmentModel.listStatements(ANY_RESOURCE, ANY_PROPERTY, memberStatement.getResource())
                .filterKeep(statement -> statement.getSubject().isURIResource()).filterDrop(memberStatement::equals)
                .forEach(statement -> {
                    Model reversePropertyModel = modelExtract.extract(statement.getSubject(), fragmentModel);
                    List<Statement> otherMembers = reversePropertyModel
                            .listStatements(statement.getSubject(), statement.getPredicate(), ANY_RESOURCE).toList();
                    otherLdesMembers.forEach(otherLdesMember -> reversePropertyModel
                            .listStatements(ANY_RESOURCE, ANY_PROPERTY, otherLdesMember.getResource()).toList());
                    otherMembers.forEach(otherMember -> reversePropertyModel
                            .remove(modelExtract.extract(otherMember.getResource(), reversePropertyModel)));

                    memberModel.add(reversePropertyModel);
                });

        return new TreeMember(id, memberModel);
    }

    private Stream<Statement> extractRelations(Model fragmentModel) {
        return Stream.iterate(fragmentModel.listStatements(ANY_RESOURCE, W3ID_TREE_RELATION, ANY_RESOURCE),
                Iterator::hasNext, UnaryOperator.identity()).map(Iterator::next);
    }


}
