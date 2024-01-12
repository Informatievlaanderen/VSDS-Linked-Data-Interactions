package ldes.client.treenodefetcher.domain.valueobjects;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class Constants {
    public static final Resource ANY_RESOURCE = null;
    public static final String W3C_TREE = "https://w3id.org/tree#";
    public static final String LDES_PREFIX = "https://w3id.org/ldes#";
    public static final Property W3ID_TREE_RELATION = createProperty(W3C_TREE, "relation");
    public static final Property W3ID_TREE_MEMBER = createProperty(W3C_TREE, "member");
    public static final Property W3ID_TREE_NODE = createProperty(W3C_TREE, "node");
    public static final Property W3ID_LDES_TIMESTAMP_PATH = createProperty(LDES_PREFIX, "timestampPath");

    private Constants() {
    }
}
