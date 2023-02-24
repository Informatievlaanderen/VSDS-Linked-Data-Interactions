package ldes.client.treenodeprocessor.fragmentfetcher;

import org.apache.jena.rdf.model.Model;

public class TreeMember {
    private final String memberId;
    private final Model model;

    public TreeMember(String memberId, Model model) {
        this.memberId = memberId;
        this.model = model;
    }

    public String getMemberId() {
        return memberId;
    }

    public Model getModel() {
        return model;
    }
}
