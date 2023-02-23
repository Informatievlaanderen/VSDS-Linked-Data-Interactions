package be.vlaanderen.informatievlaanderen.ldes.client.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.LdesMemberNotFoundException;
import org.apache.jena.rdf.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class LdesFragment {

    public static final String LDES = "https://w3id.org/ldes#";
    public static final Property LDES_VERSION_OF = createProperty(LDES, "versionOfPath");
    public static final Property LDES_TIMESTAMP_PATH = createProperty(LDES, "timestampPath");
    public static final Property TREE_SHAPE = createProperty("https://w3id.org/tree#", "shape");
    private final Model model = ModelFactory.createDefaultModel();
    private String fragmentId;
    private LocalDateTime expirationDate;

    private boolean immutable = false;
    private List<LdesMember> members = new ArrayList<>();
    private final List<String> relations = new ArrayList<>();

    public LdesFragment() {
        this(null, null);
    }

    public LdesFragment(String fragmentId, LocalDateTime expirationDate) {
        this.fragmentId = fragmentId;
        this.expirationDate = expirationDate;
    }

    public Model getModel() {
        return model;
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }

    public List<LdesMember> getMembers() {
        return members;
    }

    public void setMembers(List<LdesMember> members) {
        this.members = members;
    }

    public void addMember(LdesMember member) {
        members.add(member);
    }

    public LdesMember getMember(String memberId) {
        for (LdesMember member : members) {
            if (member.getMemberId().equalsIgnoreCase(memberId)) {
                return member;
            }
        }
        throw new LdesMemberNotFoundException(memberId);
    }

    public List<String> getRelations() {
        return relations;
    }

    public void addRelation(String relation) {
        relations.add(relation);
    }

    public Optional<String> getTimestampPath() {
        return getPropertyValue(LDES_TIMESTAMP_PATH);
    }

    public Optional<String> getVersionOfPath() {
        return getPropertyValue(LDES_VERSION_OF);
    }

    public Optional<String> getShaclShape() {
        return getPropertyValue(TREE_SHAPE);
    }

    protected Optional<String> getPropertyValue(Property property) {
        return model.listStatements(null, property, (Resource) null)
                .nextOptional()
                .map(Statement::getObject)
                .map(RDFNode::asResource)
                .map(Resource::toString);
    }
}
