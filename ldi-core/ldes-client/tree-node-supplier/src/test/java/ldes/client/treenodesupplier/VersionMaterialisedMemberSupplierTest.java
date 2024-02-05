package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionMaterialisedMemberSupplierTest {

    @Mock
    private MemberSupplier memberSupplier;
    
    @Mock
    private VersionMaterialiser versionMaterialiser;
    
    @InjectMocks
    private VersionMaterialisedMemberSupplier versionMaterialisedMemberSupplier;

    @Test
    void when_GetIsCalled_then_TheWrappedMemberSupplierIsCalledAndTheResultIsMaterialized() {
        Model versionMember = RDFParser.source("__files/ldes-member-versioned.ttl").toModel();
        Model stateMember = RDFParser.source("__files/ldes-member-unversioned.ttl").toModel();

        when(memberSupplier.get()).thenReturn(new SuppliedMember(versionMember));
        when(versionMaterialiser.transform(versionMember)).thenReturn(stateMember);

        SuppliedMember suppliedMember = versionMaterialisedMemberSupplier.get();
        
        assertThat(suppliedMember.getModel().isIsomorphicWith(stateMember)).isTrue();
    }

    @Test
    void when_DestroyStateIsCalled_then_TheWrappedMemberSupplierIsCalled() {
        versionMaterialisedMemberSupplier.destroyState();

        verify(memberSupplier).destroyState();
    }

}