package ldes.client.treenodeprocessor.fragmentfetcher;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public class TreeNode {
    private final String url;
    private final List<String> relations;
    private final List<TreeMember> members;

    public TreeNode(String url, List<String> relations, List<TreeMember> members) {
        this.url = url;
        this.relations = relations;
        this.members = members;
    }

    public String getTreeNodeId() {
        return url;
    }

    public List<String> getRelations() {
        return relations;
    }

    public List<TreeMember> getMembers() {
        return members;
    }
}
