package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberIdRepository;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExactlyOnceFilterMemberSupplierTest {

    private MemberSupplier memberSupplier = mock(MemberSupplier.class);
    private ExactlyOnceFilterMemberSupplier filterMemberSupplier;
    private SuppliedMember member;
    private SuppliedMember member2;

    @BeforeEach
    void setup() {
        member = new SuppliedMember("id1", ModelFactory.createDefaultModel());
        member2 = new SuppliedMember("id2", ModelFactory.createDefaultModel());

        ExacltyOnceFilter filter = new ExacltyOnceFilter(new InMemoryMemberIdRepository());
        filterMemberSupplier = new ExactlyOnceFilterMemberSupplier(memberSupplier, filter,false);
    }

    @Test
    void when_RepeatedMember_Then_MemberIsSkipped() {
        when(memberSupplier.get()).thenReturn(member, member, member2);

        SuppliedMember actual1 = filterMemberSupplier.get();
        SuppliedMember actual2 = filterMemberSupplier.get();

        assertEquals("id1", actual1.getId());
        assertEquals("id2", actual2.getId());
    }

}