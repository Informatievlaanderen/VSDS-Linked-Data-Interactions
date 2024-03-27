package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import org.apache.jena.rdf.model.Model;

/**
 * This is a decorator for the {@link MemberSupplier} which makes it possible to materialize the version objects to
 * state objects before providing them.
 */
public class VersionMaterialisedMemberSupplier implements MemberSupplier {

    private final MemberSupplier memberSupplier;
    private final VersionMaterialiser versionMaterialiser;

    public VersionMaterialisedMemberSupplier(MemberSupplier memberSupplier, VersionMaterialiser versionMaterialiser) {
        this.memberSupplier = memberSupplier;
        this.versionMaterialiser = versionMaterialiser;
    }

    @Override
    public SuppliedMember get() {
        final SuppliedMember suppliedMember = memberSupplier.get();
        final Model stateObject = versionMaterialiser.transform(suppliedMember.getModel());
        return new SuppliedMember(suppliedMember.getId(), stateObject);
    }

    @Override
    public void destroyState() {
        memberSupplier.destroyState();
    }

    @Override
    public void init() {
        memberSupplier.init();
    }

}
