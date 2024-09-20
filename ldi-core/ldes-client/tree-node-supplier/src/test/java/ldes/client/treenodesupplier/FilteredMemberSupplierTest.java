package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.h2.H2Properties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.filters.ExactlyOnceFilter;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilteredMemberSupplierTest {

    private final MemberSupplier memberSupplier = mock(MemberSupplier.class);
    private FilteredMemberSupplier filterMemberSupplier;
    private SuppliedMember member;
    private SuppliedMember member2;

    @BeforeEach
    void setup() {
        member = new SuppliedMember("id1", ModelFactory.createDefaultModel());
        member2 = new SuppliedMember("id2", ModelFactory.createDefaultModel());

	    StatePersistence statePersistence = StatePersistence.from(new H2Properties("test"), "test");

	    ExactlyOnceFilter filter = new ExactlyOnceFilter(statePersistence.memberIdRepository(), false);
        filterMemberSupplier = new FilteredMemberSupplier(memberSupplier, filter);
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